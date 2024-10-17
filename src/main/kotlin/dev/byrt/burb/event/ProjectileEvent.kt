package dev.byrt.burb.event

import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.team.Teams

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent

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
                        player.damage(0.001, shooter)
                        if(player.health >= 0.85) {
                            player.health -= 0.85
                        } else {
                            player.health = 0.0
                        }
                        player.world.playSound(player.location, "entity.player.hurt", 0.5f, 1f)
                        shooter.playSound(shooter.location, "entity.arrow.hit_player", 0.25f, 0f)
                        e.entity.remove()
                        e.isCancelled = true
                    }
                }
            }
        }
        if(e.hitBlock != null) {
            e.entity.remove()
        }
    }
}