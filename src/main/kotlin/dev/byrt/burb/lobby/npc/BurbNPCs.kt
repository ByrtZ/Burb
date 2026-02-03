package dev.byrt.burb.lobby.npc

import com.destroystokyo.paper.profile.ProfileProperty
import dev.byrt.burb.plugin
import dev.byrt.burb.text.Formatting
import dev.byrt.burb.util.Keys
import io.papermc.paper.datacomponent.item.ResolvableProfile
import org.bukkit.Bukkit
import org.bukkit.entity.Mannequin
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import kotlin.random.Random

@Suppress("unstableApiUsage")
object BurbNPCs {
    private const val FALLBACK_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2FlMWNiMzA5NzBlMDAwYmE1NjI4Mjc3MTQ3OGNkZDIyOTI4YjQxYWVjNTZkNTMzMzc0OWYxYjIwMTIxMDljZjUifX19"
    fun playNPCDialogue(player: Player, npcSoundSet: BurbNPCSoundSet) {
        val numberTalkSounds = Random.nextInt(1, 5)
        object : BukkitRunnable() {
            var i = 1
            override fun run() {
                if(i <= numberTalkSounds) {
                    object : BukkitRunnable() {
                        override fun run() {
                            player.playSound(npcSoundSet.baseSound)
                            if(listOf(0, 0, 0, 1).random() == 0) {
                                player.playSound(npcSoundSet.hitSound)
                            }
                        }
                    }.runTaskLater(plugin, i * Random.nextInt(2, 5).toLong())
                } else {
                    this.cancel()
                }
                i++
            }
        }.runTaskTimer(plugin, 0L, 1L)
    }

    fun spawnAllNPCs() {
        for(npc in BurbNPC.entries) {
            spawnNPC(npc)
        }
    }

    fun spawnNPC(npc: BurbNPC) {
        val mannequin = npc.npcLocation.world.spawn(npc.npcLocation, Mannequin::class.java).apply {
            customName(Formatting.allTags.deserialize("<!i>${npc.npcNameColour}${npc.npcName}"))
            description = Formatting.allTags.deserialize(npc.npcDescription)
            isCustomNameVisible = true
            isInvulnerable = true
            isImmovable = true
            setGravity(false)
            pose = npc.npcPose
            persistentDataContainer.set(Keys.LOBBY_NPC, PersistentDataType.STRING, npc.toString())
            profile = ResolvableProfile.resolvableProfile().apply {
                addProperty(ProfileProperty("textures", npc.npcSkinTexture.ifEmpty { FALLBACK_TEXTURE }))
            }.build()
        }
    }

    fun clearNPCs() {
        for(world in Bukkit.getWorlds()) {
            for(mannequin in world.getEntitiesByClass(Mannequin::class.java)) {
                if(mannequin.persistentDataContainer.has(Keys.LOBBY_NPC)) {
                    mannequin.remove()
                }
            }
        }
    }
}