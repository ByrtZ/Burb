package dev.byrt.burb.game

import dev.byrt.burb.plugin

import org.bukkit.scheduler.BukkitRunnable

object GameTask {
    private var gameRunnables = mutableMapOf<Int, BukkitRunnable>()
    private var currentGameTaskId = 0
    private var displayTime = "00:00"

    fun startGameLoop() {

        val gameRunnable = object : BukkitRunnable() {
            override fun run() {

                if(Timer.getTimerState() == TimerState.ACTIVE) {
                    Timer.setTimer(Timer.getTimer() - 1)
                }
            }
        }
        gameRunnable.runTaskTimer(plugin, 0L, 20L)
        currentGameTaskId = gameRunnable.taskId
        gameRunnables[gameRunnable.taskId] = gameRunnable
    }

    fun stopGameLoop() {
        gameRunnables.remove(currentGameTaskId)?.cancel()
    }
}