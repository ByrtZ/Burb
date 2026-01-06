package dev.byrt.burb.game.location

import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.team.Teams

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

//TODO: Dynamic system depending on capture point ownership
object SpawnPoints {
    private val plantSpawns = listOf(
        Location(Bukkit.getWorlds()[0], 47.5, 2.0, 92.5, -32f, 0f),
        Location(Bukkit.getWorlds()[0], -21.5, 2.0, 97.5, -130f, 0f),
        Location(Bukkit.getWorlds()[0], -53.5, 3.0, 137.5, -90f, 0f),
        Location(Bukkit.getWorlds()[0], 52.5, 2.0, 156.5, 165f, 0f)
    )
    private val zombieSpawns = listOf(
        Location(Bukkit.getWorlds()[0], -23.5, 2.0, 153.5, -130f, 0f),
        Location(Bukkit.getWorlds()[0], 87.5, 2.0, 136.5, 140f, 0f),
        Location(Bukkit.getWorlds()[0], 28.5, 1.0, 97.5, 90f, 0f),
        Location(Bukkit.getWorlds()[0], -29.5, 7.0, 114.0, -90f, 0f)
    )

    // TODO: BOUNDING BOXES
    /*val lobbyBoundingBox = BoundingBox()
    val inGameBoundingBox = BoundingBox()*/

    fun respawnLocation(player: Player) {
        player.teleport(
            if(GameManager.getGameState() in listOf(GameState.STARTING, GameState.IN_GAME, GameState.OVERTIME)) {
                    when(player.burbPlayer().playerTeam) {
                        Teams.PLANTS -> plantSpawns.random()
                        Teams.ZOMBIES -> zombieSpawns.random()
                        else -> {
                            Location(Bukkit.getWorlds()[0], 10.5, 0.0, 0.5, 0f, 0f)
                        }
                    }
            } else {
                Location(Bukkit.getWorlds()[0], 10.5, 0.0, 0.5, 0f, 0f)
            }
        )
    }
}