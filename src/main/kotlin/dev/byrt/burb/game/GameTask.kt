package dev.byrt.burb.game

import dev.byrt.burb.game.events.SpecialEvents
import dev.byrt.burb.text.Formatting
import dev.byrt.burb.text.InfoBoardManager
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.music.Jukebox
import dev.byrt.burb.music.Music
import dev.byrt.burb.music.MusicStress
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.player.progression.BurbPlayerData
import dev.byrt.burb.plugin
import dev.byrt.burb.team.Teams

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.title.Title

import org.bukkit.Bukkit
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable

import java.time.Duration

object GameTask {
    private var gameRunnables = mutableMapOf<Int, BukkitRunnable>()
    private var currentGameTaskId = 0
    fun startGameLoop() {
        val gameRunnable = object : BukkitRunnable() {
            override fun run() {
                InfoBoardManager.updateTimer()
                /** STARTING **/
                if(GameManager.getGameState() == GameState.STARTING && Timer.getTimerState() == TimerState.ACTIVE) {
                    if(Timer.getTimer() == 80) {
                        for(player in Bukkit.getOnlinePlayers()) {
                            player.showTitle(Title.title(
                                Component.text("\uD000"),
                                Component.text(""),
                                Title.Times.times(
                                    Duration.ofSeconds(0),
                                    Duration.ofSeconds(3),
                                    Duration.ofSeconds(1)
                                    )
                                )
                            )
                            player.playSound(Sounds.Music.GAME_INTRO_JINGLE)
                        }
                    }
                    if(Timer.getTimer() == 75) {
                        for(player in Bukkit.getOnlinePlayers()) {
                            player.showTitle(Title.title(
                                Formatting.allTags.deserialize("<burbcolour><font:burb:font>SUBURBINATION"),
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
                    if(Timer.getTimer() == 70) {
                        for(player in Bukkit.getOnlinePlayers()) {
                            Jukebox.startMusicLoop(player, Music.LOADING_MELODY)
                        }
                    }
                    if(Timer.getTimer() <= 15) {
                        for(player in Bukkit.getOnlinePlayers()) {
                            player.removePotionEffect(PotionEffectType.BLINDNESS)
                        }
                    }
                    if(Timer.getTimer() in 4..10) {
                        if(Timer.getTimer() == 10) {
                            for(player in Bukkit.getOnlinePlayers()) {
                                player.playSound(Sounds.Alert.ALARM)
                                Jukebox.stopMusicLoop(player, Music.LOADING_MELODY)
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
                            player.playSound(Sounds.Timer.CLOCK_TICK)
                            player.playSound(Sounds.Timer.TICK)
                        }
                    }
                    if(Timer.getTimer() in 1..3) {
                        for(player in Bukkit.getOnlinePlayers()) {
                            player.playSound(Sounds.Timer.CLOCK_TICK)
                            player.playSound(Sounds.Timer.TICK)
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
                    if(Timer.getTimer() == 120) {
                        for(player in Bukkit.getOnlinePlayers()) {
                            Jukebox.startMusicLoop(player, Music.OVERTIME)
                            player.playSound(Sounds.Alert.ALARM)
                            player.showTitle(Title.title(
                                    Formatting.allTags.deserialize(""),
                                    Formatting.allTags.deserialize("<red>2 minutes remain!"),
                                    Title.Times.times(
                                        Duration.ofMillis(250),
                                        Duration.ofSeconds(3),
                                        Duration.ofMillis(750)
                                    )
                                )
                            )
                        }
                    }
                    if(Timer.getTimer() == 60) {
                        for(player in Bukkit.getOnlinePlayers()) {
                            Jukebox.startMusicLoop(player, Music.LOBBY_UNDERWORLD_CHALLENGE_LOW)
                            player.playSound(Sounds.Alert.ALARM)
                            player.showTitle(Title.title(
                                Formatting.allTags.deserialize(""),
                                Formatting.allTags.deserialize("<red>1 minute remains!"),
                                Title.Times.times(
                                    Duration.ofMillis(250),
                                    Duration.ofSeconds(3),
                                    Duration.ofMillis(750)
                                    )
                                )
                            )
                        }
                    }
                    if(Timer.getTimer() == 50) {
                        for(player in Bukkit.getOnlinePlayers()) {
                            Jukebox.startMusicLoop(player, Music.LOBBY_UNDERWORLD_CHALLENGE_MEDIUM)
                        }
                    }
                    if(Timer.getTimer() == 35) {
                        for(player in Bukkit.getOnlinePlayers()) {
                            Jukebox.startMusicLoop(player, Music.LOBBY_UNDERWORLD_CHALLENGE_HIGH)
                        }
                    }
                    if(Timer.getTimer() == 20) {
                        for(player in Bukkit.getOnlinePlayers()) {
                            Jukebox.startMusicLoop(player, Music.LOBBY_UNDERWORLD_CHALLENGE_INTENSE)
                        }
                    }
                    if(Timer.getTimer() % 60 == 0) {
                        SpecialEvents.rollSpecialEvent()
                    }
                    if(Timer.getTimer() in 11..30 || Timer.getTimer() % 60 == 0) {
                        for(player in Bukkit.getOnlinePlayers()) {
                            player.playSound(Sounds.Timer.CLOCK_TICK)
                        }
                    }
                    if(!SpecialEvents.isEventRunning()) {
                        if(Timer.getTimer() in (GameManager.GameTime.IN_GAME_TIME - 240)..GameManager.GameTime.IN_GAME_TIME) {
                            Jukebox.setMusicStress(MusicStress.LOW)
                        }
                        if(Timer.getTimer() in (GameManager.GameTime.IN_GAME_TIME - 660)..(GameManager.GameTime.IN_GAME_TIME - 240)) {
                            Jukebox.setMusicStress(MusicStress.MEDIUM)
                        }
                        if(Timer.getTimer() in 121..(GameManager.GameTime.IN_GAME_TIME - 660)) {
                            Jukebox.setMusicStress(MusicStress.HIGH)
                        }
                    } else {
                        Jukebox.setMusicStress(MusicStress.NULL)
                    }
                    if(Timer.getTimer() in 0..10) {
                        for(player in Bukkit.getOnlinePlayers()) {
                            player.playSound(Sounds.Timer.CLOCK_TICK_HIGH)
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
                            player.playSound(Sounds.Timer.CLOCK_TICK)
                        }
                    }
                    if(Timer.getTimer() in 0..10) {
                        for(player in Bukkit.getOnlinePlayers()) {
                            player.playSound(Sounds.Timer.CLOCK_TICK_HIGH)
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
                    if(Timer.getTimer() == 90) {
                        for(player in Bukkit.getOnlinePlayers()) {
                            if(Scores.getWinningTeam() in listOf(Teams.NULL, Teams.SPECTATOR)) {
                                if (player.burbPlayer().playerTeam == Teams.PLANTS) player.playSound(Sounds.Score.PLANTS_LOSE)
                                if (player.burbPlayer().playerTeam == Teams.ZOMBIES) player.playSound(Sounds.Score.ZOMBIES_LOSE)
                                if (player.burbPlayer().playerTeam in listOf(Teams.PLANTS, Teams.ZOMBIES)) BurbPlayerData.appendExperience(player, 30)
                                player.sendMessage(Formatting.allTags.deserialize("Nobody won, what a disappointment!"))
                                player.showTitle(
                                    Title.title(
                                        Formatting.allTags.deserialize("<yellow><b>DRAW"),
                                        Formatting.allTags.deserialize("It was a tie, do better next time.")
                                    )
                                )
                            } else {
                                player.sendMessage(Formatting.allTags.deserialize("${Scores.getWinningTeam().teamColourTag}<b>${Scores.getWinningTeam().teamName.uppercase()}<reset> won the game!"))
                                player.showTitle(
                                    Title.title(
                                        Formatting.allTags.deserialize("${Scores.getWinningTeam().teamColourTag}<b>${Scores.getWinningTeam().teamName.uppercase()}"),
                                        Formatting.allTags.deserialize("won the game!")
                                    )
                                )
                                when(Scores.getWinningTeam()) {
                                    Teams.PLANTS -> {
                                        if (player.burbPlayer().playerTeam == Teams.PLANTS) {
                                            player.playSound(Sounds.Score.PLANTS_WIN)
                                            BurbPlayerData.appendExperience(player, 200)
                                        }
                                        if (player.burbPlayer().playerTeam == Teams.ZOMBIES) {
                                            player.playSound(Sounds.Score.ZOMBIES_LOSE)
                                            BurbPlayerData.appendExperience(player, 50)
                                        }
                                    }
                                    Teams.ZOMBIES -> {
                                        if (player.burbPlayer().playerTeam == Teams.PLANTS) {
                                            player.playSound(Sounds.Score.PLANTS_LOSE)
                                            BurbPlayerData.appendExperience(player, 50)
                                        }
                                        if (player.burbPlayer().playerTeam == Teams.ZOMBIES) {
                                            player.playSound(Sounds.Score.ZOMBIES_WIN)
                                            BurbPlayerData.appendExperience(player, 200)
                                        }
                                    }
                                    else -> {}
                                }
                            }
                        }
                    }
                    if(Timer.getTimer() == 85) {
                        for(player in Bukkit.getOnlinePlayers()) {
                            Jukebox.startMusicLoop(player, Music.POST_GAME)
                        }
                    }
                    if(Timer.getTimer() <= 0) {
                        for(player in Bukkit.getOnlinePlayers()) {
                            player.showTitle(Title.title(
                                Formatting.allTags.deserialize("\uD000"),
                                Formatting.allTags.deserialize(""),
                                Title.Times.times(
                                    Duration.ofMillis(250),
                                    Duration.ofSeconds(1),
                                    Duration.ofMillis(250)
                                    )
                                )
                            )
                        }
                        GameManager.nextState()
                        stopGameLoop()
                    }
                }

                /** TIMER DECREMENTS IF ACTIVE **/
                if(Timer.getTimerState() == TimerState.ACTIVE) {
                    Timer.decrement()
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
    }
}