package dev.byrt.burb.chat

import dev.byrt.burb.game.*
import dev.byrt.burb.game.Timer
import dev.byrt.burb.plugin

import io.papermc.paper.scoreboard.numbers.NumberFormat

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot

import java.util.*

object InfoBoardManager {
    private var scoreboard = Bukkit.getScoreboardManager().mainScoreboard
    private var objective = scoreboard.registerNewObjective("${plugin.name.lowercase()}-info-${UUID.randomUUID()}", Criteria.DUMMY, Formatting.allTags.deserialize("<burbcolour><bold>Suburbination<reset>"))

    private var currentRoundLine = scoreboard.registerNewTeam("currentRoundLine")
    private val currentRoundLineKey = ChatColor.ITALIC.toString()

    private var gameStatusLine = scoreboard.registerNewTeam("gameStatusLine")
    private val gameStatusLineKey = ChatColor.MAGIC.toString()

    private var firstPlaceLine = scoreboard.registerNewTeam("firstPlaceLine")
    private val firstPlaceLineKey = ChatColor.UNDERLINE.toString()

    private var secondPlaceLine = scoreboard.registerNewTeam("secondPlaceLine")
    private val secondPlaceLineKey = ChatColor.LIGHT_PURPLE.toString()

    fun buildScoreboard() {
        plugin.logger.info("Building scoreboard...")
        objective.displaySlot = DisplaySlot.SIDEBAR
        objective.numberFormat(NumberFormat.blank())

        // Static game text
        objective.getScore(ChatColor.AQUA.toString() + "" + ChatColor.BOLD + "Game: " + ChatColor.RESET + "Suburbination").score = 10

        // Static map text
        objective.getScore(ChatColor.AQUA.toString() + "" + ChatColor.BOLD + "Map: " + ChatColor.RESET + "the burb zone").score = 9

        // Modifiable round information
        currentRoundLine.addEntry(currentRoundLineKey)
        currentRoundLine.prefix(Component.text("Round: ", NamedTextColor.GREEN, TextDecoration.BOLD))
        currentRoundLine.suffix(Component.text("None"))
        objective.getScore(currentRoundLineKey).score = 8

        // Modifiable game status information
        gameStatusLine.addEntry(gameStatusLineKey)
        gameStatusLine.prefix(Component.text("Game status: ", NamedTextColor.RED, TextDecoration.BOLD))
        gameStatusLine.suffix(Component.text("Awaiting players...", NamedTextColor.GRAY))
        objective.getScore(gameStatusLineKey).score = 7

        // Static blank space
        objective.getScore("§").score = 6

        // Static score multiplier
        objective.getScore(ChatColor.AQUA.toString() + "" + ChatColor.BOLD + "Game Score:").score = 5

        // Modifiable first placement score
        firstPlaceLine.addEntry(firstPlaceLineKey)
        firstPlaceLine.prefix(Component.text(" 1. "))
        firstPlaceLine.suffix(Formatting.allTags.deserialize("<plantscolour>Plants<reset>               0"))
        objective.getScore(firstPlaceLineKey).score = 4

        // Modifiable second placement score
        secondPlaceLine.addEntry(secondPlaceLineKey)
        secondPlaceLine.prefix(Component.text(" 2. "))
        secondPlaceLine.suffix(Formatting.allTags.deserialize("<zombiescolour>Zombies<reset>             0"))
        objective.getScore(secondPlaceLineKey).score = 3

        // Static blank space
        objective.getScore("§§").score = 0

        plugin.logger.info("Scoreboard constructed with ID ${objective.name}...")
    }

    fun updateRound() {
        if(GameManager.getGameState() == GameState.IDLE) {
            currentRoundLine.suffix(Component.text("None"))
        } else {
            currentRoundLine.suffix(Component.text("${RoundManager.getRound().ordinal + 1}/${RoundManager.getTotalRounds()}", NamedTextColor.WHITE))
        }
    }

    fun updateStatus() {
        when(GameManager.getGameState()) {
            GameState.IDLE -> {
                gameStatusLine.prefix(Component.text("Game status: ", NamedTextColor.RED, TextDecoration.BOLD))
                gameStatusLine.suffix(Component.text("Awaiting players...", NamedTextColor.GRAY))
            }
            GameState.STARTING -> {
                if(RoundManager.getRound() == Round.ONE) {
                    gameStatusLine.prefix(Component.text("Game begins: ", NamedTextColor.RED, TextDecoration.BOLD))
                } else {
                    gameStatusLine.prefix(Component.text("Round begins: ", NamedTextColor.RED, TextDecoration.BOLD))
                }
            }
            GameState.IN_GAME -> {
                gameStatusLine.prefix(Component.text("Time left: ", NamedTextColor.RED, TextDecoration.BOLD))
            }
            GameState.ROUND_END -> {
                gameStatusLine.prefix(Component.text("Next round: ", NamedTextColor.RED, TextDecoration.BOLD))
            }
            GameState.GAME_END -> {
                gameStatusLine.prefix(Component.text("Game ending: ", NamedTextColor.RED, TextDecoration.BOLD))
            }
            GameState.OVERTIME -> {
                gameStatusLine.prefix(Component.text("OVERTIME", NamedTextColor.RED, TextDecoration.BOLD))
            }
        }
    }

    fun updateTimer() {
        if(GameManager.getGameState() == GameState.OVERTIME) {
            gameStatusLine.suffix(Component.text("", NamedTextColor.WHITE))
        } else {
            gameStatusLine.suffix(Component.text(Timer.getDisplayTimer(), NamedTextColor.WHITE))
        }
    }

    fun updatePlacements() {
        if(GameManager.getGameState() == GameState.IDLE) {
            firstPlaceLine.suffix(Formatting.allTags.deserialize("<plantscolour>Plants<reset>               0"))
            secondPlaceLine.suffix(Formatting.allTags.deserialize("<zombiescolour>Zombies<reset>             0"))
        } else {
            firstPlaceLine.suffix(Formatting.allTags.deserialize("<plantscolour>Plants<reset>               0"))
            secondPlaceLine.suffix(Formatting.allTags.deserialize("<zombiescolour>Zombies<reset>             0"))
        }
    }

    fun showScoreboard(player : Player) {
        player.scoreboard = scoreboard
    }

    fun destroyScoreboard() {
        currentRoundLine.unregister()
        gameStatusLine.unregister()
        firstPlaceLine.unregister()
        secondPlaceLine.unregister()
        objective.unregister()
    }
}