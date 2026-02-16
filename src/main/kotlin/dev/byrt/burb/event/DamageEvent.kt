package dev.byrt.burb.event

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent

import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.item.weapon.BurbWeapons
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.player.PlayerVisuals

import io.papermc.paper.event.entity.EntityKnockbackEvent

import org.bukkit.entity.Explosive
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

@Suppress("unused", "unstableApiUsage")
class DamageEvent: Listener {
    @EventHandler
    private fun onDamage(e: EntityDamageEvent) {
        // Cancel ALL damage when not in the following game states
        if(GameManager.getGameState() !in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
            e.isCancelled = true
            return
        } else {
            // Cancel the following types of damage
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
                || e.cause == EntityDamageEvent.DamageCause.POISON
                || e.cause == EntityDamageEvent.DamageCause.FREEZE
                || e.cause == EntityDamageEvent.DamageCause.SONIC_BOOM
                || e.cause == EntityDamageEvent.DamageCause.FALL
                || e.cause == EntityDamageEvent.DamageCause.SUFFOCATION) {
                e.isCancelled = true
                return
            } else {
                if(e.entity is Player) {
                    val player = e.entity as Player
                    // Cancel if player is dead
                    if(player.burbPlayer().isDead) {
                        e.isCancelled = true
                    } else {
                        if(e.damageSource.causingEntity != null) {
                            val damage = BurbWeapons.calculateDamage(player, e.damage)
                            if(e.damageSource.causingEntity is Player) {
                                val damager = e.damageSource.causingEntity as Player
                                if(!damager.burbPlayer().isDead && damage > 0.1) {
                                    PlayerVisuals.damageIndicator(player, damage)
                                }
                            } else {
                                if(damage > 0.1) {
                                    PlayerVisuals.damageIndicator(player, damage)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    private fun onDamageByEntity(e: EntityDamageByEntityEvent) {
        if(GameManager.getGameState() !in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
            e.isCancelled = true
            return
        } else {
            // Apply damage manually for explosions to bypass invulnerability
            if(e.entity is Player && e.damager is Explosive) {
                val player = e.entity as Player
                if(player.burbPlayer().isDead) {
                    e.isCancelled = true
                    return
                } else {
                    val damage = BurbWeapons.calculateDamage(player, e.damage)
                    player.health -= damage
                    player.damage(0.00001, e.damager)
                    PlayerVisuals.damageIndicator(player, damage)
                    e.isCancelled = true
                }
            }
            // Cancel all damage if the damager is dead unless it's an explosion
            if(e.damager is Player && e.entity is Player) {
                val damager = e.damager as Player
                val damaged = e.entity as Player
                if(damager.burbPlayer().isDead && e.cause !in listOf(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION, EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
                    e.isCancelled = true
                    return
                }
            }
        }
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