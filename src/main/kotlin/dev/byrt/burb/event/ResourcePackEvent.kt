package dev.byrt.burb.event

import dev.byrt.burb.chat.ChatUtility
import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.lobby.LobbyManager
import dev.byrt.burb.logger
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
            object : BukkitRunnable() {
                override fun run() {
                    LobbyManager.playerJoinTitleScreen(e.player)
                }
            }.runTaskLater(plugin, 30L)
        }
        if(e.status in listOf(PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD, PlayerResourcePackStatusEvent.Status.FAILED_RELOAD, PlayerResourcePackStatusEvent.Status.DISCARDED, PlayerResourcePackStatusEvent.Status.INVALID_URL)) {
            ChatUtility.broadcastDev("RP failed for ${e.player.name} due to ${e.status.name} (${e.id}).", false)
            logger.severe("RP failed for ${e.player.name} due to ${e.status.name} (${e.id}).")
        }
    }
}