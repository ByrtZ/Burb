package dev.byrt.burb.lobby

import dev.byrt.burb.chat.Formatting
import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.library.Translation
import dev.byrt.burb.music.Jukebox
import dev.byrt.burb.music.Music
import dev.byrt.burb.player.PlayerVisuals
import dev.byrt.burb.plugin

import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.entity.AreaEffectCloud
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable

object LobbyManager {
    fun playerJoinTitleScreen(player: Player) {
        PlayerVisuals.hidePlayer(player)
        val titleVehicle = player.world.spawn(Location(player.world, 983.5, 6.0, 992.5, -63f, 10f), AreaEffectCloud::class.java)
        titleVehicle.duration = Int.MAX_VALUE
        titleVehicle.radius = 0F
        titleVehicle.waitTime = 0
        titleVehicle.color = Color.BLACK
        titleVehicle.addScoreboardTag("${player.uniqueId}-title-vehicle")
        player.teleport(titleVehicle)
        titleVehicle.addPassenger(player)
        player.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 0, false, false))
        player.playSound(Sounds.Misc.TITLE_SCREEN_ENTER)

        if(!Jukebox.getJukeboxMap().containsKey(player.uniqueId)) {
            if(GameManager.getGameState() == GameState.IDLE) {
                object : BukkitRunnable() {
                    override fun run() {
                        Jukebox.startMusicLoop(player, plugin, Music.LOBBY_TITLE_SCREEN)
                    }
                }.runTaskLater(plugin, 20L)
            }
        }

        object : BukkitRunnable() {
            var i = 0
            override fun run() {
                if(player.vehicle == titleVehicle) {
                    player.sendActionBar(Formatting.allTags.deserialize(if(i % 10 == 0) Translation.Generic.TITLE_SCREEN_ACTIONBAR.replace("<reset>", "<reset><yellow>") else Translation.Generic.TITLE_SCREEN_ACTIONBAR))
                    i++
                } else {
                    player.sendActionBar(Formatting.allTags.deserialize(""))
                    cancel()
                }
            }
        }.runTaskTimer(plugin, 0L, 5L)

        player.removePotionEffect(PotionEffectType.BLINDNESS)
    }

    fun playerJoinHub(player: Player) {
        object : BukkitRunnable() {
            override fun run() {
                PlayerVisuals.respawn(player)
                object : BukkitRunnable() {
                    override fun run() {
                        PlayerVisuals.postRespawn(player, player.vehicle as AreaEffectCloud)
                        player.removePotionEffect(PotionEffectType.INVISIBILITY)
                        Jukebox.disconnect(player)

                        player.playSound(Sounds.Music.LOBBY_INTRO)
                        object : BukkitRunnable() {
                            override fun run() {
                                if(GameManager.getGameState() == GameState.IDLE) Jukebox.startMusicLoop(player, plugin, Music.LOBBY_WAITING)
                            }
                        }.runTaskLater(plugin, 1240L)
                    }
                }.runTaskLater(plugin, 20L)
            }
        }.runTaskLater(plugin, 2 * 20L)
    }
}