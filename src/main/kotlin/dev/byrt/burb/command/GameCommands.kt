package dev.byrt.burb.command

import dev.byrt.burb.text.ChatUtility
import dev.byrt.burb.game.Game
import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.game.Rounds
import dev.byrt.burb.game.events.SpecialEvent
import dev.byrt.burb.game.events.SpecialEvents

import org.bukkit.command.CommandSender
import org.incendo.cloud.annotations.Argument

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
    fun start(sender: CommandSender) {
        if(GameManager.getGameState() == GameState.IDLE) {
            //TODO: Add team quantity check
            ChatUtility.broadcastDev("<yellow>${sender.name}<green> started a Suburbination game.", false)
            Game.start()
        } else {
            return
        }
    }

    @Command("game stop")
    @CommandDescription("Stops the game.")
    @Permission("burb.cmd.game")
    @Confirmation
    fun stop(sender: CommandSender) {
        if(GameManager.getGameState() != GameState.IDLE) {
            ChatUtility.broadcastDev("<yellow>${sender.name}<red> stopped the Suburbination game.", false)
            Game.stop()
        } else {
            return
        }
    }

    @Command("game reload")
    @CommandDescription("Reloads the game.")
    @Permission("burb.cmd.game")
    fun reload(sender: CommandSender) {
        if(GameManager.getGameState() == GameState.GAME_END) {
            ChatUtility.broadcastDev("${sender.name} reloaded the game.", false)
            Game.reload()
        } else {
            return
        }
    }

    @Command("rounds set <amount>")
    @CommandDescription("Sets the total number of rounds.")
    @Permission("burb.cmd.game")
    fun setRounds(sender: CommandSender, @Argument amount: Int) {
        if(amount !in 1..3) return
        if(GameManager.getGameState() == GameState.IDLE) {
            ChatUtility.broadcastDev("<yellow>${sender.name} <gray>set the total number of rounds to $amount.", false)
            Rounds.setTotalRounds(amount)
        } else {
            return
        }
    }

    @Command("event start <event>")
    @CommandDescription("Starts the specified event.")
    @Permission("burb.cmd.game")
    fun startEvent(sender: CommandSender, @Argument("event") newEvent: SpecialEvent) {
        if(GameManager.getGameState() == GameState.IN_GAME) {
            SpecialEvents.startEvent(newEvent)
        } else {
            return
        }
    }
}