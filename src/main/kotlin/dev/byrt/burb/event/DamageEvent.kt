package dev.byrt.burb.event

import dev.byrt.burb.player.PlayerVisuals

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

@Suppress("unused", "unstableApiUsage")
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
        } else {
            if(e.entity is Player) {
                val player = e.entity as Player
                if(e.damage.toInt() > 0) {
                    PlayerVisuals.damageIndicator(player, e.damage)
                }
            }
        }
    }

    @EventHandler
    private fun onDamageByEntity(e: EntityDamageByEntityEvent) {
        if(e.damager is Player && e.entity is Player) {
            val damager = e.damager as Player
            val damaged = e.entity as Player
            if(damager.vehicle != null) {
                if(damager.vehicle?.scoreboardTags?.contains("${damager.uniqueId}-death-vehicle") == true) {
                    e.isCancelled = true
                    return
                }
            }
        }
        e.isCancelled = false
    }
}