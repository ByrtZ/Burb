package dev.byrt.burb.game.location

import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.game.objective.CapturePoints
import dev.byrt.burb.player.PlayerVisuals
import dev.byrt.burb.plugin
import dev.byrt.burb.team.BurbTeam
import org.bukkit.Bukkit
import org.bukkit.FireworkEffect
import org.bukkit.Location
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.BoundingBox
import kotlin.random.Random

object BurbAreas {
    private val suburbinationFireworkShowBoundingBox = BoundingBox(-56.5, 25.0, 87.5, 88.5, 35.0, 156.5)
    // TODO: BOUNDING BOXES
    /*val lobbyBoundingBox = BoundingBox()
    val inGameBoundingBox = BoundingBox()*/

    fun runSuburbinationShow(team: BurbTeam) {
        object : BukkitRunnable() {
            override fun run() {
                if(CapturePoints.getSuburbinatingTeam() != team || GameManager.getGameState() !in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
                    this.cancel()
                } else {
                    PlayerVisuals.firework(
                        location = randomLocationFromBoundingBox(suburbinationFireworkShowBoundingBox),
                        flicker = false,
                        trail = false,
                        team.teamColour,
                        fireworkType = FireworkEffect.Type.BALL_LARGE,
                        variedVelocity = false
                    )
                }
            }
        }.runTaskTimer(plugin, 0L, 10L)
    }

    private fun randomLocationFromBoundingBox(boundingBox: BoundingBox): Location {
        return Location(Bukkit.getWorlds()[0],
            Random.nextDouble(boundingBox.minX, boundingBox.maxX),
            Random.nextDouble(boundingBox.minY, boundingBox.maxY),
            Random.nextDouble(boundingBox.minZ, boundingBox.maxZ)
        )
    }
}