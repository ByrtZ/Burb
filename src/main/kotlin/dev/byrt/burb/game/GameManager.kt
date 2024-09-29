package dev.byrt.burb.game

import dev.byrt.burb.chat.ChatUtility
import dev.byrt.burb.chat.InfoBoardManager
import dev.byrt.burb.game.GameManager.GameTime.GAME_END_TIME
import dev.byrt.burb.game.GameManager.GameTime.ROUND_STARTING_TIME
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.library.Translation
import dev.byrt.burb.logger
import dev.byrt.burb.music.Jukebox
import dev.byrt.burb.music.Music

import net.kyori.adventure.audience.Audience

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.title.Title

import org.bukkit.Bukkit
import org.bukkit.SoundCategory
import org.bukkit.command.CommandSender

import java.time.Duration

object GameManager {
    private var gameState = GameState.IDLE
    private var overtimeActive = false

    fun nextState() {
        when(this.gameState) {
            GameState.IDLE -> { setGameState(GameState.STARTING) }
            GameState.STARTING -> { setGameState(GameState.IN_GAME) }
            GameState.IN_GAME -> {
                if(overtimeActive) {
                    setGameState(GameState.OVERTIME)
                } else if(RoundManager.getRound().ordinal + 1 >= RoundManager.getTotalRounds()) {
                    setGameState(GameState.GAME_END)
                } else {
                    setGameState(GameState.ROUND_END)
                }
            }
            GameState.ROUND_END -> { setGameState(GameState.STARTING) }
            GameState.GAME_END -> { setGameState(GameState.IDLE) }
            GameState.OVERTIME -> {
                if(RoundManager.getRound().ordinal + 1 >= RoundManager.getTotalRounds()) {
                    setGameState(GameState.GAME_END)
                } else {
                    setGameState(GameState.ROUND_END)
                }
            }
        }
    }

    fun setGameState(newState: GameState) {
        if(newState == gameState) return
        ChatUtility.broadcastDev("<dark_gray>Game State: <red>$gameState<reset> <aqua>-> <green>$newState<dark_gray>.", true)
        this.gameState = newState
        InfoBoardManager.updateStatus()
        when(this.gameState) {
            GameState.IDLE -> {
                Game.reload()
                GameTask.stopGameLoop()
            }
            GameState.STARTING -> {
                if(RoundManager.getRound() == Round.ONE) {
                    Timer.setTimerState(TimerState.ACTIVE, null)
                    Timer.setTimer(GameTime.GAME_STARTING_TIME, null)
                    GameTask.startGameLoop()
                    starting()
                } else {
                    Timer.setTimerState(TimerState.ACTIVE, null)
                    Timer.setTimer(ROUND_STARTING_TIME, null)
                    starting()
                }
            }
            GameState.IN_GAME -> {
                Timer.setTimerState(TimerState.ACTIVE, null)
                Timer.setTimer(GameTime.IN_GAME_TIME, null)
                startRound()
            }
            GameState.ROUND_END -> {
                Timer.setTimerState(TimerState.ACTIVE, null)
                Timer.setTimer(GameTime.ROUND_END_TIME, null)
                roundEnd()
            }
            GameState.GAME_END -> {
                Timer.setTimerState(TimerState.ACTIVE, null)
                Timer.setTimer(GAME_END_TIME, null)
                gameEnd()
            }
            GameState.OVERTIME -> {
                Timer.setTimerState(TimerState.ACTIVE, null)
                Timer.setTimer(GameTime.OVERTIME_TIME, null)
                startOvertime()
            }
        }
    }

    private fun startRound() {
        for(player in Bukkit.getOnlinePlayers()) {
            player.playSound(player.location, Sounds.Timer.STARTING_GO, 1f, 1f)
            player.playSound(player.location, Sounds.Timer.CLOCK_TICK_HIGH, 1f, 1f)
            player.playSound(player.location, Sounds.Round.ENTRANCE, 1f, 1f)
            player.resetTitle()
        }
    }

    private fun starting() {
        InfoBoardManager.updateRound()
        if(RoundManager.getRound() == Round.ONE) {
            for(player in Bukkit.getOnlinePlayers()) {
                player.showTitle(Title.title(Component.text("\uD000"), Component.text(""), Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(2), Duration.ofSeconds(1))))
                player.playSound(player.location, Sounds.Start.START_GAME_SUCCESS, 1f, 1f)
            }
        }
    }

    private fun startOvertime() {
        ChatUtility.messageAudience(Audience.audience(Bukkit.getOnlinePlayers()), "${Translation.Generic.ARROW_PREFIX}${Translation.Overtime.OVERTIME_PREFIX}${Translation.Overtime.OVERTIME_REASON}", false)
    }

    private fun gameEnd() {
        for(player in Bukkit.getOnlinePlayers()) {
            player.playSound(player.location, Sounds.GameOver.GAME_OVER, 1f, 1f)
            Jukebox.stopMusicLoop(player, Music.MAIN)
            Jukebox.stopMusicLoop(player, Music.OVERTIME)
            player.playSound(player.location, Sounds.Music.GAME_OVER_MUSIC, SoundCategory.VOICE, 0.85f, 1f)
            player.showTitle(
                    Title.title(
                    Component.text("Game Over!", NamedTextColor.RED, TextDecoration.BOLD),
                    Component.text(""),
                    Title.Times.times(
                        Duration.ofSeconds(0),
                        Duration.ofSeconds(4),
                        Duration.ofSeconds(1)
                    )
                )
            )
        }
    }

    private fun roundEnd() {
        for(player in Bukkit.getOnlinePlayers()) {
            player.playSound(player.location, Sounds.Round.ROUND_END, 1f, 1f)
            Jukebox.stopMusicLoop(player, Music.MAIN)
            Jukebox.stopMusicLoop(player, Music.OVERTIME)
            player.playSound(player.location, Sounds.Music.ROUND_OVER_MUSIC, SoundCategory.VOICE, 0.85f, 1f)
            player.showTitle(
                Title.title(
                    Component.text("Round Over!", NamedTextColor.RED, TextDecoration.BOLD),
                    Component.text(""),
                    Title.Times.times(
                        Duration.ofSeconds(0),
                        Duration.ofSeconds(4),
                        Duration.ofSeconds(1)
                    )
                )
            )
        }
        RoundManager.nextRound()
    }

    fun getGameState(): GameState {
        return gameState
    }

    fun setOvertimeState(isActive: Boolean) {
        overtimeActive = isActive
    }

    fun isOvertimeActive(): Boolean {
        return overtimeActive
    }

    fun forceState(forcedState: GameState) {
        setGameState(forcedState)
    }

    object GameTime {
        const val GAME_STARTING_TIME = 80
        const val ROUND_STARTING_TIME = 30
        const val IN_GAME_TIME = 300
        const val ROUND_END_TIME = 15
        const val GAME_END_TIME = 60
        const val OVERTIME_TIME = 30
    }
}

object RoundManager {
    private var round = Round.ONE
    private var totalRounds = 1

    fun nextRound() {
        when(round) {
            Round.ONE -> { setRound(Round.TWO) }
            Round.TWO -> { setRound(Round.THREE) }
            Round.THREE -> { logger.warning("Attempted to increment past round 3.") }
        }
    }

    fun setRound(newRound : Round) {
        if(newRound == round) return
        ChatUtility.broadcastDev("<dark_gray>Round Updated: <red>$round<reset> <aqua>-> <green>$newRound<dark_gray>.", true)
        this.round = newRound
    }

    fun getRound() : Round {
        return round
    }

    fun getTotalRounds() : Int {
        return totalRounds
    }
}

object Timer {
    private var timer = 0
    private var timerState = TimerState.INACTIVE

    fun setTimer(newTime: Int, sender: CommandSender?) {
        this.timer = newTime
        if(sender != null) {
            ChatUtility.broadcastDev("<dark_gray>Timer Updated: <yellow>${newTime}s<green> remaining<dark_gray> [${sender.name}].", true)
        }
    }

    fun getTimer(): Int {
        return this.timer
    }

    fun getDisplayTimer(): String {
        return GameTask.getDisplayTime()
    }

    fun setTimerState(newState : TimerState, sender: CommandSender?) {
        if(newState == timerState) return
        ChatUtility.broadcastDev("<dark_gray>Timer State: <red>$timerState<reset> <aqua>-> <green>$newState<dark_gray>${if(sender != null) " [${sender.name}]." else "."}", false)
        this.timerState = newState
    }

    fun getTimerState() : TimerState {
        return this.timerState
    }
}

enum class GameState {
    IDLE,
    STARTING,
    IN_GAME,
    ROUND_END,
    GAME_END,
    OVERTIME
}

enum class Round {
    ONE,
    TWO,
    THREE
}

enum class TimerState {
    ACTIVE,
    INACTIVE,
    PAUSED
}