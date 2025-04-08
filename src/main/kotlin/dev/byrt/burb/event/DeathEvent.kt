package dev.byrt.burb.event

import dev.byrt.burb.chat.Formatting
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.player.PlayerVisuals

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

@Suppress("unused")
class DeathEvent: Listener {
    @EventHandler
    private fun onDeath(e: PlayerDeathEvent) {
        PlayerVisuals.death(e.player,
            if(e.deathMessage() == null)
                Formatting.allTags.deserialize("${e.player.burbPlayer().playerTeam.teamColourTag}${e.player.name} died.")
            else
                e.deathMessage()!!
        )
        if(e.player.killer != null) {
            if(e.player.killer is Player) {
                e.player.killer!!.playSound(Sounds.Score.ELIMINATION)
            }
        }
        e.isCancelled = true
    }
}