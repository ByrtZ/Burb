package dev.byrt.burb.event

import dev.byrt.burb.library.Sounds

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class DeathEvent: Listener {
    @EventHandler
    private fun onDeath(e: PlayerDeathEvent) {
        if(e.player.killer != null) {
            if(e.player.killer is Player) {
                val killer = e.player.killer!!
                killer.playSound(Sounds.Score.ELIMINATION)
            }
        }
    }
}