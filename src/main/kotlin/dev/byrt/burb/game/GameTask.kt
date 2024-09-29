package dev.byrt.burb.game

import dev.byrt.burb.chat.Formatting
import dev.byrt.burb.chat.InfoBoardManager
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.plugin

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.title.Title

import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

import java.time.Duration

object GameTask {
    private var gameRunnables = mutableMapOf<Int, BukkitRunnable>()
    private var currentGameTaskId = 0
    private var displayTime = "00:00"

    fun startGameLoop() {

        val gameRunnable = object : BukkitRunnable() {
            override fun run() {
                displayTime = String.format("%02d:%02d", Timer.getTimer() / 60, Timer.getTimer() % 60)
                InfoBoardManager.updateTimer()

                /** STARTING **/
                if(GameManager.getGameState() == GameState.STARTING && Timer.getTimerState() == TimerState.ACTIVE) {
                    if(RoundManager.getRound() == Round.ONE) {
                        if(Timer.getTimer() == 80) {
                            for(player in Bukkit.getOnlinePlayers()) {
                                player.showTitle(Title.title(
                                    Component.text("\uD000"),
                                    Component.text(""),
                                    Title.Times.times(
                                        Duration.ofSeconds(0),
                                        Duration.ofSeconds(10),
                                        Duration.ofSeconds(1)
                                        )
                                    )
                                )
                                player.playSound(Sounds.Music.GAME_INTRO_JINGLE)
                            }
                        }
                        if(Timer.getTimer() == 76) {
                            for(player in Bukkit.getOnlinePlayers()) {
                                player.showTitle(Title.title(
                                    Formatting.allTags.deserialize("<burbcolour>Suburbination"),
                                    Component.text(""),
                                    Title.Times.times(
                                        Duration.ofSeconds(1),
                                        Duration.ofSeconds(4),
                                        Duration.ofSeconds(1)
                                        )
                                    )
                                )
                            }
                        }
                        if(Timer.getTimer() == 25) {
                            for(player in Bukkit.getOnlinePlayers()) {
                                player.playSound(player.location, Sounds.Tutorial.TUTORIAL_POP, 1f, 1f)
                                player.sendMessage(Component.text("-----------------------------------------------------").color(NamedTextColor.GREEN).decoration(TextDecoration.STRIKETHROUGH, true)
                                    .append(Component.text(" Starting soon:\n\n").color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, true).decoration(TextDecoration.STRIKETHROUGH, false)
                                        .append(Component.text("      I don't have anything funny to say, this just needs replacing.\n\n").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, false).decoration(TextDecoration.ITALIC, true)
                                            .append(Component.text("\n\n\n")
                                                .append(Component.text("-----------------------------------------------------").color(NamedTextColor.GREEN).decoration(TextDecoration.STRIKETHROUGH, true).decoration(TextDecoration.ITALIC, false)
                                                )
                                            )
                                        )
                                    )
                                )
                            }
                        }
                    }
                    if(Timer.getTimer() in 4..10) {
                        if(Timer.getTimer() == 10) {
                            for(player in Bukkit.getOnlinePlayers()) {
                                player.playSound(player.location, Sounds.Music.GAME_STARTING_MUSIC, 1f, 1f)
                            }
                        }
                        for(player in Bukkit.getOnlinePlayers()) {
                            player.showTitle(Title.title(
                                Component.text("Starting in").color(NamedTextColor.AQUA),
                                Component.text("►${Timer.getTimer()}◄").decoration(TextDecoration.BOLD, true),
                                Title.Times.times(
                                    Duration.ofSeconds(0),
                                    Duration.ofSeconds(5),
                                    Duration.ofSeconds(0)
                                    )
                                )
                            )
                            player.playSound(player.location, Sounds.Timer.CLOCK_TICK, 1f, 1f)
                        }
                    }
                    if(Timer.getTimer() in 1..3) {
                        for(player in Bukkit.getOnlinePlayers()) {
                            player.playSound(player.location, Sounds.Timer.CLOCK_TICK, 1f, 1f)
                            player.playSound(player.location, Sounds.Timer.STARTING_123, 1f, 1f)
                            if(Timer.getTimer() == 3) {
                                player.showTitle(Title.title(
                                    Component.text("Starting in").color(NamedTextColor.AQUA),
                                    Component.text("►${Timer.getTimer()}◄").color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true),
                                    Title.Times.times(
                                        Duration.ofSeconds(0),
                                        Duration.ofSeconds(5),
                                        Duration.ofSeconds(0)
                                        )
                                    )
                                )
                            }
                            if(Timer.getTimer() == 2) {
                                player.showTitle(Title.title(
                                    Component.text("Starting in").color(NamedTextColor.AQUA),
                                    Component.text("►${Timer.getTimer()}◄").color(NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, true),
                                    Title.Times.times(
                                        Duration.ofSeconds(0),
                                        Duration.ofSeconds(5),
                                        Duration.ofSeconds(0)
                                        )
                                    )
                                )
                            }
                            if(Timer.getTimer() == 1) {
                                player.showTitle(Title.title(
                                    Component.text("Starting in").color(NamedTextColor.AQUA),
                                    Component.text("►${Timer.getTimer()}◄").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, true),
                                    Title.Times.times(
                                        Duration.ofSeconds(0),
                                        Duration.ofSeconds(5),
                                        Duration.ofSeconds(0)
                                        )
                                    )
                                )
                            }
                        }
                    }
                    if(Timer.getTimer() <= 0) {
                        GameManager.nextState()
                    }
                }

                /** IN GAME **/
                if(GameManager.getGameState() == GameState.IN_GAME && Timer.getTimerState() == TimerState.ACTIVE) {
                    if(Timer.getTimer() in 11..30 || Timer.getTimer() % 60 == 0) {
                        for(player in Bukkit.getOnlinePlayers()) {
                            player.playSound(player.location, Sounds.Timer.CLOCK_TICK, 1f, 1f)
                        }
                    }
                    if(Timer.getTimer() in 0..10) {
                        for(player in Bukkit.getOnlinePlayers()) {
                            player.playSound(player.location, Sounds.Timer.CLOCK_TICK_HIGH, 1f, 1f)
                        }
                    }
                    if(Timer.getTimer() <= 0) {
                        GameManager.nextState()
                    }
                }

                /** OVERTIME **/
                if(GameManager.getGameState() == GameState.OVERTIME && Timer.getTimerState() == TimerState.ACTIVE) {
                    if(Timer.getTimer() in 11..30 || Timer.getTimer() % 60 == 0) {
                        for(player in Bukkit.getOnlinePlayers()) {
                            player.playSound(player.location, Sounds.Timer.CLOCK_TICK, 1f, 1f)
                        }
                    }
                    if(Timer.getTimer() in 0..10) {
                        for(player in Bukkit.getOnlinePlayers()) {
                            player.playSound(player.location, Sounds.Timer.CLOCK_TICK_HIGH, 1f, 1f)
                        }
                    }
                    if(Timer.getTimer() <= 0) {
                        GameManager.nextState()
                    }
                }

                /** ROUND END **/
                if(Timer.getTimer() <= 0 && GameManager.getGameState() == GameState.ROUND_END && Timer.getTimerState() == TimerState.ACTIVE) {
                    GameManager.nextState()
                }

                /** GAME END **/
                if(GameManager.getGameState() == GameState.GAME_END && Timer.getTimerState() == TimerState.ACTIVE) {
                    if(Timer.getTimer() <= 0) {
                        GameManager.nextState()
                        stopGameLoop()
                    }
                }

                /** TIMER DECREMENTS IF ACTIVE **/
                if(Timer.getTimerState() == TimerState.ACTIVE) {
                    Timer.setTimer(Timer.getTimer() - 1, null)
                }
            }
        }
        gameRunnable.runTaskTimer(plugin, 0L, 20L)
        currentGameTaskId = gameRunnable.taskId
        gameRunnables[gameRunnable.taskId] = gameRunnable
    }

    fun stopGameLoop() {
        gameRunnables.remove(currentGameTaskId)?.cancel()
        Timer.setTimer(0, null)
        displayTime = "00:00"
    }

    fun getDisplayTime(): String {
        return this.displayTime
    }
}