package dev.byrt.burb.event

import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.music.Jukebox
import dev.byrt.burb.music.Music
import dev.byrt.burb.plugin

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerResourcePackStatusEvent
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable

class ResourcePackEvent: Listener {
    @EventHandler
    private fun onResourcePackStatusUpdate(e: PlayerResourcePackStatusEvent) {
        if(e.status == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
            if(GameManager.getGameState() == GameState.IDLE) {
                if(!Jukebox.getJukeboxMap().containsKey(e.player.uniqueId)) {
                    e.player.playSound(Sounds.Music.LOBBY_INTRO)
                    object : BukkitRunnable() {
                        override fun run() {
                            if(GameManager.getGameState() == GameState.IDLE) Jukebox.startMusicLoop(e.player, plugin, Music.LOBBY_WAITING)
                        }
                    }.runTaskLater(plugin, 1240L)
                }
                object : BukkitRunnable() {
                    override fun run() {
                        e.player.teleport(Location(Bukkit.getWorlds()[0], 0.5, 30.0, 0.5, 0.0f, 0.0f))
                        e.player.removePotionEffect(PotionEffectType.BLINDNESS)
                    }
                }.runTaskLater(plugin, 10L)
            }
        }
    }
}