package dev.byrt.burb.chat

import dev.byrt.burb.game.*
import dev.byrt.burb.game.Timer
import dev.byrt.burb.plugin

import io.papermc.paper.scoreboard.numbers.NumberFormat

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.bossbar.BossBar.Color

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot

import java.util.*

@Suppress("DEPRECATION")
object InfoBoardManager {
    private var scoreboard = Bukkit.getScoreboardManager().mainScoreboard
    private var objective = scoreboard.registerNewObjective("${plugin.name.lowercase()}-info-${UUID.randomUUID()}", Criteria.DUMMY, Formatting.allTags.deserialize("<burbcolour><bold>${ChatUtility.BURB_FONT_TAG}SUBURBINATION<reset>"))

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
        objective.displaySlot = null
        objective.numberFormat(NumberFormat.blank())

        // Modifiable game text
        currentGameLine.addEntry(currentGameLineKey)
        currentGameLine.prefix(Formatting.allTags.deserialize("<aqua>${ChatUtility.BURB_FONT_TAG}GAME:<reset> "))
        currentGameLine.suffix(Formatting.allTags.deserialize("${ChatUtility.BURB_FONT_TAG}SUBURBINATION"))
        objective.getScore(currentGameLineKey).score = 10

        // Modifiable map text
        currentMapLine.addEntry(currentMapLineKey)
        currentMapLine.prefix(Formatting.allTags.deserialize("<aqua>${ChatUtility.BURB_FONT_TAG}MAP:<reset> "))
        currentMapLine.suffix(Formatting.allTags.deserialize("${ChatUtility.BURB_FONT_TAG}THE<reset> ${ChatUtility.BURB_FONT_TAG}BURB<reset> ${ChatUtility.BURB_FONT_TAG}ZONE"))
        objective.getScore(currentMapLineKey).score = 9

        // Modifiable round information
        currentRoundLine.addEntry(currentRoundLineKey)
        currentRoundLine.prefix(Formatting.allTags.deserialize("<green>${ChatUtility.BURB_FONT_TAG}ROUND:<reset> "))
        currentRoundLine.suffix(Formatting.allTags.deserialize("${ChatUtility.BURB_FONT_TAG}NONE"))
        objective.getScore(currentRoundLineKey).score = 8

        // Modifiable game status information
        gameStatusLine.addEntry(gameStatusLineKey)
        gameStatusLine.prefix(Formatting.allTags.deserialize("<red>${ChatUtility.BURB_FONT_TAG}GAME<reset> ${ChatUtility.BURB_FONT_TAG}<red>STATUS:<reset> "))
        gameStatusLine.suffix(Formatting.allTags.deserialize("<gray>${ChatUtility.BURB_FONT_TAG}AWAITING<reset> ${ChatUtility.BURB_FONT_TAG}<gray>PLAYERS..."))
        objective.getScore(gameStatusLineKey).score = 7

        // Static blank space
        objective.getScore("§").score = 6

        // Static score multiplier
        currentScoreLine.addEntry(currentScoreLineKey)
        currentScoreLine.prefix(Formatting.allTags.deserialize("<aqua>${ChatUtility.BURB_FONT_TAG}GAME<reset> <aqua>${ChatUtility.BURB_FONT_TAG}STANDINGS:"))
        currentScoreLine.suffix(Formatting.allTags.deserialize(""))
        objective.getScore(currentScoreLineKey).score = 5

        // Modifiable first placement score
        firstPlaceLine.addEntry(firstPlaceLineKey)
        firstPlaceLine.prefix(Formatting.allTags.deserialize("${ChatUtility.BURB_FONT_TAG}<dark_gray>-<reset> "))
        firstPlaceLine.suffix(Formatting.allTags.deserialize("<plantscolour>${ChatUtility.BURB_FONT_TAG}PLANTS<white>:<reset> ${ChatUtility.BURB_FONT_TAG}NONE"))
        objective.getScore(firstPlaceLineKey).score = 4

        // Modifiable second placement score
        secondPlaceLine.addEntry(secondPlaceLineKey)
        secondPlaceLine.prefix(Formatting.allTags.deserialize("${ChatUtility.BURB_FONT_TAG}<dark_gray>-<reset> "))
        secondPlaceLine.suffix(Formatting.allTags.deserialize("<zombiescolour>${ChatUtility.BURB_FONT_TAG}ZOMBIES<white>:<reset> ${ChatUtility.BURB_FONT_TAG}NONE"))
        objective.getScore(secondPlaceLineKey).score = 3

        // Static blank space
        objective.getScore("§§").score = 0

        plugin.logger.info("Scoreboard constructed with ID ${objective.name}...")
    }

    fun updateRound() {
        if(GameManager.getGameState() == GameState.IDLE) {
            currentRoundLine.suffix(Formatting.allTags.deserialize("${ChatUtility.BURB_FONT_TAG}NONE"))
        } else {
            currentRoundLine.suffix(Formatting.allTags.deserialize("${ChatUtility.BURB_FONT_TAG}${RoundManager.getRound().ordinal + 1}/${RoundManager.getTotalRounds()}"))
        }
    }

    fun updateStatus() {
        when(GameManager.getGameState()) {
            GameState.IDLE -> {
                gameStatusLine.prefix(Formatting.allTags.deserialize("<red>${ChatUtility.BURB_FONT_TAG}GAME<reset> ${ChatUtility.BURB_FONT_TAG}<red>STATUS:<reset> "))
                gameStatusLine.suffix(Formatting.allTags.deserialize("<gray>${ChatUtility.BURB_FONT_TAG}AWAITING<reset> ${ChatUtility.BURB_FONT_TAG}<gray>PLAYERS..."))
                objective.displaySlot = null
            }
            GameState.STARTING -> {
                objective.displaySlot = DisplaySlot.SIDEBAR
                if(RoundManager.getRound() == Round.ONE) {
                    gameStatusLine.prefix(Formatting.allTags.deserialize("<red>${ChatUtility.BURB_FONT_TAG}GAME<reset> ${ChatUtility.BURB_FONT_TAG}<red>BEGINS:<reset> "))
                } else {
                    gameStatusLine.prefix(Formatting.allTags.deserialize("<red>${ChatUtility.BURB_FONT_TAG}ROUND<reset> ${ChatUtility.BURB_FONT_TAG}<red>BEGINS:<reset> "))
                }
            }
            GameState.IN_GAME -> {
                gameStatusLine.prefix(Formatting.allTags.deserialize("<red>${ChatUtility.BURB_FONT_TAG}TIME<reset> ${ChatUtility.BURB_FONT_TAG}<red>LEFT:<reset> "))
            }
            GameState.ROUND_END -> {
                gameStatusLine.prefix(Formatting.allTags.deserialize("<red>${ChatUtility.BURB_FONT_TAG}NEXT<reset> ${ChatUtility.BURB_FONT_TAG}<red>ROUND:<reset> "))
            }
            GameState.GAME_END -> {
                gameStatusLine.prefix(Formatting.allTags.deserialize("<red>${ChatUtility.BURB_FONT_TAG}GAME<reset> ${ChatUtility.BURB_FONT_TAG}<red>ENDING:<reset> "))
            }
            GameState.OVERTIME -> {
                gameStatusLine.prefix(Formatting.allTags.deserialize("<red>${ChatUtility.BURB_FONT_TAG}OVERTIME<reset> "))
            }
        }
    }

    fun updateTimer() {
        if(GameManager.getGameState() == GameState.OVERTIME) {
            gameStatusLine.suffix(Formatting.allTags.deserialize(""))
        } else if(GameManager.getGameState() == GameState.IDLE) {
            gameStatusLine.suffix(Formatting.allTags.deserialize("<gray>${ChatUtility.BURB_FONT_TAG}AWAITING<reset> ${ChatUtility.BURB_FONT_TAG}<gray>PLAYERS..."))
        } else {
            gameStatusLine.suffix(Formatting.allTags.deserialize("${ChatUtility.BURB_FONT_TAG}${Timer.getDisplayTimer()}"))
        }
    }

    fun timerBossBar() {
        object : BukkitRunnable() {
            val timerBossBar = BossBar.bossBar(Formatting.allTags.deserialize(""), 0f, Color.RED, BossBar.Overlay.PROGRESS)
            override fun run() {
                if(GameManager.getGameState() != GameState.IDLE) {
                    for(player in Bukkit.getOnlinePlayers()) {
                        if(!player.activeBossBars().contains(timerBossBar)) timerBossBar.addViewer(player)
                    }
                }
                when(Timer.getTimerState()) {
                    TimerState.ACTIVE -> {
                        when(GameManager.getGameState()) {
                            GameState.IDLE -> {
                                for(player in Bukkit.getOnlinePlayers()) {
                                    timerBossBar.removeViewer(player)
                                }
                                this.cancel()
                            }
                            GameState.STARTING -> {
                                timerBossBar.name(Formatting.allTags.deserialize("${ChatUtility.NO_SHADOW_TAG}\uD011<reset><translate:space.-122>${ChatUtility.BURB_FONT_TAG}GAME  STARTING  IN:  ${if(Timer.getTimer() <= 9) "<red>" else "<#ffff00>"}${Timer.getDisplayTimer()}<reset>"))
                            }
                            GameState.IN_GAME -> {
                                timerBossBar.name(Formatting.allTags.deserialize("${ChatUtility.NO_SHADOW_TAG}\uD011<reset><translate:space.-104>${ChatUtility.BURB_FONT_TAG}TIME  LEFT:  ${if(Timer.getTimer() <= 89) "<red>" else "<#ffff00>"}${Timer.getDisplayTimer()}<reset>"))
                            }
                            GameState.ROUND_END -> {
                                timerBossBar.name(Formatting.allTags.deserialize("${ChatUtility.NO_SHADOW_TAG}\uD011<reset><translate:space.-115>${ChatUtility.BURB_FONT_TAG}NEXT  ROUND  IN:  <#ffff00>${Timer.getDisplayTimer()}<reset>"))
                            }
                            GameState.GAME_END -> {
                                timerBossBar.name(Formatting.allTags.deserialize("${ChatUtility.NO_SHADOW_TAG}\uD011<reset><translate:space.-107>${ChatUtility.BURB_FONT_TAG}BACK  TO  HUB:  <#ffff00>${Timer.getDisplayTimer()}<reset>"))
                            }
                            GameState.OVERTIME -> {
                                timerBossBar.name(Formatting.allTags.deserialize("${ChatUtility.NO_SHADOW_TAG}\uD011<reset><translate:space.-95>${ChatUtility.BURB_FONT_TAG}<red>OVERTIME<reset>"))
                            }
                        }
                    }
                    TimerState.INACTIVE -> {
                        timerBossBar.name(Formatting.allTags.deserialize("${ChatUtility.NO_SHADOW_TAG}\uD011<reset><translate:space.-85>${ChatUtility.BURB_FONT_TAG}<red>TIMER  UNAVAILABLE<reset>"))
                    }
                    TimerState.PAUSED -> {
                        timerBossBar.name(Formatting.allTags.deserialize("${ChatUtility.NO_SHADOW_TAG}\uD011<reset><translate:space.-100>${ChatUtility.BURB_FONT_TAG}TIMER  PAUSED<reset>"))
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L)
    }

    fun updateScore() {
        if(GameManager.getGameState() == GameState.IDLE) {
            firstPlaceLine.prefix(Formatting.allTags.deserialize("${ChatUtility.BURB_FONT_TAG}<dark_gray>-<reset> "))
            firstPlaceLine.suffix(Formatting.allTags.deserialize("<plantscolour>${ChatUtility.BURB_FONT_TAG}PLANTS<white>:<reset> ${ChatUtility.BURB_FONT_TAG}NONE"))
            secondPlaceLine.prefix(Formatting.allTags.deserialize("${ChatUtility.BURB_FONT_TAG}<dark_gray>-<reset> "))
            secondPlaceLine.suffix(Formatting.allTags.deserialize("<zombiescolour>${ChatUtility.BURB_FONT_TAG}ZOMBIES<white>:<reset> ${ChatUtility.BURB_FONT_TAG}NONE"))
        } else {
            val placementKeys = ScoreManager.getPlacementMap().keys.toTypedArray()
            val placementValues = ScoreManager.getPlacementMap().values.toTypedArray()
            firstPlaceLine.prefix(Formatting.allTags.deserialize("${ChatUtility.BURB_FONT_TAG}1.<reset> "))
            firstPlaceLine.suffix(Formatting.allTags.deserialize("${placementKeys[0].teamColourTag}${ChatUtility.BURB_FONT_TAG}${placementKeys[0].teamName.uppercase()}<white>:<reset> ${ChatUtility.BURB_FONT_TAG}${placementValues[0]}"))
            secondPlaceLine.prefix(Formatting.allTags.deserialize("${ChatUtility.BURB_FONT_TAG}2.<reset> "))
            secondPlaceLine.suffix(Formatting.allTags.deserialize("${placementKeys[1].teamColourTag}${ChatUtility.BURB_FONT_TAG}${placementKeys[1].teamName.uppercase()}<white>:<reset> ${ChatUtility.BURB_FONT_TAG}${placementValues[1]}"))
        }
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