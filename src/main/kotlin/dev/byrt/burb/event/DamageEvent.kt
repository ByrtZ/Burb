package dev.byrt.burb.event

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByBlockEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

class DamageEvent: Listener {
    @EventHandler
    private fun onDamage(e: EntityDamageEvent) {
        e.isCancelled = true
    }

    @EventHandler
    private fun onDamageByEntity(e: EntityDamageByEntityEvent) {
        e.isCancelled = true
    }

    @EventHandler
    private fun onDamageByBlock(e: EntityDamageByBlockEvent) {
        e.isCancelled = true
    }
}