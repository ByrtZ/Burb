package dev.byrt.burb.event

import org.bukkit.damage.DamageType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByBlockEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

@Suppress("unstableApiUsage")
class DamageEvent: Listener {
    @EventHandler
    private fun onDamage(e: EntityDamageEvent) {
        e.isCancelled = !(e.damageSource.damageType == DamageType.ARROW || e.damageSource.damageType == DamageType.MOB_PROJECTILE || e.damageSource.damageType == DamageType.PLAYER_ATTACK)
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