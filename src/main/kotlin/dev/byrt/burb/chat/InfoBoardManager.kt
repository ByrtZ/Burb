package dev.byrt.burb.chat

import dev.byrt.burb.game.*
import dev.byrt.burb.game.Timer
import dev.byrt.burb.plugin

import io.papermc.paper.scoreboard.numbers.NumberFormat

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot

import java.util.*

@Suppress("DEPRECATION")
object InfoBoardManager {
    private var scoreboard = Bukkit.getScoreboardManager().mainScoreboard
    private var objective = scoreboard.registerNewObjective("${plugin.name.lowercase()}-info-${UUID.randomUUID()}", Criteria.DUMMY, Formatting.allTags.deserialize("<burbcolour><bold><font:burb:font>SUBURBINATION<reset>"))

    private var currentGameLine = scoreboard.registerNewTeam("currentGameLine")
    private val currentGameLineKey = ChatColor.STRIKETHROUGH.toString()

    private var currentMapLine = scoreboard.registerNewTeam("currentMapLine")
    private val currentMapLineKey = ChatColor.BOLD.toString()

    private var currentScoreLine = scoreboard.registerNewTeam("currentScoreLine")
    private val currentScoreLineKey = ChatColor.DARK_PURPLE.toString()

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

        // Modifiable game text
        currentGameLine.addEntry(currentGameLineKey)
        currentGameLine.prefix(Formatting.allTags.deserialize("<aqua><font:burb:font>GAME:<reset> "))
        currentGameLine.suffix(Formatting.allTags.deserialize("<font:burb:font>SUBURBINATION"))
        objective.getScore(currentGameLineKey).score = 10

        // Modifiable map text
        currentMapLine.addEntry(currentMapLineKey)
        currentMapLine.prefix(Formatting.allTags.deserialize("<aqua><font:burb:font>MAP:<reset> "))
        currentMapLine.suffix(Formatting.allTags.deserialize("<font:burb:font>THE<reset> <font:burb:font>BURB<reset> <font:burb:font>ZONE"))
        objective.getScore(currentMapLineKey).score = 9

        // Modifiable round information
        currentRoundLine.addEntry(currentRoundLineKey)
        currentRoundLine.prefix(Formatting.allTags.deserialize("<green><font:burb:font>ROUND:<reset> "))
        currentRoundLine.suffix(Formatting.allTags.deserialize("<font:burb:font>NONE"))
        objective.getScore(currentRoundLineKey).score = 8

        // Modifiable game status information
        gameStatusLine.addEntry(gameStatusLineKey)
        gameStatusLine.prefix(Formatting.allTags.deserialize("<red><font:burb:font>GAME<reset> <font:burb:font><red>STATUS:<reset> "))
        gameStatusLine.suffix(Formatting.allTags.deserialize("<gray><font:burb:font>AWAITING<reset> <font:burb:font><gray>PLAYERS..."))
        objective.getScore(gameStatusLineKey).score = 7

        // Static blank space
        objective.getScore("§").score = 6

        // Static score multiplier
        currentScoreLine.addEntry(currentScoreLineKey)
        currentScoreLine.prefix(Formatting.allTags.deserialize("<aqua><font:burb:font>GAME<reset> <aqua><font:burb:font>STANDINGS:"))
        currentScoreLine.suffix(Formatting.allTags.deserialize(""))
        objective.getScore(currentScoreLineKey).score = 5

        // Modifiable first placement score
        firstPlaceLine.addEntry(firstPlaceLineKey)
        firstPlaceLine.prefix(Formatting.allTags.deserialize("<font:burb:font>1.<reset> "))
        firstPlaceLine.suffix(Formatting.allTags.deserialize("<plantscolour><font:burb:font>PLANTS<white>:<reset> <font:burb:font>0"))
        objective.getScore(firstPlaceLineKey).score = 4

        // Modifiable second placement score
        secondPlaceLine.addEntry(secondPlaceLineKey)
        secondPlaceLine.prefix(Formatting.allTags.deserialize("<font:burb:font>2.<reset> "))
        secondPlaceLine.suffix(Formatting.allTags.deserialize("<zombiescolour><font:burb:font>ZOMBIES<white>:<reset> <font:burb:font>0"))
        objective.getScore(secondPlaceLineKey).score = 3

        // Static blank space
        objective.getScore("§§").score = 0

        plugin.logger.info("Scoreboard constructed with ID ${objective.name}...")
    }

    fun updateRound() {
        if(GameManager.getGameState() == GameState.IDLE) {
            currentRoundLine.suffix(Formatting.allTags.deserialize("<font:burb:font>NONE"))
        } else {
            currentRoundLine.suffix(Formatting.allTags.deserialize("<font:burb:font>${RoundManager.getRound().ordinal + 1}/${RoundManager.getTotalRounds()}"))
        }
    }

    fun updateStatus() {
        when(GameManager.getGameState()) {
            GameState.IDLE -> {
                gameStatusLine.prefix(Formatting.allTags.deserialize("<red><font:burb:font>GAME<reset> <font:burb:font><red>STATUS:<reset> "))
                gameStatusLine.suffix(Formatting.allTags.deserialize("<gray><font:burb:font>AWAITING<reset> <font:burb:font><gray>PLAYERS..."))
            }
            GameState.STARTING -> {
                if(RoundManager.getRound() == Round.ONE) {
                    gameStatusLine.prefix(Formatting.allTags.deserialize("<red><font:burb:font>GAME<reset> <font:burb:font><red>BEGINS:<reset> "))
                } else {
                    gameStatusLine.prefix(Formatting.allTags.deserialize("<red><font:burb:font>ROUND<reset> <font:burb:font><red>BEGINS:<reset> "))
                }
            }
            GameState.IN_GAME -> {
                gameStatusLine.prefix(Formatting.allTags.deserialize("<red><font:burb:font>TIME<reset> <font:burb:font><red>LEFT:<reset> "))
            }
            GameState.ROUND_END -> {
                gameStatusLine.prefix(Formatting.allTags.deserialize("<red><font:burb:font>NEXT<reset> <font:burb:font><red>ROUND:<reset> "))
            }
            GameState.GAME_END -> {
                gameStatusLine.prefix(Formatting.allTags.deserialize("<red><font:burb:font>GAME<reset> <font:burb:font><red>ENDING:<reset> "))
            }
            GameState.OVERTIME -> {
                gameStatusLine.prefix(Formatting.allTags.deserialize("<red><font:burb:font>OVERTIME<reset> "))
            }
        }
    }

    fun updateTimer() {
        if(GameManager.getGameState() == GameState.OVERTIME) {
            gameStatusLine.suffix(Formatting.allTags.deserialize(""))
        } else {
            gameStatusLine.suffix(Formatting.allTags.deserialize("<font:burb:font>${Timer.getDisplayTimer()}"))
        }
    }

    fun updatePlacements() {
        if(GameManager.getGameState() == GameState.IDLE) {
            firstPlaceLine.suffix(Formatting.allTags.deserialize("<plantscolour><font:burb:font>PLANTS<white>:<reset> <font:burb:font>0"))
            secondPlaceLine.suffix(Formatting.allTags.deserialize("<zombiescolour><font:burb:font>ZOMBIES<white>:<reset> <font:burb:font>0"))
        } else {
            firstPlaceLine.suffix(Formatting.allTags.deserialize("<plantscolour><font:burb:font>PLANTS<white>:<reset> <font:burb:font>0"))
            secondPlaceLine.suffix(Formatting.allTags.deserialize("<zombiescolour><font:burb:font>ZOMBIES<white>:<reset> <font:burb:font>0"))
        }
    }

    fun showScoreboard(player : Player) {
        player.scoreboard = scoreboard
    }

    fun destroyScoreboard() {
        currentGameLine.unregister()
        currentMapLine.unregister()
        currentScoreLine.unregister()
        currentRoundLine.unregister()
        gameStatusLine.unregister()
        firstPlaceLine.unregister()
        secondPlaceLine.unregister()
        objective.unregister()
    }
}