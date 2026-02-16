package dev.byrt.burb.event

import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.player.PlayerVisuals
import dev.byrt.burb.plugin
import dev.byrt.burb.team.Teams

import org.bukkit.NamespacedKey
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffectType

@Suppress("unused")
class ProjectileEvent: Listener {
    @EventHandler
    private fun onProjectileHit(e: ProjectileHitEvent) {
        if(e.hitEntity != null) {
            // Apply damage to enemy players
            if(e.hitEntity is Player && e.entity.shooter is Player) {
                val player = e.hitEntity as Player
                val shooter = e.entity.shooter as Player
                if(player != shooter) {
                    if(player.burbPlayer().playerTeam in listOf(Teams.SPECTATOR, Teams.NULL)) return
                    if(shooter.burbPlayer().playerTeam in listOf(Teams.SPECTATOR, Teams.NULL)) return
                    if(player.burbPlayer().playerTeam != shooter.burbPlayer().playerTeam) {
                        applyDamage(e, player, shooter)
                    }
                }
            }
            // Apply damage dealt to living entities
            if(e.hitEntity is LivingEntity && e.entity.shooter is Player) {
                val hitEntity = e.hitEntity as LivingEntity
                val shooter = e.entity.shooter as Player
                if(hitEntity.type in listOf(EntityType.MANNEQUIN, EntityType.PLAYER)) return
                if(shooter.burbPlayer().playerTeam in listOf(Teams.SPECTATOR, Teams.NULL)) return
                applyDamage(e, hitEntity, shooter)
            }
        }
        if(e.hitBlock != null) {
            e.entity.remove()
        }
    }

    private fun applyDamage(e: ProjectileHitEvent, entity: LivingEntity, shooter: Player) {
        if(e.entity.persistentDataContainer.has(NamespacedKey(plugin, "burb.weapon.damage"))) {
            entity.damage(0.00001, shooter)
            val damageDealt = e.entity.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.damage"), PersistentDataType.DOUBLE)!! * if(entity.hasPotionEffect(PotionEffectType.RESISTANCE)) 0.5 else 1.0
            if(entity.health >= damageDealt) {
                entity.health -= damageDealt
            } else {
                entity.health = 0.0
            }
            entity.world.playSound(entity.location, "entity.player.hurt", 0.5f, 1f)
            shooter.playSound(shooter.location, "entity.arrow.hit_player", 0.25f, 0f)
            PlayerVisuals.damageIndicator(entity, damageDealt)
            e.entity.remove()
            e.isCancelled = true
        }
    }
}