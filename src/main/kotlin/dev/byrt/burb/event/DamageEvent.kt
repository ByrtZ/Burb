package dev.byrt.burb.event

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

@Suppress("unstableApiUsage")
class DamageEvent: Listener {
    @EventHandler
    private fun onDamage(e: EntityDamageEvent) {
        if(e.cause == EntityDamageEvent.DamageCause.HOT_FLOOR
            || e.cause == EntityDamageEvent.DamageCause.DROWNING
            || e.cause == EntityDamageEvent.DamageCause.CRAMMING
            || e.cause == EntityDamageEvent.DamageCause.LAVA
            || e.cause == EntityDamageEvent.DamageCause.FIRE
            || e.cause == EntityDamageEvent.DamageCause.FIRE_TICK
            || e.cause == EntityDamageEvent.DamageCause.CRAMMING
            || e.cause == EntityDamageEvent.DamageCause.LIGHTNING
            || e.cause == EntityDamageEvent.DamageCause.WITHER
            || e.cause == EntityDamageEvent.DamageCause.VOID
            || e.cause == EntityDamageEvent.DamageCause.DRAGON_BREATH
            || e.cause == EntityDamageEvent.DamageCause.POISON
            || e.cause == EntityDamageEvent.DamageCause.FREEZE
            || e.cause == EntityDamageEvent.DamageCause.SONIC_BOOM
            || e.cause == EntityDamageEvent.DamageCause.FALL) {
            e.isCancelled = true
        }
    }

    @EventHandler
    private fun onDamageByEntity(e: EntityDamageByEntityEvent) {
        e.isCancelled = false
    }
}