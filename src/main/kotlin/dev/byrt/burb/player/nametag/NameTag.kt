package dev.byrt.burb.player.nametag

import net.kyori.adventure.text.Component
import org.bukkit.entity.Display
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.util.Transformation
import org.joml.Quaternionf
import org.joml.Vector3f
import java.lang.AutoCloseable
import java.util.UUID

/**
 * A set of text entities that make up a player's name tag.
 */
class NameTag(player: Player, size: Int): AutoCloseable {

    private companion object {
        /** The distance between the last line and the top of the player's head. */
        private const val BASE_HEIGHT = 0.25f

        /**
         * The space between each line.
         */
        private const val LINE_HEIGHT = 0.3f
    }

    private var entities = buildList {
        repeat(size) { i ->
            player.world.spawn(player.location, TextDisplay::class.java) {
                it.isPersistent = false
                it.billboard = Display.Billboard.CENTER
                it.transformation = Transformation(
                    Vector3f(0f, BASE_HEIGHT + LINE_HEIGHT * (size - i - 1), 0f),
                    Quaternionf(),
                    Vector3f(1f, 1f, 1f),
                    Quaternionf(),
                )
                player.addPassenger(it)
                add(it)
            }
        }
    }

    /** Gets a line's display entity, indexed top-to-bottom. */
    operator fun get(index: Int): TextDisplay = entities[index]

    /** Sets the text for a line, indexed top-to-bottom. */
    operator fun set(index: Int, value: Component) = entities[index].text(value)

    /**
     * Removes every entity.
     */
    override fun close() {
        entities.forEach { it.remove() }
        entities = emptyList() // destroy refs just in case
    }
}


abstract class NameTagProvider: AutoCloseable, Listener {

    protected val nametags: Map<UUID, NameTag>
        field = mutableMapOf<UUID, NameTag>()

    abstract val lines: Int

    abstract fun setUpForPlayer(player: Player, nametag: NameTag)

    public fun create(player: Player) {
        nametags.remove(player.uniqueId)?.close()

        val tag = NameTag(player, lines)
        setUpForPlayer(player, tag)
        nametags[player.uniqueId] = tag
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public fun onJoin(e: PlayerJoinEvent) {
        create(e.player)
    }

    @EventHandler
    public fun onLeave(e: PlayerQuitEvent) {
        nametags.remove(e.player.uniqueId)?.close()
    }

    override fun close() {
        nametags.values.forEach { it.close() }
        nametags.clear()
    }
}