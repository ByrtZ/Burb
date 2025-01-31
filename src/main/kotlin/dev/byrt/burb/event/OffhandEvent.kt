package dev.byrt.burb.event

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerSwapHandItemsEvent

@Suppress("unused")
class OffhandEvent: Listener {
    @EventHandler
    private fun onOffhand(e: PlayerSwapHandItemsEvent) {
        e.isCancelled = true
    }
}