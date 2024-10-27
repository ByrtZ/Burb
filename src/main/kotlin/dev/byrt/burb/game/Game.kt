package dev.byrt.burb.game

import dev.byrt.burb.chat.ChatUtility
import dev.byrt.burb.chat.InfoBoardManager
import dev.byrt.burb.music.Jukebox
import dev.byrt.burb.music.Music
import dev.byrt.burb.plugin
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
    }

    fun cleanup() {
        InfoBoardManager.destroyScoreboard()
        CapturePointManager.testDestroyCapPoints()
    }

    fun reload() {
        InfoBoardManager.updateStatus()
        InfoBoardManager.updateRound()
        InfoBoardManager.updateTimer()
        CapturePointManager.testDestroyCapPoints()
        for(player in Bukkit.getOnlinePlayers()) {
            Jukebox.startMusicLoop(player, plugin, Music.LOBBY_WAITING)
        }
    }
}