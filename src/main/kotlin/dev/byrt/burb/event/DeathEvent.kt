package dev.byrt.burb.event

import dev.byrt.burb.game.ScoreManager
import dev.byrt.burb.text.Formatting
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.player.PlayerVisuals
import dev.byrt.burb.player.progression.BurbProgression

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

@Suppress("unused")
class DeathEvent: Listener {
    @EventHandler
    private fun onDeath(e: PlayerDeathEvent) {
        if(e.player.killer != null) {
            if(e.player.killer is Player) {
                e.player.killer!!.playSound(Sounds.Score.ELIMINATION)
                ScoreManager.addScore(e.player.killer!!.burbPlayer().playerTeam, 50)
                BurbProgression.appendExperience(e.player.killer!!, 25)
            }
        }
        PlayerVisuals.death(
            e.player,
            if(e.player.killer != null && e.player.killer is Player) e.player.killer else null,
            if(e.player.killer is Player && e.player.killer != null) Formatting.allTags.deserialize("${e.player.burbPlayer().playerTeam.teamColourTag}${e.player.name}<reset> was vanquished by ${e.player.killer!!.burbPlayer().playerTeam.teamColourTag}${e.player.killer!!.name}<reset>.") else Formatting.allTags.deserialize("${e.player.burbPlayer().playerTeam.teamColourTag}${e.player.name}<reset> was vanquished.")
        )
        e.isCancelled = true
    }
}