package dev.byrt.burb.game

import dev.byrt.burb.cosmetics.shop.BurbStickerShop
import dev.byrt.burb.game.location.SpawnPoints
import dev.byrt.burb.game.objective.CapturePoints
import dev.byrt.burb.game.visual.GameDayTime
import dev.byrt.burb.game.visual.GameVisuals
import dev.byrt.burb.item.ItemManager
import dev.byrt.burb.item.ServerItem
import dev.byrt.burb.lobby.BurbLobby
import dev.byrt.burb.lobby.LobbyBall
import dev.byrt.burb.lobby.npc.BurbNPCs
import dev.byrt.burb.music.Jukebox
import dev.byrt.burb.music.Music
import dev.byrt.burb.text.ChatUtility
import dev.byrt.burb.text.InfoBoardManager
import dev.byrt.burb.util.CommitIntegration
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location

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
        CommitIntegration.grabLatestCommit()
        BurbLobby.createTutorialBoards()
        LobbyBall.createLobbyBall()
        BurbNPCs.spawnAllNPCs()
        BurbStickerShop.createStickerShop(Location(Bukkit.getWorlds()[0], -39.5, 8.0, 20.875, 0f, 0f))
        GameVisuals.resetDayTime()
    }

    fun cleanup() {
        BurbNPCs.clearNPCs()
        InfoBoardManager.destroyScoreboard()
        BurbLobby.destroyTutorialBoards()
        BurbStickerShop.destroyShops()
        LobbyBall.cleanup()
        CommitIntegration.destroyUpdatesBoard()
        ItemManager.destroyBullets()
        GameVisuals.resetDayTime()
        CapturePoints.clearCapturePoints()
    }

    fun reload() {
        InfoBoardManager.updateStatus()
        InfoBoardManager.updateRound()
        InfoBoardManager.updateTimer()
        Scores.setPlantsScore(0)
        Scores.setZombiesScore(0)
        CapturePoints.clearCapturePoints()
        Jukebox.resetMusicStress()
        GameVisuals.setDayTime(GameDayTime.DAY)
        GameManager.teams.teamGlowingEnabled = false
        for(player in Bukkit.getOnlinePlayers()) {
            SpawnPoints.respawnLocation(player)
            Jukebox.disconnect(player)
            Jukebox.startMusicLoop(player, Music.LOBBY_WAITING)
            player.gameMode = GameMode.ADVENTURE
            if(!player.inventory.contains(ServerItem.getProfileItem())) player.inventory.setItem(0, ServerItem.getProfileItem())
        }
        LobbyBall.createLobbyBall()
    }
}