package dev.byrt.burb.event

import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerDropItemEvent

@Suppress("unused")
class ItemEvent: Listener {
    @EventHandler
    private fun blockDropItemEvent(e: BlockDropItemEvent) {
        e.isCancelled = true
    }

    @EventHandler
    private fun playerPickupItemEvent(e: PlayerAttemptPickupItemEvent) {
        e.isCancelled = e.player.gameMode != GameMode.CREATIVE
    }

    @EventHandler
    private fun playerDropItemEvent(e: PlayerDropItemEvent) {
        e.isCancelled = e.player.gameMode != GameMode.CREATIVE
    }
}