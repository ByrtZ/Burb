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
        Location(Bukkit.getWorlds()[0], 47.5, 2.0, 92.5, -32.0f, 0.0f)
    )
    private val zombieSpawns = listOf(
        Location(Bukkit.getWorlds()[0], -23.5, 2.0, 153.5, -130.0f, 0.0f)
    )

    fun respawnLocation(player: Player) {
        player.teleport(
            if(GameManager.getGameState() in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
                    when(player.burbPlayer().playerTeam) {
                        Teams.PLANTS -> plantSpawns.first()
                        Teams.ZOMBIES -> zombieSpawns.first()
                        else -> {
                            Bukkit.getWorlds()[0].spawnLocation
                        }
                    }
            } else {
                Bukkit.getWorlds()[0].spawnLocation
            }
        )
    }
}