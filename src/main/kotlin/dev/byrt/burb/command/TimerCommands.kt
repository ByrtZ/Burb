package dev.byrt.burb.command

import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.game.Timer
import dev.byrt.burb.game.TimerState

import org.bukkit.command.CommandSender

import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.annotations.processing.CommandContainer

@Suppress("unused", "unstableApiUsage")
@CommandContainer
class TimerCommands {
    @Command("timer set <seconds>")
    @CommandDescription("Sets the current timer.")
    @Permission("burb.cmd.timer")
    fun timerSet(sender: CommandSender, seconds: Int) {
        if(Timer.getTimerState() != TimerState.INACTIVE) {
            if(GameManager.getGameState() != GameState.IDLE) {
                if(seconds > 0) {
                    Timer.setTimer(seconds, sender)
                } else {
                    return
                }
            } else {
                return
            }
        } else {
            return
        }
    }

    @Command("timer skip [seconds]")
    @CommandDescription("Skips the current timer by x seconds or fully.")
    @Permission("burb.cmd.timer")
    fun timerSkip(sender: CommandSender, seconds: Int?) {
        if(Timer.getTimerState() != TimerState.INACTIVE) {
            if(seconds != null) {
                if(seconds > 0) {
                    Timer.setTimer(Timer.getTimer() - seconds, sender)
                    Timer.setTimerState(TimerState.ACTIVE, sender)
                } else {
                    return
                }
            } else {
                Timer.setTimer(1, sender)
                Timer.setTimerState(TimerState.ACTIVE, sender)
            }
        } else {
            return
        }
    }

    @Command("timer pause")
    @CommandDescription("Pauses the current timer.")
    @Permission("burb.cmd.timer")
    fun timerPause(sender: CommandSender) {
        if(Timer.getTimerState() == TimerState.ACTIVE) {
            Timer.setTimerState(TimerState.PAUSED, sender)
        } else {
            return
        }
    }

    @Command("timer resume")
    @CommandDescription("Resumes the current timer.")
    @Permission("burb.cmd.timer")
    fun timerResume(sender: CommandSender) {
        if(Timer.getTimerState() == TimerState.PAUSED) {
            Timer.setTimerState(TimerState.ACTIVE, sender)
        } else {
            return
        }
    }
}