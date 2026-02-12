package dev.byrt.burb.player

import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.game.events.SpecialEvent
import dev.byrt.burb.game.events.SpecialEvents
import dev.byrt.burb.game.location.SpawnPoints
import dev.byrt.burb.interfaces.BurbInterface
import dev.byrt.burb.interfaces.BurbInterfaceType
import dev.byrt.burb.item.ItemManager
import dev.byrt.burb.item.ServerItem
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.library.Translation
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.player.character.BurbCharacter
import dev.byrt.burb.player.character.setRandomCharacter
import dev.byrt.burb.player.cosmetics.BurbCosmetics
import dev.byrt.burb.plugin
import dev.byrt.burb.text.ChatUtility.BURB_FONT_TAG
import dev.byrt.burb.text.Formatting
import dev.byrt.burb.text.Formatting.sendTranslated
import io.papermc.paper.entity.TeleportFlag
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.*
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Display
import org.bukkit.entity.Firework
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Duration
import kotlin.random.Random

object PlayerVisuals {
    fun damageIndicator(entity: LivingEntity, damage: Double) {
        val damageTaken = BigDecimal(damage).setScale(2, RoundingMode.HALF_EVEN)
        val damageIndicatorEntity = entity.location.world.spawn(entity.location.clone().add(Random.nextDouble(-0.25, 0.35), Random.nextDouble(0.5, 2.5), Random.nextDouble(-0.25, 0.35)), TextDisplay::class.java).apply {
            alignment = TextDisplay.TextAlignment.CENTER
            billboard = Display.Billboard.VERTICAL
            isShadowed = true
            isCustomNameVisible = true
            scoreboardTags.add("burb.damage_indicator")
            customName(Formatting.allTags.deserialize("${BURB_FONT_TAG}${if(damageTaken.toInt() <= 3) "<yellow>" else if(damageTaken.toInt() in 4..6) "<gold>" else if(damageTaken.toInt() >= 7) "<red>" else "<#000000>"}${damageTaken}"))
        }
        object : BukkitRunnable() {
            override fun run() {
                damageIndicatorEntity.remove()
            }
        }.runTaskLater(plugin, 30L)
    }

    /**
     * @param [player] Player to die
     * @param [killer] Player who killed the [player], nullable
     * @param [showDeathMessage] Should the death message be shown
     * @param [isTeamWipe] Should the whole team be eliminated with extended timer, only applies to vanquish showdown event and should only be called under this circumstance
     * @param [forcedTeamWipe] Debug parameter, forces a team wipe and only runs if receiving team has more than one player
     */
    fun death(player: Player, killer: Player?, showDeathMessage: Boolean, isTeamWipe: Boolean = false) {
        player.burbPlayer().setIsDead(true)
        player.activePotionEffects.forEach { e -> if(e.type !in listOf(PotionEffectType.HUNGER, PotionEffectType.INVISIBILITY)) player.removePotionEffect(e.type)}
        if(player.burbPlayer().playerCharacter == BurbCharacter.ZOMBIES_HEAVY) {
            player.addPotionEffect(PotionEffect(PotionEffectType.JUMP_BOOST, PotionEffect.INFINITE_DURATION, 3, false, false))
        }
        player.addPotionEffect(PotionEffect(PotionEffectType.HUNGER, PotionEffect.INFINITE_DURATION, 0, false, false))
        ItemManager.clearItems(player)
        val deathOverlayItem = ItemStack(Material.CARVED_PUMPKIN)
        val deathOverlayItemMeta = deathOverlayItem.itemMeta
        deathOverlayItem.addEnchantment(Enchantment.BINDING_CURSE, 1)
        deathOverlayItemMeta.itemModel = NamespacedKey("minecraft", "binoculars")
        deathOverlayItem.itemMeta = deathOverlayItemMeta
        player.inventory.helmet = deathOverlayItem

        val deathVehicle = player.world.spawn(player.location.add(0.0, 0.6, 0.0), ItemDisplay::class.java).apply {
            teleportDuration = 1
            addScoreboardTag("${player.uniqueId}-death-vehicle")
            addPassenger(player)
        }

        val deathMessage = if (showDeathMessage) {
            if (killer != null) {
                Component.translatable("burb.death.killed_by", player.displayName(), killer.displayName())
            } else {
                Component.translatable("burb.death.died", player.displayName())
            }
        } else Component.empty()

        if(!isTeamWipe) {
            player.showTitle(
                Title.title(
                    Formatting.allTags.deserialize("<#ff3333>You died!"),
                    deathMessage,
                    Title.Times.times(
                        Duration.ofMillis(250),
                        Duration.ofSeconds(8),
                        Duration.ofMillis(750)
                    )
                )
            )
            player.playSound(Sounds.Score.DEATH)
            deathMessage?.let(Bukkit::broadcast)
        } else {
            player.showTitle(
                Title.title(
                    Formatting.allTags.deserialize("<#ff3333>You have been team wiped!"),
                    Formatting.allTags.deserialize("<gray>Your respawn timer has been extended."),
                    Title.Times.times(
                        Duration.ofMillis(250),
                        Duration.ofSeconds(8),
                        Duration.ofMillis(750)
                    )
                )
            )
        }

        /** TEAM WIPE SPECIAL EVENT **/
        if(SpecialEvents.getCurrentEvent() == SpecialEvent.VANQUISH_SHOWDOWN || forcedTeamWipe) {
            // Only run if this vanquished team member is the final player on the team to be eliminated to initiate a team wipe
            val playerTeam = GameManager.teams.getTeam(player.uniqueId) ?: return
            val members = GameManager.teams.teamMembers(playerTeam)
            if(members.all(BurbPlayer::isDead) && !isTeamWipe) {
                deathVehicle.remove()
                members.forEach { member ->
                    death(member.bukkitPlayer(), null, false, true)
                }
                Bukkit.getServer().playSound(Sounds.Score.TEAM_WIPE)
                Bukkit.getServer().sendTranslated("burb.special_event.vanquish_showdown.wipe", playerTeam)
            }
        }

        hidePlayer(player)

        /** Move death vehicle ahead of the killer as a faux spectator mode. **/
        if(killer != null) {
            object : BukkitRunnable() {
                override fun run() {
                    player.playSound(Sounds.Score.DEATH_STATS)
                    object : BukkitRunnable() {
                        override fun run() {
                            if(killer.burbPlayer().isDead || deathVehicle.isDead || !deathVehicle.passengers.contains(player) || !player.isOnline) {
                                this.cancel()
                            } else {
                                if(!deathVehicle.passengers.contains(player)) deathVehicle.addPassenger(player)
                                val killerLocation = killer.location.setRotation(killer.yaw, 0f)
                                val killerDirection = killerLocation.add(killerLocation.direction.normalize().multiply(2))
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
            val RESPAWN_TIME = if(isTeamWipe) 30 else 12
            var timer = RESPAWN_TIME
            var ticks = 0
            override fun run() {
                if(player.vehicle == deathVehicle) {
                    if(timer < RESPAWN_TIME - 6) {
                        if(GameManager.getGameState() in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
                            if(ticks == 1) {
                                player.showTitle(
                                    Title.title(
                                        Formatting.allTags.deserialize("<${if(isTeamWipe) "red" else "yellow"}>Respawning in"),
                                        Formatting.allTags.deserialize("<b>►${timer}◄"),
                                        Title.Times.times(
                                            Duration.ofMillis(0),
                                            Duration.ofSeconds(2),
                                            Duration.ofMillis(750)
                                        )
                                    )
                                )
                                player.playSound(Sounds.Timer.TICK)
                                if(SpecialEvents.getCurrentEvent() != SpecialEvent.RANDOS_REVENGE) {
                                    player.sendActionBar(Formatting.allTags.deserialize(if(timer % 2 == 0) Translation.Generic.CHARACTER_SELECTION_ACTIONBAR else Translation.Generic.CHARACTER_SELECTION_ACTIONBAR.replace(BURB_FONT_TAG, "$BURB_FONT_TAG<yellow>")))
                                }
                            }
                            if(SpecialEvents.getCurrentEvent() != SpecialEvent.RANDOS_REVENGE && player.isSneaking) {
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
                    deathVehicle.remove()
                    cancel()
                }
            }
        }.runTaskTimer(plugin, 0L, 1L)
    }

    fun showPlayer(player: Player) {
        for(other in Bukkit.getOnlinePlayers()) {
            other.showPlayer(plugin, player)
        }
    }
    fun hidePlayer(player: Player) {
        for(other in Bukkit.getOnlinePlayers()) {
            other.hidePlayer(plugin, player)
        }
    }

    fun respawn(player: Player) {
        player.showTitle(
            Title.title(
                Formatting.glyph("\uD000"),
                Formatting.allTags.deserialize(""),
                Title.Times.times(
                    Duration.ofMillis(250),
                    Duration.ofSeconds(2),
                    Duration.ofMillis(500)
                )
            )
        )
        if(SpecialEvents.getCurrentEvent() == SpecialEvent.VANQUISH_SHOWDOWN) {
            player.playSound(Sounds.Score.VANQUISH_SHOWDOWN_RESPAWN)
        } else {
            player.playSound(Sounds.Score.RESPAWN)
        }
    }

    fun postRespawn(player: Player, vehicle: ItemDisplay) {
        player.eject()
        vehicle.remove()
        player.burbPlayer().setIsDead(false)
        player.fireTicks = 0
        player.health = 20.0
        player.inventory.helmet = null
        SpawnPoints.respawnLocation(player)
        ItemManager.givePlayerTeamBoots(player)

        // Add hub item
        if(GameManager.getGameState() == GameState.IDLE) {
            player.inventory.setItem(0, ServerItem.getProfileItem())
        } else {
            player.inventory.remove(ServerItem.getProfileItem())
        }

        if(SpecialEvents.getCurrentEvent() == SpecialEvent.RANDOS_REVENGE) {
            player.burbPlayer().setRandomCharacter()
            player.sendMessage(Formatting.allTags.deserialize("<newline>${Translation.Generic.ARROW_PREFIX}<rainbow>Rando's Revenge<reset> morphed you into a different character!<newline>"))
            player.playSound(Sounds.Misc.RANDO_NEW_CHARACTER)
        } else {
            if(SpecialEvents.getCurrentEvent() == SpecialEvent.VANQUISH_SHOWDOWN) {
                player.playSound(Sounds.Score.VANQUISH_SHOWDOWN_POST_RESPAWN)
            }
            ItemManager.giveCharacterItems(player)
        }

        BurbCosmetics.equipCosmetics(player)

        showPlayer(player)
    }

    fun reloadWeapon(player: Player, reloadTime: Int) {
        var timer = 0
        var reloadPhase = 0.0
        var pitch = 0f
        object : BukkitRunnable() {
            override fun run() {
                if(!player.isOnline) cancel()
                if(GameManager.getGameState() !in listOf(GameState.IN_GAME, GameState.OVERTIME)) cancel()
                if(player.burbPlayer().isDead) cancel()
                if(timer < reloadTime) {
                    if(timer % 2 == 0) {
                        player.playSound(player.location, Sounds.Weapon.RELOAD_TICK, 0.3f, pitch)
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

    fun applyPack(player: Player) {
        player.teleport(Location(Bukkit.getWorlds()[0], 0.5, -1000.0, 0.5))
        player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, PotionEffect.INFINITE_DURATION, 0))
    }

    /**
     * @param [location] Where to spawn the firework
     * @param [flicker] Whether the firework has the flicker effect
     * @param [trail] Whether the firework has the trail effect
     * @param [color] What colour the firework is
     * @param [fireworkType] What type the firework is
     * @param [variedVelocity] Whether the firework should shoot into the sky with a random offset
     */
    fun firework(
        location: Location,
        flicker: Boolean,
        trail: Boolean,
        color: Color,
        fireworkType: FireworkEffect.Type,
        variedVelocity: Boolean
    ) {
        val f: Firework = location.world.spawn(
            Location(location.world, location.x, location.y + 1.0, location.z),
            Firework::class.java
        )
        val fm = f.fireworkMeta
        fm.addEffect(
            FireworkEffect.builder()
                .flicker(flicker)
                .trail(trail)
                .with(fireworkType)
                .withColor(color)
                .build()
        )
        if (variedVelocity) {
            fm.power = 1
            f.fireworkMeta = fm
            val direction = Vector(
                Random.nextDouble(-0.005, 0.005),
                Random.nextDouble(0.25, 0.35),
                Random.nextDouble(-0.005, 0.005)
            ).normalize()
            f.velocity = direction
        } else {
            fm.power = 0
            f.fireworkMeta = fm
            f.ticksToDetonate = 0
        }
    }
}