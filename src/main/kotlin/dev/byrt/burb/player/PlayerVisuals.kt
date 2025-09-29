package dev.byrt.burb.player

import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.text.Formatting
import dev.byrt.burb.game.location.SpawnPoints
import dev.byrt.burb.interfaces.BurbInterface
import dev.byrt.burb.interfaces.BurbInterfaceType
import dev.byrt.burb.item.ItemManager
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.library.Translation
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.plugin

import io.papermc.paper.entity.TeleportFlag

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.kyori.adventure.title.Title

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Display
import org.bukkit.entity.ItemDisplay
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

    fun death(player: Player, killer: Player?, deathMessage: Component) {
        for(online in Bukkit.getOnlinePlayers()) online.sendMessage(Formatting.allTags.deserialize(Translation.Generic.DEATH_PREFIX).append(deathMessage))
        player.clearActivePotionEffects()
        player.addPotionEffect(PotionEffect(PotionEffectType.HUNGER, PotionEffect.INFINITE_DURATION, 0, false, false))
        ItemManager.clearItems(player)
        val deathOverlayItem = ItemStack(Material.CARVED_PUMPKIN)
        val deathOverlayItemMeta = deathOverlayItem.itemMeta
        deathOverlayItem.addEnchantment(Enchantment.BINDING_CURSE, 1)
        deathOverlayItemMeta.itemModel = NamespacedKey("minecraft", "binoculars")
        deathOverlayItem.itemMeta = deathOverlayItemMeta
        player.inventory.helmet = deathOverlayItem

        val deathVehicle = player.world.spawn(player.location, ItemDisplay::class.java).apply {
            teleportDuration = 1
            addScoreboardTag("${player.uniqueId}-death-vehicle")
            addPassenger(player)
        }

        hidePlayer(player)
        deathEffects(player)

        /** Move death vehicle ahead of the killer as a faux spectator mode. **/
        if(killer != null) {
            object : BukkitRunnable() {
                override fun run() {
                    player.playSound(Sounds.Score.DEATH_STATS)
                    object : BukkitRunnable() {
                        override fun run() {
                            if(killer.vehicle?.scoreboardTags?.contains("${killer.uniqueId}-death-vehicle") == true || deathVehicle.isDead || !player.isOnline) {
                                this.cancel()
                            } else {
                                if(!deathVehicle.passengers.contains(player)) deathVehicle.addPassenger(player)
                                val killerDirection = killer.location.add(killer.location.direction.multiply(5).normalize())
                                killerDirection.y = killer.location.y + 0.25
                                deathVehicle.teleport(killerDirection, TeleportFlag.EntityState.RETAIN_PASSENGERS)
                                player.setRotation(killer.yaw - 180.0f, 5.0f)
                            }

                        }
                    }.runTaskTimer(plugin, 0L, 1L)
                }
            }.runTaskLater(plugin, 50L)
        }

        /** Respawn, includes ability to change characters, and Post Respawn **/
        object : BukkitRunnable() {
            val RESPAWN_TIME = 15
            var timer = RESPAWN_TIME
            var ticks = 0
            override fun run() {
                if(player.vehicle == deathVehicle) {
                    if(timer < RESPAWN_TIME - 6) {
                        if(GameManager.getGameState() in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
                            if(ticks == 1) {
                                player.showTitle(
                                    Title.title(
                                        Formatting.allTags.deserialize("<yellow>Respawning in"),
                                        Formatting.allTags.deserialize("<b>►${timer}◄"),
                                        Title.Times.times(
                                            Duration.ofMillis(0),
                                            Duration.ofSeconds(2),
                                            Duration.ofMillis(750)
                                        )
                                    )
                                )
                                player.playSound(Sounds.Timer.TICK)
                                player.sendActionBar(Formatting.allTags.deserialize(Translation.Generic.CHARACTER_SELECTION_ACTIONBAR))
                            }
                            if(player.isSneaking) {
                                BurbInterface(player, BurbInterfaceType.CHARACTER_SELECT)
                            }
                        } else {
                            timer = 0
                        }
                    }
                    if(timer <= 0) {
                        object : BukkitRunnable() {
                            override fun run() {
                                respawn(player)
                                object : BukkitRunnable() {
                                    override fun run() {
                                        postRespawn(player, deathVehicle)
                                    }
                                }.runTaskLater(plugin, 20L)
                            }
                        }.runTask(plugin)
                        cancel()
                    } else {
                        if(ticks >= 20) {
                            ticks = 0
                            timer--
                        }
                        ticks++
                    }
                } else {
                    cancel()
                }
            }
        }.runTaskTimer(plugin, 0L, 1L)
        player.showTitle(
            Title.title(
                Formatting.allTags.deserialize("<red>You died!"),
                Formatting.allTags.deserialize("<gray>${PlainTextComponentSerializer.plainText().serialize(deathMessage)}"),
                Title.Times.times(
                    Duration.ofMillis(250),
                    Duration.ofSeconds(8),
                    Duration.ofMillis(750)
                )
            )
        )
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

    fun postRespawn(player: Player, vehicle: ItemDisplay) {
        player.eject()
        vehicle.remove()
        player.fireTicks = 0
        player.health = 20.0
        player.inventory.helmet = null
        SpawnPoints.respawnLocation(player)
        ItemManager.givePlayerTeamBoots(player, player.burbPlayer().playerTeam)
        ItemManager.giveCharacterItems(player)
        showPlayer(player)
    }

    fun reloadWeapon(player: Player, reloadTime: Int) {
        var timer = 0
        var reloadPhase = 0.0
        var pitch = 0f
        object : BukkitRunnable() {
            override fun run() {
                if(!player.isOnline) cancel()
                if(player.vehicle != null) {
                    if(player.vehicle?.scoreboardTags?.contains("${player.uniqueId}-death-vehicle") == true) {
                        cancel()
                    }
                }
                if(timer < reloadTime) {
                    if(timer % 2 == 0) {
                        player.playSound(player.location, Sounds.Weapon.RELOAD_TICK, 0.5f, pitch)
                        player.showTitle(
                            Title.title(
                                Formatting.allTags.deserialize(""),
                                Formatting.allTags.deserialize("<transition:red:green:$reloadPhase>Reloading${if(reloadPhase in 0.0..0.33) "." else if(reloadPhase in 0.34..0.66) ".." else "..."}"),
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
        if(player.vehicle is ItemDisplay) {
            player.vehicle?.remove()
            showPlayer(player)
            player.teleport(Bukkit.getWorlds()[0].spawnLocation)
            player.inventory.helmet = null
        }
    }
}