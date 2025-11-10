package dev.byrt.burb.game

import dev.byrt.burb.text.ChatUtility
import dev.byrt.burb.text.InfoBoardManager
import org.bukkit.command.CommandSender

object Timer {
    private var timer = 0
    private var timerState = TimerState.INACTIVE
    private var displayTime = "00:00"

    fun setTimer(newTime: Int, sender: CommandSender?) {
        if (newTime == timer) return
        this.timer = newTime
        this.displayTime = String.format("%02d:%02d", (this.timer + 1) / 60, (this.timer + 1) % 60)
        InfoBoardManager.updateTimer()
        if (sender != null) {
            ChatUtility.broadcastDev(
                "<dark_gray>Timer Updated: <yellow>${newTime}s<green> remaining<dark_gray> [${sender.name}].",
                true
            )
        }
    }

    fun decrement() {
        setTimer(timer - 1, null)
    }

    fun getTimer(): Int {
        return this.timer
    }

    fun getDisplayTimer(): String {
        return this.displayTime
    }

    fun setTimerState(newState : TimerState, sender: CommandSender?) {
        if (newState == timerState) return
        ChatUtility.broadcastDev(
            "<dark_gray>Timer State: <red>$timerState<reset> <aqua>-> <green>$newState<dark_gray>${if (sender != null) " [${sender.name}]." else "."}",
            true
        )
        this.timerState = newState
    }

    fun getTimerState() : TimerState {
        return this.timerState
    }
}

enum class TimerState {
    ACTIVE,
    INACTIVE,
    PAUSED
}