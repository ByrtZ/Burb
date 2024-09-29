package dev.byrt.burb.event

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent

class ProjectileEvent: Listener {
    @EventHandler
    private fun onProjectileHit(e: ProjectileHitEvent) {
        if(e.hitEntity != null) {
            if(e.hitEntity is Player) {
                val player = e.hitEntity as Player
                player.damage(3.5, e.entity)
                e.entity.remove()
            }
        }
        if(e.hitBlock != null) {
            e.entity.remove()
        }
    }
}