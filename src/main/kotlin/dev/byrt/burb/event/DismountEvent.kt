package dev.byrt.burb.event

import org.bukkit.entity.AreaEffectCloud
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDismountEvent

class DismountEvent: Listener {
    @EventHandler
    private fun onDismount(e: EntityDismountEvent) {
        if(e.entity is Player && e.dismounted is AreaEffectCloud) {
            e.isCancelled = true
        }
    }
}