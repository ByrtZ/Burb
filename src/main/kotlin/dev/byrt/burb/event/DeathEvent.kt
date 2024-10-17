package dev.byrt.burb.event

import dev.byrt.burb.library.Sounds
import dev.byrt.burb.player.PlayerVisuals

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class DeathEvent: Listener {
    @EventHandler
    private fun onDeath(e: PlayerDeathEvent) {
        if(e.player.killer != null) {
            if(e.player.killer is Player) {
                e.player.killer!!.playSound(Sounds.Score.ELIMINATION)
                e.deathMessage()?.let { PlayerVisuals.death(e.player, it) }
                e.isCancelled = true
            }
        }
    }
}