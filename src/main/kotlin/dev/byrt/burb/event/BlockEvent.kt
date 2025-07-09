package dev.byrt.burb.event

import com.destroystokyo.paper.event.block.BlockDestroyEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockBurnEvent
import org.bukkit.event.block.BlockFadeEvent
import org.bukkit.event.block.BlockFormEvent
import org.bukkit.event.block.BlockGrowEvent
import org.bukkit.event.block.BlockPhysicsEvent
import org.bukkit.event.block.BlockPlaceEvent

class BlockEvent: Listener {
    @EventHandler
    private fun onBlockPlace(e: BlockPlaceEvent) {
        e.isCancelled = !e.player.isOp
    }

    @EventHandler
    private fun onBlockBreak(e: BlockBreakEvent) {
        e.isCancelled = !e.player.isOp
    }

    @EventHandler
    private fun onBlockPerish(e: BlockFadeEvent) {
        e.isCancelled = true
    }

    @EventHandler
    private fun onBlockForm(e: BlockFormEvent) {
        e.isCancelled = true
    }

    @EventHandler
    private fun onBlockBurn(e: BlockBurnEvent) {
        e.isCancelled = true
    }

    @EventHandler
    private fun onBlockGrow(e: BlockGrowEvent) {
        e.isCancelled = true
    }

    @EventHandler
    private fun onBlockPhysics(e: BlockPhysicsEvent) {
        e.isCancelled = true
    }

    @EventHandler
    private fun onBlockShatter(e: BlockDestroyEvent) {
        e.isCancelled = true
    }
}