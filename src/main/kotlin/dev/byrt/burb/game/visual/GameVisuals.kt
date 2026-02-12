package dev.byrt.burb.game.visual

import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.game.Timer
import dev.byrt.burb.game.events.SpecialEvents
import dev.byrt.burb.plugin
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

object GameVisuals {
    fun setDayTime(gameDayTime: GameDayTime) {
        if(gameDayTime.dayTime == Bukkit.getWorlds()[0].time) return
        if(gameDayTime == GameDayTime.DAY && ((Timer.getTimer() <= 120 && GameManager.getGameState() == GameState.IN_GAME) || SpecialEvents.isEventRunning())) return
        object : BukkitRunnable() {
            override fun run() {
                when(gameDayTime) {
                    GameDayTime.DAY -> {
                        if(gameDayTime.dayTime < Bukkit.getWorlds()[0].time) {
                            Bukkit.getWorlds()[0].time -= 25
                        } else {
                            Bukkit.getWorlds()[0].time = gameDayTime.dayTime
                            cancel()
                        }
                    }
                    GameDayTime.NIGHT -> {
                        if(gameDayTime.dayTime > Bukkit.getWorlds()[0].time) {
                            Bukkit.getWorlds()[0].time += 25
                        } else {
                            Bukkit.getWorlds()[0].time = gameDayTime.dayTime
                            cancel()
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L)
    }

    fun resetDayTime() {
        Bukkit.getWorlds()[0].time = GameDayTime.DAY.dayTime
    }
}