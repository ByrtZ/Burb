package dev.byrt.burb.command

import dev.byrt.burb.chat.ChatUtility
import dev.byrt.burb.game.Game
import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState

import io.papermc.paper.command.brigadier.CommandSourceStack

import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.annotations.processing.CommandContainer
import org.incendo.cloud.processors.confirmation.annotation.Confirmation

@Suppress("unused", "unstableApiUsage")
@CommandContainer
class GameCommands {
    @Command("game start")
    @CommandDescription("Starts the game.")
    @Permission("burb.cmd.game")
    @Confirmation
    fun start(css: CommandSourceStack) {
        if(GameManager.getGameState() == GameState.IDLE) {
            //TODO: Add team quantity check
            ChatUtility.broadcastDev("<yellow>${css.sender.name}<green> started a Suburbination game.", false)
            Game.start()
        } else {
            return
        }
    }

    @Command("game stop")
    @CommandDescription("stop the game.")
    @Permission("burb.cmd.game")
    @Confirmation
    fun stop(css: CommandSourceStack) {
        if(GameManager.getGameState() != GameState.IDLE) {
            ChatUtility.broadcastDev("<yellow>${css.sender.name}<red> stopped the Suburbination game.", false)
            Game.stop()
        } else {
            return
        }
    }

    @Command("game reload")
    @CommandDescription("Reloads the game.")
    @Permission("burb.cmd.game")
    fun reload(css: CommandSourceStack) {
        if(GameManager.getGameState() == GameState.GAME_END) {
            ChatUtility.broadcastDev("${css.sender.name} reloaded the game.", false)
            Game.reload()
        } else {
            return
        }
    }
}