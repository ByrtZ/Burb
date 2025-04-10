package dev.byrt.burb.player

import dev.byrt.burb.chat.ChatUtility
import dev.byrt.burb.chat.Formatting
import dev.byrt.burb.item.ItemManager
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.plugin

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.kyori.adventure.title.Title

import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.AreaEffectCloud
import org.bukkit.entity.Display
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Duration

import kotlin.random.Random

object PlayerVisuals {
    fun damageIndicator(player: Player, damage: Double) {
        val damageTaken = BigDecimal(damage).setScale(2, RoundingMode.HALF_EVEN)
        val damageIndicatorEntity = player.location.world.spawn(player.location.clone().add(Random.nextDouble(-0.25, 0.35), Random.nextDouble(0.5, 2.5), Random.nextDouble(-0.25, 0.35)), TextDisplay::class.java).apply {
            alignment = TextDisplay.TextAlignment.CENTER
            billboard = Display.Billboard.VERTICAL
            isCustomNameVisible = true
            scoreboardTags.add("burb.damage_indicator")
            customName(Formatting.allTags.deserialize("${if(damageTaken.toInt() <= 3) "<yellow>" else if(damageTaken.toInt() in 4..6) "<gold>" else if(damageTaken.toInt() >= 7) "<red>" else "<#000000>"}${damageTaken}"))
        }
        object : BukkitRunnable() {
            override fun run() {
                damageIndicatorEntity.remove()
            }
        }.runTaskLater(plugin, 30L)
    }

    fun death(player: Player, deathMessage: Component) {
        val plainDeathMessage = if(player.hasPotionEffect(PotionEffectType.INVISIBILITY)) { PlainTextComponentSerializer.plainText().serialize(deathMessage).replace(player.name, "<obfuscated>*<reset>".repeat(Random.nextInt(4, 16))) } else { PlainTextComponentSerializer.plainText().serialize(deathMessage) }
        parseDeathMessage(player, plainDeathMessage)

        player.clearActivePotionEffects()
        player.addPotionEffect(PotionEffect(PotionEffectType.HUNGER, PotionEffect.INFINITE_DURATION, 0, false, false))
        ItemManager.clearItems(player)
        val deathOverlayItem = ItemStack(Material.CARVED_PUMPKIN)
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

    fun respawn(player: Player) {
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

    fun postRespawn(player: Player, vehicle: AreaEffectCloud) {
        player.eject()
        vehicle.remove()
        player.fireTicks = 0
        player.health = 20.0
        player.inventory.helmet = null
        showPlayer(player)
        ItemManager.givePlayerTeamBoots(player, player.burbPlayer().playerTeam)
        ItemManager.giveCharacterItems(player)
        player.teleport(Location(Bukkit.getWorlds()[0], 0.5, 30.0, 0.5, 0.0f, 0.0f))
    }

    fun reloadWeapon(player: Player, reloadTime: Int) {
        var timer = 0
        var reloadPhase = 0.0
        var pitch = 0f
        object : BukkitRunnable() {
            override fun run() {
                if(!player.isOnline) cancel()
                if(timer < reloadTime) {
                    if(timer % 2 == 0) {
                        player.playSound(player.location, Sounds.Weapon.RELOAD_TICK, 0.5f, pitch)
                        player.showTitle(
                            Title.title(
                                Formatting.allTags.deserialize(""),
                                Formatting.allTags.deserialize("<transition:red:green:$reloadPhase>Reloading..."),
                                Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(5), Duration.ofSeconds(0))
                            )
                        )
                        pitch += 0.075f
                        if(reloadPhase < 1) reloadPhase += 0.05 else reloadPhase = 1.0
                    }
                } else {
                    player.showTitle(
                        Title.title(
                            Formatting.allTags.deserialize(""),
                            Formatting.allTags.deserialize("<green>Reloaded!"),
                            Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(1), Duration.ofMillis(250))
                        )
                    )
                    player.playSound(Sounds.Weapon.RELOAD_SUCCESS)
                    cancel()
                }
                timer++
            }
        }.runTaskTimer(plugin, 0L, 1L)
    }

    fun disconnectInterrupt(player: Player) {
        if(player.vehicle is AreaEffectCloud) {
            player.vehicle?.remove()
            showPlayer(player)
            player.teleport(Bukkit.getWorlds()[0].spawnLocation)
            player.inventory.helmet = null
        }
    }
}