package dev.byrt.burb.event

import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.misc.LobbyBall

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

@Suppress("unused")
class PlayerMove: Listener {
    @EventHandler
    private fun onMove(e: PlayerMoveEvent) {
        if(GameManager.getGameState() == GameState.IDLE) {
            val player = e.player
            LobbyBall.getBallMap().values.forEach { ballPhysics ->
                val ball = ballPhysics.ball
                val ballLocation = ball.location
                if(player.location.world == ballLocation.world && player.location.distanceSquared(ballLocation) < 1.75) {
                    val direction = player.location.toVector().subtract(ballLocation.toVector()).normalize().multiply(-1.25)
                    direction.y += 0.35
                    ballPhysics.applyForce(direction)
                }
            }
        }
    }
}