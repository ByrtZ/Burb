package dev.byrt.burb.event

import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.Scores
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.player.PlayerVisuals
import dev.byrt.burb.player.progression.BurbExperienceLevels

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

@Suppress("unused")
class DeathEvent : Listener {
    @EventHandler
    private fun onDeath(e: PlayerDeathEvent) {
        e.player.killer?.let {
            it.playSound(Sounds.Score.ELIMINATION)
            Scores.addScore(GameManager.teams.getTeam(it.uniqueId) ?: return, 50)
            BurbExperienceLevels.appendExperience(it, 50)
        }

        PlayerVisuals.death(e.player, e.player.killer, true)
        e.isCancelled = true
    }
}