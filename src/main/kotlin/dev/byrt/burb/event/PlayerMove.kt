package dev.byrt.burb.event

import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.lobby.LobbyBall
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.team.Teams

import org.bukkit.Bukkit
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
                    val direction = player.location.toVector().subtract(ballLocation.toVector()).normalize().multiply(-0.75)
                    direction.y += 0.25
                    ballPhysics.applyForce(direction)
                }
            }
        }
        if(GameManager.getGameState() == GameState.STARTING) {
            for(player in Bukkit.getOnlinePlayers().filter { online -> online.burbPlayer().playerTeam in listOf(Teams.PLANTS, Teams.ZOMBIES) }) {
                val to = e.from
                to.pitch = e.to.pitch
                to.yaw = e.to.yaw
                e.to = to
            }
        }
    }
}