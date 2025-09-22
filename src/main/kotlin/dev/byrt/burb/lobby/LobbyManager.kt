package dev.byrt.burb.lobby

import dev.byrt.burb.text.Formatting
import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.library.Translation
import dev.byrt.burb.music.Jukebox
import dev.byrt.burb.music.Music
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.player.PlayerVisuals
import dev.byrt.burb.plugin
import dev.byrt.burb.team.Teams

import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable

object LobbyManager {
    fun playerJoinTitleScreen(player: Player) {
        player.gameMode = GameMode.ADVENTURE
        player.addPotionEffect(PotionEffect(PotionEffectType.HUNGER, PotionEffect.INFINITE_DURATION, 0, false, false))
        player.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 0, false, false))
        PlayerVisuals.hidePlayer(player)

        val titleVehicle = player.world.spawn(Location(player.world, 983.5, 6.0, 992.5, -63f, 10f), ItemDisplay::class.java)
        titleVehicle.addScoreboardTag("${player.uniqueId}-title-vehicle")
        player.teleport(titleVehicle)
        titleVehicle.addPassenger(player)
        player.playSound(Sounds.Misc.TITLE_SCREEN_ENTER)

        if(!Jukebox.getJukeboxMap().containsKey(player.uniqueId)) {
            object : BukkitRunnable() {
                override fun run() {
                    Jukebox.startMusicLoop(player, plugin, Music.LOBBY_TITLE_SCREEN)
                }
            }.runTaskLater(plugin, 20L)
        }

        object : BukkitRunnable() {
            var i = 0
            override fun run() {
                if(player.vehicle == titleVehicle) {
                    player.sendActionBar(Formatting.allTags.deserialize(if(i <= 10) Translation.Generic.TITLE_SCREEN_ACTIONBAR.replace("<reset>", "<reset><yellow>") else Translation.Generic.TITLE_SCREEN_ACTIONBAR))
                    i++
                    if(i >= 20) i = 0
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
                        PlayerVisuals.postRespawn(player, player.vehicle as ItemDisplay)
                        player.removePotionEffect(PotionEffectType.INVISIBILITY)
                        Jukebox.disconnect(player)
                        if(GameManager.getGameState() == GameState.IDLE) {
                            player.playSound(Sounds.Music.LOBBY_INTRO)
                            object : BukkitRunnable() {
                                override fun run() {
                                    if(GameManager.getGameState() == GameState.IDLE) Jukebox.startMusicLoop(player, plugin, Music.LOBBY_WAITING)
                                }
                            }.runTaskLater(plugin, 1240L)
                        } else {
                            if(GameManager.getGameState() == GameState.IN_GAME) Jukebox.playCurrentMusicStress(player)
                            if(player.burbPlayer().playerTeam !in listOf(Teams.PLANTS, Teams.ZOMBIES)) {
                                player.gameMode = GameMode.SPECTATOR
                            }
                        }
                    }
                }.runTaskLater(plugin, 20L)
            }
        }.runTaskLater(plugin, 2 * 20L)
    }
}