package dev.byrt.burb.player

import dev.byrt.burb.chat.ChatUtility
import dev.byrt.burb.chat.Formatting
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.plugin

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.kyori.adventure.title.Title

import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.AreaEffectCloud
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable

import java.time.Duration

import kotlin.random.Random

object PlayerVisuals {
    fun death(player: Player, deathMessage: Component) {
        val plainDeathMessage = if(player.hasPotionEffect(PotionEffectType.INVISIBILITY)) { PlainTextComponentSerializer.plainText().serialize(deathMessage).replace(player.name, "<obfuscated>*<reset>".repeat(Random.nextInt(4, 16))) } else { PlainTextComponentSerializer.plainText().serialize(deathMessage) }
        parseDeathMessage(player, plainDeathMessage)

        player.clearActivePotionEffects()
        player.inventory.clear()
        val deathOverlayItem = ItemStack(Material.CARVED_PUMPKIN, 1)
        deathOverlayItem.addEnchantment(Enchantment.BINDING_CURSE, 1)
        player.inventory.helmet = deathOverlayItem

        val deathVehicle = player.world.spawn(player.location, AreaEffectCloud::class.java)
        deathVehicle.duration = Int.MAX_VALUE
        deathVehicle.radius = 0F
        deathVehicle.waitTime = 0
        deathVehicle.color = Color.BLACK
        deathVehicle.addScoreboardTag("${player.uniqueId}-death-vehicle")
        deathVehicle.addPassenger(player)

        hidePlayer(player)
        deathEffects(player)

        /** Scheduled Respawn and Post Respawn **/
        object : BukkitRunnable() {
            override fun run() {
                respawn(player)
                object : BukkitRunnable() {
                    override fun run() {
                        postRespawn(player, deathVehicle)
                    }
                }.runTaskLater(plugin, 20L)
            }
        }.runTaskLater(plugin, 160L)
        player.showTitle(
            Title.title(
                Formatting.allTags.deserialize("<red>You died!"),
                Formatting.allTags.deserialize("<gray>${plainDeathMessage}"),
                Title.Times.times(
                    Duration.ofMillis(250),
                    Duration.ofSeconds(8),
                    Duration.ofMillis(750)
                )
            )
        )
    }

    private fun parseDeathMessage(player: Player, plainDeathMessage: String) {
        val parsedDeathMessage = plainDeathMessage.replace(player.name, "${if(player.isOp) "<dark_red>" else "<white>"}${player.name}<reset>")
        ChatUtility.messageAudience(Audience.audience(Bukkit.getOnlinePlayers()), "<red><prefix:skull><reset> $parsedDeathMessage.", false)
    }

    fun hidePlayer(player: Player) {
        for(other in Bukkit.getOnlinePlayers()) {
            other.hidePlayer(plugin, player)
        }
    }

    fun showPlayer(player: Player) {
        for(other in Bukkit.getOnlinePlayers()) {
            other.showPlayer(plugin, player)
        }
    }

    private fun deathEffects(player: Player) {
        player.playSound(Sounds.Score.DEATH)
    }

    private fun respawn(player: Player) {
        player.showTitle(
            Title.title(
                Formatting.allTags.deserialize("\uD000"),
                Formatting.allTags.deserialize(""),
                Title.Times.times(
                    Duration.ofMillis(250),
                    Duration.ofSeconds(2),
                    Duration.ofMillis(500)
                )
            )
        )
        player.playSound(Sounds.Score.RESPAWN)
    }

    private fun postRespawn(player: Player, deathVehicle: AreaEffectCloud) {
        player.eject()
        deathVehicle.remove()
        player.teleport(Bukkit.getWorlds()[0].spawnLocation)
        player.fireTicks = 0
        player.health = 20.0
        player.inventory.helmet = null
        showPlayer(player)
    }

    fun disconnectInterruptDeath(player: Player) {
        if(player.vehicle is AreaEffectCloud) {
            player.vehicle?.remove()
            showPlayer(player)
            player.teleport(Bukkit.getWorlds()[0].spawnLocation)
            player.inventory.helmet = null
        }
    }
}