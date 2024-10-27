package dev.byrt.burb.game

import dev.byrt.burb.chat.ChatUtility
import dev.byrt.burb.chat.Formatting
import dev.byrt.burb.chat.InfoBoardManager
import dev.byrt.burb.game.GameManager.GameTime.GAME_END_TIME
import dev.byrt.burb.game.GameManager.GameTime.ROUND_STARTING_TIME
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.library.Translation
import dev.byrt.burb.logger
import dev.byrt.burb.music.Jukebox
import dev.byrt.burb.music.Music
import dev.byrt.burb.player.BurbPlayer
import dev.byrt.burb.plugin
import dev.byrt.burb.team.TeamManager
import dev.byrt.burb.team.Teams

import net.kyori.adventure.audience.Audience

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.title.Title
import org.bukkit.*

import org.bukkit.command.CommandSender
import org.bukkit.entity.Display
import org.bukkit.entity.TextDisplay
import org.bukkit.scheduler.BukkitRunnable

import java.time.Duration
import kotlin.math.cos
import kotlin.math.sin

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
                GameTask.stopGameLoop()
                Game.reload()
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
            player.playSound(Sounds.Timer.CLOCK_TICK_HIGH)
            player.resetTitle()
        }
    }

    private fun starting() {
        InfoBoardManager.updateRound()
        if(RoundManager.getRound() == Round.ONE) {
            for(player in Bukkit.getOnlinePlayers()) {
                player.showTitle(Title.title(Component.text("\uD000"), Component.text(""), Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(2), Duration.ofSeconds(1))))
                player.playSound(player.location, Sounds.Start.START_GAME_SUCCESS, 1f, 1f)
                player.stopSound(Sounds.Music.LOBBY_INTRO)
                Jukebox.stopMusicLoop(player, Music.LOBBY_WAITING)
            }
            CapturePointManager.testCreateCapPoints()
        }
    }

    private fun startOvertime() {
        ChatUtility.messageAudience(Audience.audience(Bukkit.getOnlinePlayers()), "${Translation.Generic.ARROW_PREFIX}${Translation.Overtime.OVERTIME_PREFIX}${Translation.Overtime.OVERTIME_REASON}", false)
    }

    private fun gameEnd() {
        for(player in Bukkit.getOnlinePlayers()) {
            player.playSound(Sounds.Round.GAME_OVER)
            player.playSound(Sounds.Round.ROUND_END)
            Jukebox.startMusicLoop(player, plugin, Music.NULL)
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
            Jukebox.startMusicLoop(player, plugin, Music.NULL)
            player.playSound(Sounds.Round.ROUND_END)
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
    private var displayTime = "00:00"

    fun setTimer(newTime: Int, sender: CommandSender?) {
        if(newTime == timer) return
        this.timer = newTime
        this.displayTime = String.format("%02d:%02d", (this.timer + 1) / 60, (this.timer + 1 ) % 60)
        InfoBoardManager.updateTimer()
        if(sender != null) {
            ChatUtility.broadcastDev("<dark_gray>Timer Updated: <yellow>${newTime}s<green> remaining<dark_gray> [${sender.name}].", true)
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
        if(newState == timerState) return
        ChatUtility.broadcastDev("<dark_gray>Timer State: <red>$timerState<reset> <aqua>-> <green>$newState<dark_gray>${if(sender != null) " [${sender.name}]." else "."}", false)
        this.timerState = newState
    }

    fun getTimerState() : TimerState {
        return this.timerState
    }
}

object CapturePointManager {
    private val capturePoints = mutableMapOf<CapturePoint, Location>()
    private val capturePointsLoopMap = mutableMapOf<CapturePoint, BukkitRunnable>()
    private val capturedPoints = mutableMapOf<CapturePoint, Teams>()
    private var suburbinatingTeam = Teams.NULL
    const val REQUIRED_CAPTURE_SCORE = 500
    fun testCreateCapPoints() {
        addCapturePoint(CapturePoint.A, Location(Bukkit.getWorlds()[0], -25.5, 4.0, 0.5))
        addCapturePoint(CapturePoint.B, Location(Bukkit.getWorlds()[0], 11.5, 0.0, -44.5))
        addCapturePoint(CapturePoint.C, Location(Bukkit.getWorlds()[0], 7.5, 2.0, 49.5))
    }

    fun testDestroyCapPoints() {
        removeCapturePoint(CapturePoint.A)
        removeCapturePoint(CapturePoint.B)
        removeCapturePoint(CapturePoint.C)

        for(world in Bukkit.getWorlds()) {
            for(td in world.getEntitiesByClasses(TextDisplay::class.java)) {
                if(td.scoreboardTags.contains("burb.game.capture_point.text_display")) {
                    td.remove()
                }
            }
        }
    }

    fun suburbinationCheck() {
        val frequency = capturedPoints.values.groupingBy { it }.eachCount()
        if(frequency[Teams.PLANTS] == 3) {
            if(this.suburbinatingTeam == Teams.PLANTS) return
            this.suburbinatingTeam = Teams.PLANTS
            for(player in Bukkit.getOnlinePlayers()) {
                player.sendMessage(Formatting.allTags.deserialize("<plantscolour><bold>SUBURBINATION<reset>: The plants are now suburbinating."))
                if(Timer.getTimer() > 50) {
                    Jukebox.startMusicLoop(player, plugin, Music.SUBURBINATION_PLANTS)
                }
            }
        }
        else if(frequency[Teams.ZOMBIES] == 3) {
            if(this.suburbinatingTeam == Teams.ZOMBIES) return
            this.suburbinatingTeam = Teams.ZOMBIES
            for(player in Bukkit.getOnlinePlayers()) {
                player.sendMessage(Formatting.allTags.deserialize("<zombiescolour><bold>SUBURBINATION<reset>: The zombies are now suburbinating."))
                if(Timer.getTimer() > 50) {
                    Jukebox.startMusicLoop(player, plugin, Music.SUBURBINATION_ZOMBIES)
                }
            }
        } else {
            if(this.suburbinatingTeam == Teams.NULL || this.suburbinatingTeam == Teams.SPECTATOR) return
            this.suburbinatingTeam = Teams.NULL
            for(player in Bukkit.getOnlinePlayers()) {
                player.sendMessage(Formatting.allTags.deserialize("<speccolour><bold>SUBURBINATION<reset>: Suburbination is no longer active."))
                if(Timer.getTimer() > 50) {
                    Jukebox.startMusicLoop(player, plugin, Music.NULL)
                }
            }
        }
    }

    fun getCapturedPoints(): Map<CapturePoint, Teams> {
        return this.capturedPoints
    }

    fun capturePointRunnable(capturePoint: CapturePoint) {
        val capturePointRunnable = object : BukkitRunnable() {
            var capturePointTicks = 0
            var capturePointSeconds = 0
            var plantCapturePointProgress = 0
            var zombieCapturePointProgress = 0
            var dominatingTeam = Teams.NULL
            var isContested = false
            var playersInPoint = mutableSetOf<BurbPlayer>()
            var numPlantsInPoint = 0
            var numZombiesInPoint = 0
            var textDisplay = capturePoint.location.world.spawn(Location(capturePoint.location.world, capturePoint.location.x, capturePoint.location.y + 7, capturePoint.location.z), TextDisplay::class.java)
            override fun run() {
                if(!textDisplay.scoreboardTags.contains("burb.game.capture_point.text_display")) textDisplay.addScoreboardTag("burb.game.capture_point.text_display")
                textDisplay.alignment = TextDisplay.TextAlignment.CENTER
                textDisplay.billboard = Display.Billboard.VERTICAL
                textDisplay.text(Formatting.allTags.deserialize("<yellow><bold>CAPTURE POINT <gold>[<yellow>${capturePoint}<gold>]<newline><newline><reset>Dominating Team: ${if(dominatingTeam == Teams.NULL) "<speccolour>None" else "${dominatingTeam.teamColourTag}${dominatingTeam.teamName}"}<newline><reset>Capture Status: <burbcolour>${if(dominatingTeam == Teams.PLANTS && plantCapturePointProgress > zombieCapturePointProgress && plantCapturePointProgress != 501) "Plants taking over..." else if(dominatingTeam == Teams.ZOMBIES && zombieCapturePointProgress > plantCapturePointProgress && zombieCapturePointProgress != 501) "Zombies taking over..." else if(dominatingTeam == Teams.PLANTS && plantCapturePointProgress < zombieCapturePointProgress && plantCapturePointProgress != 501) "Plants taking down zombies..." else if(dominatingTeam == Teams.ZOMBIES && zombieCapturePointProgress < plantCapturePointProgress && zombieCapturePointProgress != 501) "Zombies taking down plants..." else if(dominatingTeam == Teams.PLANTS && plantCapturePointProgress == 501) "Plants own point" else if(dominatingTeam == Teams.ZOMBIES && zombieCapturePointProgress == 501) "Zombies own point" else "<reset><red>Uncontested"}<reset><newline>"))
                // Work out who is in range of the point
                val participants = TeamManager.getParticipants()
                for(participant in participants) {
                    if(participant.getBukkitPlayer().location.distanceSquared(capturePoint.location) <= 25.0 && !playersInPoint.contains(participant)) {
                        playersInPoint.add(participant)
                        participant.getBukkitPlayer().sendActionBar(Formatting.allTags.deserialize("In capture point $capturePoint | plant progress $plantCapturePointProgress | zombie progress $zombieCapturePointProgress | dominating team $dominatingTeam"))
                    } else {
                        playersInPoint.remove(participant)
                    }
                }
                // Calculate how many of each team are in the point
                numPlantsInPoint = 0
                numZombiesInPoint = 0
                for(playerInPoint in playersInPoint) {
                    if(playerInPoint.playerTeam == Teams.PLANTS) numPlantsInPoint++
                    if(playerInPoint.playerTeam == Teams.ZOMBIES) numZombiesInPoint++
                }
                // Set the dominating team
                if(numPlantsInPoint > numZombiesInPoint) dominatingTeam = Teams.PLANTS
                if(numZombiesInPoint > numPlantsInPoint) dominatingTeam = Teams.ZOMBIES
                // Set if point is contested
                isContested = numPlantsInPoint == numZombiesInPoint
                // If the dominating team is a participant team and the point is not contested, continue to add score
                if(dominatingTeam != Teams.NULL || dominatingTeam != Teams.SPECTATOR) {
                    if(!isContested) {
                        if(dominatingTeam == Teams.PLANTS) {
                            if(zombieCapturePointProgress > 0) {
                                zombieCapturePointProgress--
                            } else {
                                if(zombieCapturePointProgress == 0 && suburbinatingTeam == Teams.ZOMBIES) {
                                    capturedPoints.remove(capturePoint)
                                    suburbinationCheck()
                                }
                                if(plantCapturePointProgress < REQUIRED_CAPTURE_SCORE) {
                                    plantCapturePointProgress++
                                }
                                if(plantCapturePointProgress == REQUIRED_CAPTURE_SCORE) {
                                    Bukkit.getOnlinePlayers().forEach { player -> player.sendMessage(Formatting.allTags.deserialize("CAPTURE POINT $capturePoint has been captured by $dominatingTeam")) }
                                    capturedPoints.remove(capturePoint)
                                    capturedPoints[capturePoint] = dominatingTeam
                                    suburbinationCheck()
                                    plantCapturePointProgress = REQUIRED_CAPTURE_SCORE + 1
                                }
                            }
                        }
                        if(dominatingTeam == Teams.ZOMBIES) {
                            if(plantCapturePointProgress > 0) {
                                plantCapturePointProgress--
                            } else {
                                if(plantCapturePointProgress == 0 && suburbinatingTeam == Teams.PLANTS) {
                                    capturedPoints.remove(capturePoint)
                                    suburbinationCheck()
                                }
                                if(zombieCapturePointProgress < REQUIRED_CAPTURE_SCORE) {
                                    zombieCapturePointProgress++
                                }
                                if(zombieCapturePointProgress == REQUIRED_CAPTURE_SCORE) {
                                    Bukkit.getOnlinePlayers().forEach { player -> player.sendMessage(Formatting.allTags.deserialize("CAPTURE POINT $capturePoint has been captured by $dominatingTeam")) }
                                    capturedPoints.remove(capturePoint)
                                    capturedPoints[capturePoint] = dominatingTeam
                                    suburbinationCheck()
                                    zombieCapturePointProgress = REQUIRED_CAPTURE_SCORE + 1
                                }
                            }
                        }
                    }
                }
                if(capturePointTicks % 10 == 0) {
                    capturePointParticleCircle(capturePoint, capturePoint.location, dominatingTeam)
                }
                if(capturePointSeconds >= 20) {
                    capturePointTicks = 0
                    capturePointSeconds++
                }
            }
        }
        capturePointRunnable.runTaskTimer(plugin, 0L, 1L)
        capturePointsLoopMap[capturePoint] = capturePointRunnable
    }

    private fun capturePointParticleCircle(capturePoint: CapturePoint, location: Location, dominatingTeam: Teams) {
        object : BukkitRunnable() {
            override fun run() {
                for(i in 0 .. 31) {
                    // TODO: TEMPORARY RADII
                    val x = cos(i.toDouble()) * 5.0
                    val z = sin(i.toDouble()) * 5.0
                    location.world.spawnParticle(
                        Particle.DUST,
                        location.x + x + 0.5,
                        location.y + 0.25,
                        location.z + z + 0.5,
                        1,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        Particle.DustOptions(if(dominatingTeam == Teams.PLANTS) Color.LIME else if(dominatingTeam == Teams.ZOMBIES) Color.PURPLE else Color.WHITE, 0.75f),
                        true
                    )
                    if(i == 31) {
                        cancel()
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L)
    }

    fun addCapturePoint(capturePoint: CapturePoint, location: Location) {
        if(capturePoints.containsKey(capturePoint)) return
        capturePoints[capturePoint] = location
        capturePoint.location = location
        capturePointRunnable(capturePoint)
    }

    fun removeCapturePoint(capturePoint: CapturePoint) {
        if(!capturePoints.containsKey(capturePoint)) return
        if(capturePointsLoopMap.containsKey(capturePoint)) {
            capturePointsLoopMap[capturePoint]!!.cancel()
            capturePointsLoopMap.remove(capturePoint)
        }
        capturePoints.remove(capturePoint, capturePoint.location)
        capturePoint.location = Location(Bukkit.getWorlds()[0], 0.5, 30.0 ,0.5)
    }
}

object ScoreManager {
    private var plantsScore = 0
    private var zombiesScore = 0

    fun getPlacementMap(): Map<Teams, Int> {
        return mutableMapOf(Pair(Teams.PLANTS, plantsScore), Pair(Teams.ZOMBIES, zombiesScore)).toList().sortedBy { (_, scores) -> scores }.reversed().toMap()
    }

    fun addPlantsScore(score: Int) {
        this.plantsScore += score
    }

    fun addZombiesScore(score: Int) {
        this.zombiesScore += score
    }

    fun subPlantsScore(score: Int) {
        this.plantsScore -= score
    }

    fun subZombiesScore(score: Int) {
        this.zombiesScore -= score
    }

    fun setPlantsScore(score: Int) {
        this.plantsScore = score
    }

    fun setZombiesScore(score: Int) {
        this.zombiesScore = score
    }

    fun getPlantsScore(): Int {
        return this.plantsScore
    }

    fun getZombiesScore(): Int {
        return this.zombiesScore
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

enum class CapturePoint(var location: Location) {
    A(Location(Bukkit.getWorlds()[0], 0.5, 30.0 ,0.5)),
    B(Location(Bukkit.getWorlds()[0], 0.5, 30.0 ,0.5)),
    C(Location(Bukkit.getWorlds()[0], 0.5, 30.0 ,0.5));
}