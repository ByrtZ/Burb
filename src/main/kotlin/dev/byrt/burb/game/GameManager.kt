package dev.byrt.burb.game

import dev.byrt.burb.text.ChatUtility
import dev.byrt.burb.text.Formatting
import dev.byrt.burb.text.InfoBoardManager
import dev.byrt.burb.game.GameManager.GameTime.GAME_END_TIME
import dev.byrt.burb.game.GameManager.GameTime.ROUND_STARTING_TIME
import dev.byrt.burb.game.location.SpawnPoints
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.library.Translation
import dev.byrt.burb.logger
import dev.byrt.burb.lobby.LobbyBall
import dev.byrt.burb.music.Jukebox
import dev.byrt.burb.music.Music
import dev.byrt.burb.music.MusicStress
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.player.progression.BurbProgression
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
                if (overtimeActive) {
                    setGameState(GameState.OVERTIME)
                } else if (RoundManager.getRound().ordinal + 1 >= RoundManager.getTotalRounds()) {
                    setGameState(GameState.GAME_END)
                } else {
                    setGameState(GameState.ROUND_END)
                }
            }
            GameState.ROUND_END -> { setGameState(GameState.STARTING) }
            GameState.GAME_END -> { setGameState(GameState.IDLE) }
            GameState.OVERTIME -> {
                if (RoundManager.getRound().ordinal + 1 >= RoundManager.getTotalRounds()) {
                    setGameState(GameState.GAME_END)
                } else {
                    setGameState(GameState.ROUND_END)
                }
            }
        }
    }

    fun setGameState(newState: GameState) {
        if (newState == gameState) return
        ChatUtility.broadcastDev("<dark_gray>Game State: <red>$gameState<reset> <aqua>-> <green>$newState<dark_gray>.", true)
        this.gameState = newState
        InfoBoardManager.updateStatus()
        when(this.gameState) {
            GameState.IDLE -> {
                GameTask.stopGameLoop()
                Game.reload()
            }
            GameState.STARTING -> {
                if (RoundManager.getRound() == Round.ONE) {
                    Timer.setTimerState(TimerState.ACTIVE, null)
                    Timer.setTimer(GameTime.GAME_STARTING_TIME, null)
                    GameTask.startGameLoop()
                    InfoBoardManager.timerBossBar()
                    InfoBoardManager.capturePointBossBar()
                    LobbyBall.cleanup()
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
            player.playSound(Sounds.Timer.STARTING_GO)
            player.playSound(Sounds.Timer.CLOCK_TICK_HIGH)
            player.resetTitle()
        }
    }

    private fun starting() {
        InfoBoardManager.updateRound()
        TeamManager.hideTeamNametags()
        CapturePointManager.initializeCapturePoints()
        if(RoundManager.getRound() == Round.ONE) {
            for(player in Bukkit.getOnlinePlayers()) {
                player.showTitle(Title.title(Component.text("\uD000"), Component.text(""), Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(2), Duration.ofSeconds(1))))
                player.stopSound(Sounds.Music.LOBBY_INTRO)
            }
        }
        for(player in Bukkit.getOnlinePlayers()) {
            if(player.burbPlayer().playerTeam !in listOf(Teams.SPECTATOR, Teams.NULL)) {
                TeamManager.enableTeamGlowing(player)
                SpawnPoints.respawnLocation(player)
            }
            Jukebox.disconnect(player)
        }
    }

    private fun startOvertime() {
        ChatUtility.messageAudience(Audience.audience(Bukkit.getOnlinePlayers()), "${Translation.Generic.ARROW_PREFIX}${Translation.Overtime.OVERTIME_PREFIX}${Translation.Overtime.OVERTIME_REASON}", false)
    }

    private fun gameEnd() {
        for(player in Bukkit.getOnlinePlayers()) {
            player.playSound(Sounds.Round.GAME_OVER)
            player.playSound(Sounds.Round.ROUND_END)
            Jukebox.disconnect(player)
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
        TeamManager.showTeamNametags()
    }

    private fun roundEnd() {
        for(player in Bukkit.getOnlinePlayers()) {
            Jukebox.disconnect(player)
            player.playSound(Sounds.Round.ROUND_END)
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
        TeamManager.showTeamNametags()
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
        const val IN_GAME_TIME = 900
        const val ROUND_END_TIME = 15
        const val GAME_END_TIME = 100
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
        if (newRound == round) return
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
        if (newTime == timer) return
        this.timer = newTime
        this.displayTime = String.format("%02d:%02d", (this.timer + 1) / 60, (this.timer + 1) % 60)
        InfoBoardManager.updateTimer()
        if (sender != null) {
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
        if (newState == timerState) return
        ChatUtility.broadcastDev("<dark_gray>Timer State: <red>$timerState<reset> <aqua>-> <green>$newState<dark_gray>${if (sender != null) " [${sender.name}]." else "."}", true)
        this.timerState = newState
    }

    fun getTimerState() : TimerState {
        return this.timerState
    }
}

object CapturePointManager {
    private val capturePoints = mutableMapOf<CapturePoint, Location>()
    private val capturePointTasks = mutableMapOf<CapturePoint, BukkitRunnable>()
    private val capturedPoints = mutableMapOf<CapturePoint, Teams>()
    private var suburbinatingTeam = Teams.NULL
    var capturePointScores = mutableMapOf<CapturePoint, Pair<Int, Teams>>()
    const val REQUIRED_CAPTURE_SCORE = 100

    fun initializeCapturePoints() {
        listOf(
            CapturePoint.A to Location(Bukkit.getWorlds()[0], 80.5, 2.0, 104.5),
            CapturePoint.B to Location(Bukkit.getWorlds()[0], 17.5, 3.0, 153.5),
            CapturePoint.C to Location(Bukkit.getWorlds()[0], -43.5, 1.0, 108.5)
        ).forEach { (point, location) -> addCapturePoint(point, location) }
    }

    fun clearCapturePoints() {
        for(point in capturePoints) removeCapturePoint(point.key)
        capturePoints.clear()
        suburbinatingTeam = Teams.NULL
    }

    private fun addCapturePoint(capturePoint: CapturePoint, location: Location) {
        if(capturePoints.containsKey(capturePoint)) return
        capturePoints[capturePoint] = location
        capturePoint.location = location
        capturePointTask(capturePoint)
    }

    private fun removeCapturePoint(capturePoint: CapturePoint) {
        capturePointTasks[capturePoint]?.cancel()
        capturePointTasks.remove(capturePoint)
        capturedPoints.remove(capturePoint)

        capturePoint.location = Location(Bukkit.getWorlds()[0], 0.5, 30.0, 0.5)

        Bukkit.getWorlds().forEach { world ->
            world.getEntitiesByClass(TextDisplay::class.java)
                .filter { it.scoreboardTags.contains("burb.game.capture_point.text_display") }
                .forEach { it.remove() }
        }
    }

    fun getCapturePointData(capturePoint: CapturePoint): Pair<Int, Teams> {
        return if(capturePointScores[capturePoint] != null) {
            capturePointScores[capturePoint]!!
        } else {
            Pair(0, Teams.NULL)
        }
    }

    fun updateSuburbination() {
        val teamCounts = capturedPoints.values.groupingBy { it }.eachCount()
        val newSuburbinationTeam = when {
            teamCounts[Teams.PLANTS] == 3 -> Teams.PLANTS
            teamCounts[Teams.ZOMBIES] == 3 -> Teams.ZOMBIES
            else -> Teams.NULL
        }

        if (suburbinatingTeam == Teams.PLANTS) ScoreManager.addPlantsScore(3)
        if (suburbinatingTeam == Teams.ZOMBIES) ScoreManager.addZombiesScore(3)

        if (newSuburbinationTeam != suburbinatingTeam) {
            suburbinatingTeam = newSuburbinationTeam

            val message = when (newSuburbinationTeam) {
                Teams.PLANTS -> "<newline><plantscolour><bold>SUBURBINATION<reset>: The plants are now suburbinating.<newline>"
                Teams.ZOMBIES -> "<newline><zombiescolour><bold>SUBURBINATION<reset>: The zombies are now suburbinating.<newline>"
                else -> "<newline><speccolour><bold>SUBURBINATION<reset>: Suburbination is no longer active.<newline>"
            }
            Bukkit.getOnlinePlayers().forEach { player ->
                player.sendMessage(Formatting.allTags.deserialize(message))
                if (Timer.getTimer() > 90) {
                    val music = when (newSuburbinationTeam) {
                        Teams.PLANTS -> Music.SUBURBINATION_PLANTS
                        Teams.ZOMBIES -> Music.SUBURBINATION_ZOMBIES
                        else -> if(Jukebox.getMusicStress() == MusicStress.LOW) Music.RANDOM_LOW else if(Jukebox.getMusicStress() == MusicStress.MEDIUM) Music.RANDOM_MEDIUM else if(Jukebox.getMusicStress() == MusicStress.HIGH) Music.RANDOM_HIGH else Music.NULL
                    }
                    Jukebox.startMusicLoop(player, plugin, music)
                }
            }
            when(newSuburbinationTeam) {
                Teams.PLANTS -> for(player in Bukkit.getOnlinePlayers().filter { filter -> filter.burbPlayer().playerTeam == Teams.PLANTS }) { BurbProgression.appendExperience(player, 40) }
                Teams.ZOMBIES -> for(player in Bukkit.getOnlinePlayers().filter { filter -> filter.burbPlayer().playerTeam == Teams.ZOMBIES }) { BurbProgression.appendExperience(player, 40) }
                else -> {}
            }
        }
    }

    private fun capturePointTask(capturePoint: CapturePoint) {
        val task = object : BukkitRunnable() {
            var plantProgress = 0
            var zombieProgress = 0
            var dominatingTeam = Teams.NULL
            var lastCapturedTeam = Teams.NULL
            var textDisplay: TextDisplay? = null

            override fun run() {
                if(GameManager.getGameState() == GameState.GAME_END) {
                    plantProgress = 0
                    zombieProgress = 0
                    dominatingTeam = Teams.NULL
                    lastCapturedTeam = Teams.NULL
                    textDisplay?.remove()
                }
                if(GameManager.getGameState() !in listOf(GameState.IN_GAME, GameState.OVERTIME)) return

                capturePointScores[capturePoint] = if(plantProgress > zombieProgress) Pair(plantProgress, Teams.PLANTS) else if(zombieProgress > plantProgress) Pair(zombieProgress, Teams.ZOMBIES) else Pair(0, Teams.NULL)

                val location = capturePoint.location
                if(textDisplay == null || textDisplay!!.isDead) {
                    textDisplay = location.world.spawn(location.clone().add(0.0, 7.0, 0.0), TextDisplay::class.java).apply {
                        scoreboardTags.add("burb.game.capture_point.text_display")
                        alignment = TextDisplay.TextAlignment.CENTER
                        billboard = Display.Billboard.VERTICAL
                    }
                }

                val playersInRange = TeamManager.getParticipants().filter {
                    it.getBukkitPlayer().location.distanceSquared(location) <= 25.0 && (it.getBukkitPlayer().vehicle == null || (it.getBukkitPlayer().vehicle != null && it.getBukkitPlayer().vehicle?.scoreboardTags?.contains("${it.getBukkitPlayer().uniqueId}-death-vehicle") == false))
                }
                val plants = playersInRange.count { it.playerTeam == Teams.PLANTS }
                val zombies = playersInRange.count { it.playerTeam == Teams.ZOMBIES }
                val contested = plants > 0 && zombies > 0

                dominatingTeam = when {
                    plants > zombies -> Teams.PLANTS
                    zombies > plants -> Teams.ZOMBIES
                    else -> Teams.NULL
                }

                // Uncontested progress adding
                if(!contested) {
                    when(dominatingTeam) {
                        Teams.PLANTS -> {
                            if (zombieProgress > 0) zombieProgress--
                            else if (plantProgress < REQUIRED_CAPTURE_SCORE) {
                                plantProgress++
                                ScoreManager.addPlantsScore(1)
                            }
                        }
                        Teams.ZOMBIES -> {
                            if (plantProgress > 0) plantProgress--
                            else if (zombieProgress < REQUIRED_CAPTURE_SCORE) {
                                zombieProgress++
                                ScoreManager.addZombiesScore(1)
                            }
                        }
                        else -> {}
                    }
                }

                // Bump capture progress back up if uncontested but team had last captured it
                if(lastCapturedTeam == Teams.PLANTS && plantProgress >= 1 && plantProgress > REQUIRED_CAPTURE_SCORE && !contested && plants == 0 && zombies == 0) {
                    plantProgress++
                }
                // Bump capture progress back up if uncontested but team had last captured it
                if(lastCapturedTeam == Teams.ZOMBIES && zombieProgress >= 1 && zombieProgress > REQUIRED_CAPTURE_SCORE  && !contested && plants == 0 && zombies == 0) {
                    zombieProgress++
                }

                // Uncapture point
                if(plantProgress == 0 && zombieProgress == 0) {
                    capturedPoints.remove(capturePoint)
                }

                // Point text display information
                textDisplay!!.text(Formatting.allTags.deserialize("<font:burb:font><yellow><bold>POINT <gold>[<yellow>${capturePoint}<gold>]<newline><newline><burbcolour>${
                    when {
                        plantProgress == REQUIRED_CAPTURE_SCORE -> "<font:burb:font>Plants own point"
                        zombieProgress == REQUIRED_CAPTURE_SCORE -> "<font:burb:font>Zombies own point"
                        dominatingTeam == Teams.PLANTS -> "<font:burb:font>Plants taking over"
                        dominatingTeam == Teams.ZOMBIES -> "<font:burb:font>Zombies taking over"
                        contested -> "<reset><font:burb:font><red><b>CONTESTED"
                        else -> "<reset><font:burb:font><b>UNCONTESTED"
                    }
                }<reset><font:burb:font><newline><newline><b>${if(plantProgress > zombieProgress) "<plantscolour>${plantProgress}" else if(zombieProgress > plantProgress) "<zombiescolour>${zombieProgress}" else "<speccolour>0"}<newline>"))

                // Coloured particle ring to show point status
                spawnCaptureParticles(location, dominatingTeam, plantProgress, zombieProgress, contested)

                // On capture point
                if((plantProgress == REQUIRED_CAPTURE_SCORE || zombieProgress == REQUIRED_CAPTURE_SCORE) && lastCapturedTeam != dominatingTeam) {
                    if(dominatingTeam !in listOf(Teams.NULL, Teams.SPECTATOR)) {
                        if(plantProgress >= REQUIRED_CAPTURE_SCORE) ScoreManager.addPlantsScore(1000)
                        if(zombieProgress >= REQUIRED_CAPTURE_SCORE) ScoreManager.addZombiesScore(1000)
                        capturedPoints[capturePoint] = dominatingTeam
                        lastCapturedTeam = dominatingTeam
                        Bukkit.getOnlinePlayers().forEach {
                            it.sendMessage(Formatting.allTags.deserialize("${Translation.Generic.ARROW_PREFIX}<yellow>Point $capturePoint<reset> is now controlled by the ${dominatingTeam.teamColourTag}${dominatingTeam.teamName}<reset>."))
                            it.playSound(if(it.burbPlayer().playerTeam == lastCapturedTeam) Sounds.Score.CAPTURE_FRIENDLY else Sounds.Score.CAPTURE_UNFRIENDLY)
                            if(it.burbPlayer().playerTeam == dominatingTeam) BurbProgression.appendExperience(it, 10)
                        }
                    }
                }
                // Increment score if not captured
                if(plantProgress != REQUIRED_CAPTURE_SCORE || zombieProgress != REQUIRED_CAPTURE_SCORE) {
                    if (capturedPoints.containsKey(capturePoint) && suburbinatingTeam != capturedPoints[capturePoint]) {
                        capturedPoints[capturePoint]?.let { ScoreManager.addScore(it, 1) }
                    }
                }

                // Increment score if captured
                if(capturedPoints.containsKey(capturePoint)) {
                    capturedPoints[capturePoint]?.let { ScoreManager.addScore(it, 2) }
                }

                // Check for suburbination
                updateSuburbination()
            }
        }
        task.runTaskTimer(plugin, 0L, 1L)
        capturePointTasks[capturePoint] = task
    }

    private fun spawnCaptureParticles(location: Location, dominatingTeam: Teams, plantProgress: Int, zombieProgress: Int, contested: Boolean) {
        object : BukkitRunnable() {
            override fun run() {
                repeat(32) { i ->
                    val angle = Math.toRadians(i * 11.25)
                    val x = cos(angle) * 5.0
                    val z = sin(angle) * 5.0
                    location.world.spawnParticle(
                        Particle.DUST,
                        location.x + x, location.y + 0.25, location.z + z,
                        1, 0.0, 0.0, 0.0, 0.0,
                        Particle.DustOptions(
                            when {
                                contested -> Color.RED
                                plantProgress == REQUIRED_CAPTURE_SCORE -> Color.LIME
                                zombieProgress == REQUIRED_CAPTURE_SCORE -> Color.PURPLE
                                dominatingTeam == Teams.PLANTS -> Color.LIME
                                dominatingTeam == Teams.ZOMBIES -> Color.PURPLE
                                else -> Color.WHITE
                            }, 0.75f
                        ),
                        true
                    )
                }
            }
        }.runTask(plugin)
    }

    fun getSuburbinatingTeam(): Teams {
        return suburbinatingTeam
    }

    fun isSuburbinating(): Boolean {
        return suburbinatingTeam in listOf(Teams.PLANTS, Teams.ZOMBIES)
    }
}

object ScoreManager {
    private var plantsScore = 0
    private var zombiesScore = 0
    private const val WIN_SCORE = 100000

    fun getWinningTeam(): Teams {
        if (plantsScore > zombiesScore) return Teams.PLANTS
        if (zombiesScore > plantsScore) return Teams.ZOMBIES
        return Teams.NULL
    }

    private fun teamScoreWinCheck() {
        if ((plantsScore >= WIN_SCORE || zombiesScore >= WIN_SCORE) && (GameManager.getGameState() == GameState.IN_GAME || GameManager.getGameState() == GameState.OVERTIME)) {
            GameManager.nextState()
        }
    }

    fun getPlacementMap(): Map<Teams, Int> {
        return mutableMapOf(Pair(Teams.PLANTS, getDisplayScore(Teams.PLANTS)), Pair(Teams.ZOMBIES, getDisplayScore(Teams.ZOMBIES))).toList().sortedBy { (_, scores) -> scores }.reversed().toMap()
    }

    fun addScore(team: Teams, score: Int) {
        if (team == Teams.PLANTS) this.plantsScore += score
        if (team == Teams.ZOMBIES) this.zombiesScore += score
        InfoBoardManager.updateScore()
        teamScoreWinCheck()
    }

    fun addPlantsScore(score: Int) {
        this.plantsScore += score
        InfoBoardManager.updateScore()
        teamScoreWinCheck()
    }

    fun addZombiesScore(score: Int) {
        this.zombiesScore += score
        InfoBoardManager.updateScore()
        teamScoreWinCheck()
    }

    fun subPlantsScore(score: Int) {
        this.plantsScore -= score
        InfoBoardManager.updateScore()
        teamScoreWinCheck()
    }

    fun subZombiesScore(score: Int) {
        this.zombiesScore -= score
        InfoBoardManager.updateScore()
        teamScoreWinCheck()
    }

    fun setPlantsScore(score: Int) {
        this.plantsScore = score
        InfoBoardManager.updateScore()
        teamScoreWinCheck()
    }

    fun setZombiesScore(score: Int) {
        this.zombiesScore = score
        InfoBoardManager.updateScore()
        teamScoreWinCheck()
    }

    fun getDisplayScore(teams: Teams): Int {
        return when(teams) {
            Teams.PLANTS -> {
                this.plantsScore.floorDiv(1000)
            }
            Teams.ZOMBIES -> {
                this.zombiesScore.floorDiv(1000)
            } else -> -1
        }
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