package dev.byrt.burb.game

import dev.byrt.burb.text.ChatUtility
import dev.byrt.burb.text.InfoBoardManager
import dev.byrt.burb.game.location.SpawnPoints
import dev.byrt.burb.game.objective.CapturePoints
import dev.byrt.burb.item.ItemManager
import dev.byrt.burb.lobby.LobbyBall
import dev.byrt.burb.music.Jukebox
import dev.byrt.burb.music.Music
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.team.TeamManager
import dev.byrt.burb.team.Teams
import dev.byrt.burb.util.CommitGrabber

import org.bukkit.Bukkit

object Game {
    fun start() {
        if(GameManager.getGameState() == GameState.IDLE) {
            GameManager.nextState()
        } else {
            ChatUtility.broadcastDev("<red>Unable to start game, as game is already running.", false)
        }
    }

    fun stop() {
        if(GameManager.getGameState() == GameState.IDLE) {
            ChatUtility.broadcastDev("<red>Unable to stop game, as no game is running.", false)
        } else {
            GameManager.setGameState(GameState.GAME_END)
        }
    }

    fun setup() {
        InfoBoardManager.buildScoreboard()
        TeamManager.buildDisplayTeams()
        CommitGrabber.grabLatestCommit()
    }

    fun cleanup() {
        TeamManager.destroyDisplayTeams()
        InfoBoardManager.destroyScoreboard()
        CapturePoints.clearCapturePoints()
        ItemManager.destroyBullets()
        LobbyBall.cleanup()
    }

    fun reload() {
        InfoBoardManager.updateStatus()
        InfoBoardManager.updateRound()
        InfoBoardManager.updateTimer()
        Scores.setPlantsScore(0)
        Scores.setZombiesScore(0)
        CapturePoints.clearCapturePoints()
        Jukebox.resetMusicStress()
        for(player in Bukkit.getOnlinePlayers()) {
            if(player.burbPlayer().playerTeam !in listOf(Teams.SPECTATOR, Teams.NULL)) {
                TeamManager.disableTeamGlowing(player)
            }
            SpawnPoints.respawnLocation(player)
            Jukebox.disconnect(player)
            Jukebox.startMusicLoop(player, Music.LOBBY_WAITING)
        }
    }
}