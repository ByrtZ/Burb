package dev.byrt.burb.event

import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.lobby.LobbyManager
import dev.byrt.burb.plugin

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerResourcePackStatusEvent
import org.bukkit.scheduler.BukkitRunnable

@Suppress("unused")
class ResourcePackEvent: Listener {
    @EventHandler
    private fun onResourcePackStatusUpdate(e: PlayerResourcePackStatusEvent) {
        if(e.status == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
            if(GameManager.getGameState() == GameState.IDLE) {
                object : BukkitRunnable() {
                    override fun run() {
                        LobbyManager.playerJoinTitleScreen(e.player)
                    }
                }.runTaskLater(plugin, 5L)
            }
        }
    }
}