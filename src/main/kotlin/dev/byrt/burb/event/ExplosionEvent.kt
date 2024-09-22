package dev.byrt.burb.event

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityExplodeEvent

class ExplosionEvent: Listener {
    @EventHandler
    private fun onExplosion(e: EntityExplodeEvent) {
        e.blockList().clear()
    }
}