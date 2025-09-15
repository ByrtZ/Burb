package dev.byrt.burb.event

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent

import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.player.BurbCharacter
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.player.PlayerVisuals

import io.papermc.paper.event.entity.EntityKnockbackEvent

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

@Suppress("unused", "unstableApiUsage")
class DamageEvent: Listener {
    @EventHandler
    private fun onDamage(e: EntityDamageEvent) {
        if(GameManager.getGameState() !in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
            e.isCancelled = true
            return
        } else {
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
                return
            } else {
                if(e.entity is Player) {
                    val player = e.entity as Player
                    if(player.vehicle != null) {
                        if(player.vehicle?.scoreboardTags?.contains("${player.uniqueId}-death-vehicle") == true) {
                            e.isCancelled = true
                            return
                        }
                    }
                    if(e.damage.toInt() > 0) {
                        if(!e.isCancelled || e.entity.vehicle?.scoreboardTags?.contains("${e.entity.uniqueId}-death-vehicle") == false) {
                            PlayerVisuals.damageIndicator(player, e.damage)
                        }
                    }
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
            // Double backstab damage for melee classes
            if(damager.burbPlayer().playerCharacter in listOf(BurbCharacter.PLANTS_HEAVY, BurbCharacter.ZOMBIES_HEAVY)) {
                val damagedYaw = if (damaged.location.yaw >= 0) damaged.location.yaw else 180 + -damaged.location.yaw
                val damagerYaw = if (damager.location.yaw >= 0) damager.location.yaw else 180 + -damager.location.yaw
                val angle = if (damagedYaw - damagerYaw >= 0) damagedYaw - damagerYaw else damagerYaw - damagedYaw
                if(angle <= 45) {
                    e.damage *= 2
                }
            }
        }
        e.isCancelled = false
    }

    @EventHandler
    private fun onKnockback(e: EntityKnockbackEvent) {
        e.isCancelled = true
    }

    @EventHandler
    private fun onKnockbackByEntity(e: EntityKnockbackByEntityEvent) {
        e.isCancelled = true
    }
}