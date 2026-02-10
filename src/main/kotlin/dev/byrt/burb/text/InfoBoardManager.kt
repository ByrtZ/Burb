package dev.byrt.burb.text

import dev.byrt.burb.game.*
import dev.byrt.burb.game.Timer
import dev.byrt.burb.game.objective.CapturePoint
import dev.byrt.burb.game.objective.CapturePoints
import dev.byrt.burb.plugin
import dev.byrt.burb.team.BurbTeam
import dev.byrt.burb.team.Teams

import io.papermc.paper.scoreboard.numbers.NumberFormat
import me.lucyydotp.tinsel.font.Spacing

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.bossbar.BossBar.Color
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.translatable
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot

import java.util.*

@Suppress("DEPRECATION")
object InfoBoardManager {
    private var scoreboard = Bukkit.getScoreboardManager().mainScoreboard
    private var objective = scoreboard.registerNewObjective("${plugin.name.lowercase()}-info-${UUID.randomUUID()}", Criteria.DUMMY, Formatting.allTags.deserialize("<burbcolour><bold>${ChatUtility.BURB_FONT_TAG}SUBURBIA<reset>"))

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
        currentMapLine.suffix(Formatting.allTags.deserialize("${ChatUtility.BURB_FONT_TAG}THE BURB ZONE"))
        objective.getScore(currentMapLineKey).score = 9

        // Modifiable round information
        currentRoundLine.addEntry(currentRoundLineKey)
        currentRoundLine.prefix(Formatting.allTags.deserialize("<green>${ChatUtility.BURB_FONT_TAG}ROUND:<reset> "))
        currentRoundLine.suffix(Formatting.allTags.deserialize("${ChatUtility.BURB_FONT_TAG}NONE"))
        objective.getScore(currentRoundLineKey).score = 8

        // Modifiable game status information
        gameStatusLine.addEntry(gameStatusLineKey)
        gameStatusLine.prefix(Formatting.allTags.deserialize("<red>${ChatUtility.BURB_FONT_TAG}GAME STATUS:<reset> "))
        gameStatusLine.suffix(Formatting.allTags.deserialize("<gray>${ChatUtility.BURB_FONT_TAG}AWAITING PLAYERS..."))
        objective.getScore(gameStatusLineKey).score = 7

        // Static blank space
        objective.getScore("§").score = 6

        // Static score multiplier
        currentScoreLine.addEntry(currentScoreLineKey)
        currentScoreLine.prefix(Formatting.allTags.deserialize("<aqua>${ChatUtility.BURB_FONT_TAG}GAME STANDINGS:"))
        currentScoreLine.suffix(Formatting.allTags.deserialize(""))
        objective.getScore(currentScoreLineKey).score = 5

        // Modifiable first placement score
        firstPlaceLine.addEntry(firstPlaceLineKey)
        firstPlaceLine.prefix(Formatting.allTags.deserialize("${ChatUtility.BURB_FONT_TAG}<dark_gray>-<reset> "))
        firstPlaceLine.suffix(Formatting.allTags.deserialize("<plantscolour>${ChatUtility.BURB_FONT_TAG}PLANTS<white>: NONE"))
        objective.getScore(firstPlaceLineKey).score = 4

        // Modifiable second placement score
        secondPlaceLine.addEntry(secondPlaceLineKey)
        secondPlaceLine.prefix(Formatting.allTags.deserialize("${ChatUtility.BURB_FONT_TAG}<dark_gray>-<reset> "))
        secondPlaceLine.suffix(Formatting.allTags.deserialize("<zombiescolour>${ChatUtility.BURB_FONT_TAG}ZOMBIES<white>: NONE"))
        objective.getScore(secondPlaceLineKey).score = 3

        // Static blank space
        objective.getScore("§§").score = 0

        plugin.logger.info("Scoreboard constructed with ID ${objective.name}...")
    }

    fun updateRound() {
        if(GameManager.getGameState() == GameState.IDLE) {
            currentRoundLine.suffix(Formatting.allTags.deserialize("${ChatUtility.BURB_FONT_TAG}NONE"))
        } else {
            currentRoundLine.suffix(Formatting.allTags.deserialize("${ChatUtility.BURB_FONT_TAG}${Rounds.getRound().ordinal + 1}/${Rounds.getTotalRounds()}"))
        }
    }

    fun updateStatus() {
        when(GameManager.getGameState()) {
            GameState.IDLE -> {
                gameStatusLine.prefix(Formatting.allTags.deserialize("<red>${ChatUtility.BURB_FONT_TAG}GAME STATUS:<reset> "))
                gameStatusLine.suffix(Formatting.allTags.deserialize("<gray>${ChatUtility.BURB_FONT_TAG}AWAITING<reset> ${ChatUtility.BURB_FONT_TAG}<gray>PLAYERS..."))
                objective.displaySlot = null
            }
            GameState.STARTING -> {
                objective.displaySlot = DisplaySlot.SIDEBAR
                if(Rounds.getRound() == Round.ONE) {
                    gameStatusLine.prefix(Formatting.allTags.deserialize("<red>${ChatUtility.BURB_FONT_TAG}GAME BEGINS:<reset> "))
                } else {
                    gameStatusLine.prefix(Formatting.allTags.deserialize("<red>${ChatUtility.BURB_FONT_TAG}ROUND BEGINS:<reset> "))
                }
            }
            GameState.IN_GAME -> {
                gameStatusLine.prefix(Formatting.allTags.deserialize("<red>${ChatUtility.BURB_FONT_TAG}TIME LEFT:<reset> "))
            }
            GameState.ROUND_END -> {
                gameStatusLine.prefix(Formatting.allTags.deserialize("<red>${ChatUtility.BURB_FONT_TAG}NEXT ROUND:<reset> "))
            }
            GameState.GAME_END -> {
                gameStatusLine.prefix(Formatting.allTags.deserialize("<red>${ChatUtility.BURB_FONT_TAG}GAME ENDING:<reset> "))
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
            gameStatusLine.suffix(Formatting.allTags.deserialize("<gray>${ChatUtility.BURB_FONT_TAG}AWAITING PLAYERS..."))
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
                                timerBossBar.name(TextAlignment.centreBossBarText("GAME STARTING IN: ${if(Timer.getTimer() <= 9) "<red>" else "<#ffff00>"}${Timer.getDisplayTimer()}"))
                            }
                            GameState.IN_GAME -> {
                                timerBossBar.name(TextAlignment.centreBossBarText("TIME LEFT: ${if (Timer.getTimer() <= 89) "<red>" else "<#ffff00>"}${Timer.getDisplayTimer()}"))
                            }
                            GameState.ROUND_END -> {
                                timerBossBar.name(TextAlignment.centreBossBarText("NEXT ROUND IN: <#ffff00>${Timer.getDisplayTimer()}"))
                            }
                            GameState.GAME_END -> {
                                timerBossBar.name(TextAlignment.centreBossBarText("BACK TO HUB: <#ffff00>${Timer.getDisplayTimer()}"))
                            }
                            GameState.OVERTIME -> {
                                timerBossBar.name(TextAlignment.centreBossBarText("<red>OVERTIME"))
                            }
                        }
                    }
                    TimerState.INACTIVE -> {
                        timerBossBar.name(TextAlignment.centreBossBarText("<red>TIMER UNAVAILABLE"))
                    }
                    TimerState.PAUSED -> {
                        timerBossBar.name(TextAlignment.centreBossBarText("TIMER PAUSED"))
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L)
    }

    fun scoreBossBar() {
        object : BukkitRunnable() {
            val scoreBossBar = BossBar.bossBar(Formatting.allTags.deserialize(""), 0f, Color.RED, BossBar.Overlay.PROGRESS)
            var ticks = 0
            override fun run() {
                if(GameManager.getGameState() != GameState.IDLE) {
                    for(player in Bukkit.getOnlinePlayers()) {
                        if(!player.activeBossBars().contains(scoreBossBar)) scoreBossBar.addViewer(player)
                    }
                }
                when(GameManager.getGameState()) {
                    GameState.IDLE -> {
                        for(player in Bukkit.getOnlinePlayers()) {
                            scoreBossBar.removeViewer(player)
                        }
                        this.cancel()
                    }
                    GameState.GAME_END -> {
                        if(Timer.getTimer() > 89) {
                            scoreBossBar.name(TextAlignment.centreBossBarText("WINNERS: <gray><obf>???"))
                        }
                        if(Timer.getTimer() <= 89 && ticks == 0) {
                            scoreBossBar.name(TextAlignment.centreBossBarText(
                                translatable("burb.bossbar.winners", Scores.getWinningTeam()?.uppercaseName() ?: translatable("burb.team.uppercase.none"))
                            ))
                        }
                    }
                    else -> {
                        val text = Component.text()
                            .append(TextAlignment.centreBossBarText("<plantscolour>PLANTS<white>: ${Scores.getDisplayScore(Teams.PLANTS)} <gray>| <zombiescolour>ZOMBIES<white>: ${Scores.getDisplayScore(Teams.ZOMBIES)}"))
                            .append(Spacing.spacing(75))


                        val suburbinatingTeam = CapturePoints.getSuburbinatingTeam()
                        if(suburbinatingTeam != null) {
                            text.append(translatable(
                                "burb.bossbar.suburbination",
                                Style.style()
                                    .color(suburbinatingTeam.textColour)
                                    .decorate(TextDecoration.UNDERLINED)
                                    .build()
                            ))
                        } else {
                            CapturePoint.entries.joinToString("<gray>| ") {
                                val data = CapturePoints.getCapturePointData(it)
                                val color = when (data?.second) {
                                    BurbTeam.PLANTS -> "<plantscolour>"
                                    BurbTeam.ZOMBIES -> "<zombiescolour>"
                                    else -> "<speccolour>"
                                }
                                "$color${data?.first ?: 0}<white>%"
                            }
                                .let(TextAlignment::centreBossBarText)
                                .let(text::append)
                        }
                        scoreBossBar.name(text)
                    }
                }
                ticks++
                if(ticks >= 9) {
                    ticks = 0
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
            Scores.getPlacementMap().entries
                .sortedByDescending { it.value }
                .zip(listOf(firstPlaceLine, secondPlaceLine))
                .forEachIndexed { index, (team, scoreboardTeam) ->
                    scoreboardTeam.prefix(Formatting.allTags.deserialize("${ChatUtility.BURB_FONT_TAG}${index}.<reset> "))
                    scoreboardTeam.suffix(
                        Component.text()
                            .font(Formatting.BURB_FONT)
                            .append(team.key.uppercaseName().let {
                                if (CapturePoints.getSuburbinatingTeam() == team.key) it.decorate(TextDecoration.BOLD) else it
                            })
                            .append(Component.text(": ${team.value.floorDiv(1000)}"))
                            .build()
                    )
                }
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