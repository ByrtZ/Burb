package dev.byrt.burb.event

import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.player.PlayerVisuals
import dev.byrt.burb.plugin
import dev.byrt.burb.team.Teams

import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.persistence.PersistentDataType

@Suppress("unused")
class ProjectileEvent: Listener {
    @EventHandler
    private fun onProjectileHit(e: ProjectileHitEvent) {
        if(e.hitEntity != null) {
            if(e.hitEntity is Player && e.entity.shooter is Player) {
                val player = e.hitEntity as Player
                val shooter = e.entity.shooter as Player
                if(player != shooter) {
                    if(player.burbPlayer().playerTeam == Teams.SPECTATOR) return
                    if(shooter.burbPlayer().playerTeam == Teams.SPECTATOR) return
                    if(player.burbPlayer().playerTeam != shooter.burbPlayer().playerTeam) {
                        if(e.entity.persistentDataContainer.has(NamespacedKey(plugin, "burb.weapon.damage"))) {
                            player.damage(0.001, shooter)
                            val damageDealt = e.entity.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.damage"), PersistentDataType.DOUBLE)!!
                            if(player.health >= damageDealt) {
                                player.health -= damageDealt
                            } else {
                                player.health = 0.0
                            }
                            player.world.playSound(player.location, "entity.player.hurt", 0.5f, 1f)
                            shooter.playSound(shooter.location, "entity.arrow.hit_player", 0.25f, 0f)
                            PlayerVisuals.damageIndicator(player, damageDealt)
                            e.entity.remove()
                            e.isCancelled = true
                        }
                    }
                }
            }
        }
        if(e.hitBlock != null) {
            e.entity.remove()
        }
    }
}