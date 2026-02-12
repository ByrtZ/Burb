package dev.byrt.burb.player

import dev.byrt.burb.util.ReflectUtil
import dev.byrt.burb.util.extension.nms
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelOutboundHandlerAdapter
import io.netty.channel.ChannelPromise
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBundlePacket
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializer
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import kotlin.experimental.or

/**
 * Intercepts entity data packets to override their glowing state.
 */
class GlowingInterceptor(val glowingManager: PlayerGlowing, val player: ServerPlayer) :
    ChannelOutboundHandlerAdapter() {

    private companion object {
        private val entityDataAccessor = ReflectUtil.getStatic<Entity, EntityDataAccessor<Byte>>("DATA_SHARED_FLAGS_ID")
        private val glowingFlag = ReflectUtil.getStatic<Entity, Int>("FLAG_GLOWING")
    }

    private fun newPacket(packet: Any): Packet<in ClientGamePacketListener>? = when (val msg = packet) {
        is ClientboundBundlePacket -> ClientboundBundlePacket(
            msg.subPackets().map { newPacket(it) ?: it } as Iterable<Packet<in ClientGamePacketListener>>)

        is ClientboundSetEntityDataPacket -> {
            val target = player.level().server.playerList.players
                .find { it.id == msg.id }
                ?.takeIf { glowingManager.glowingGroups.values.any { group -> it.uuid in group && player.uuid in group } }
                ?: return null


            val newData = buildList {
                var hasSeenData = false
                msg.packedItems.forEach { item ->
                    if (item.id != entityDataAccessor.id) {
                        add(item)
                        return@forEach
                    }
                    hasSeenData = true

                    add(
                        SynchedEntityData.DataValue(
                            item.id,
                            item.serializer as EntityDataSerializer<Byte>,
                            item.value as Byte or (1 shl glowingFlag).toByte()
                        )
                    )
                }

                if (!hasSeenData) add(
                    SynchedEntityData.DataValue(
                        entityDataAccessor.id,
                        entityDataAccessor.serializer,
                        target.entityData.get(entityDataAccessor) or (1 shl glowingFlag).toByte()
                    )
                )
            }
            ClientboundSetEntityDataPacket(msg.id, newData)
        }

        else -> null
    }

    override fun write(ctx: ChannelHandlerContext, msg: Any, promise: ChannelPromise) {
        ctx.write(newPacket(msg) ?: msg, promise)
    }
}

/**
 * Manages player glowing groups. A glowing group is a collection of players that are all glowing to each other.
 */
object PlayerGlowing : Listener {

    val glowingGroups: Map<String, Set<UUID>>
        field = mutableMapOf<String, MutableSet<UUID>>()

    @EventHandler
    fun playerConnect(e: PlayerJoinEvent) {
        val pipeline = e.player.nms.connection.connection.channel.pipeline()
        pipeline.addAfter("unbundler", "burb", GlowingInterceptor(this, e.player.nms))
    }

    @EventHandler
    fun playerDisconnect(e: PlayerQuitEvent) {
        glowingGroups.forEach {
            it.value -= e.player.uniqueId
        }
    }

    private fun syncData(player: Player, viewers: Set<UUID>) {
        viewers.forEach { r ->
            val receiver = Bukkit.getPlayer(r) ?: return@forEach
            receiver.nms.connection.send(
                ClientboundSetEntityDataPacket(player.nms.id, player.nms.entityData.packAll())
            )

            player.nms.connection.send(
                ClientboundSetEntityDataPacket(receiver.nms.id, receiver.nms.entityData.packAll())
            )
        }
    }

    /**
     * Adds a player to a glowing group.
     */
    fun addToGlowingGroup(name: String, player: Player) {
        val group = glowingGroups.computeIfAbsent(name) { mutableSetOf() }
        if (!group.add(player.uniqueId)) return
        syncData(player, group)
    }

    /**
     * Removes a player from a glowing group.
     */
    fun removeFromGlowingGroup(name: String, player: Player) {
        val group = glowingGroups[name] ?: return
        if (!group.remove(player.uniqueId)) return
        syncData(player, group)
    }
}