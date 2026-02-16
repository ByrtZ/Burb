package dev.byrt.burb.event

import dev.byrt.burb.game.Scores
import dev.byrt.burb.text.Formatting
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.player.PlayerVisuals
import dev.byrt.burb.player.progression.BurbPlayerData

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

@Suppress("unused")
class DeathEvent : Listener {
    @EventHandler
    private fun onDeath(e: PlayerDeathEvent) {
        e.player.killer?.let {
            it.playSound(Sounds.Score.ELIMINATION)
            Scores.addScore(it.burbPlayer().playerTeam ?: return, 50)
            BurbPlayerData.appendExperience(it, 25)
        }

        PlayerVisuals.death(e.player, e.player.killer, true)
        e.isCancelled = true
    }
}