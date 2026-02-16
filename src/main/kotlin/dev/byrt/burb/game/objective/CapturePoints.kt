package dev.byrt.burb.game.objective

import dev.byrt.burb.game.Game
import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.game.Scores
import dev.byrt.burb.game.Timer
import dev.byrt.burb.game.events.SpecialEvents
import dev.byrt.burb.game.location.BurbAreas
import dev.byrt.burb.game.visual.GameDayTime
import dev.byrt.burb.game.visual.GameVisuals
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.library.Translation
import dev.byrt.burb.music.Jukebox
import dev.byrt.burb.music.Music
import dev.byrt.burb.music.MusicStress
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.player.progression.BurbPlayerData
import dev.byrt.burb.plugin
import dev.byrt.burb.team.BurbTeam
import dev.byrt.burb.text.Formatting
import dev.byrt.burb.text.Formatting.sendTranslated
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.Component.translatable
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Display
import org.bukkit.entity.TextDisplay
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Transformation
import kotlin.math.cos
import kotlin.math.sin

// I'm sorry to anyone reading this class, I do not know what I wrote here but it works so it's staying.
object CapturePoints {
    private val capturePoints = mutableMapOf<CapturePoint, Location>()
    private val capturedPoints = mutableMapOf<CapturePoint, BurbTeam>()
    private val capturePointTasks = mutableMapOf<CapturePoint, BukkitRunnable>()
    private var capturePointScores = mutableMapOf<CapturePoint, Pair<Int, BurbTeam>>()
    private var suburbinatingTeam: BurbTeam? = null
    const val REQUIRED_CAPTURE_SCORE = 100

    fun initializeCapturePoints() {
        listOf(
            CapturePoint.A to Location(Bukkit.getWorlds()[0], 80.5, 2.0, 104.5),
            CapturePoint.B to Location(Bukkit.getWorlds()[0], 17.5, 3.0, 153.5),
            CapturePoint.C to Location(Bukkit.getWorlds()[0], -43.5, 0.0, 108.5)
        ).forEach { (point, location) -> addCapturePoint(point, location) }
    }

    fun clearCapturePoints() {
        for (point in capturePoints) removeCapturePoint(point.key)
        capturePoints.clear()
        suburbinatingTeam = null
    }

    private fun addCapturePoint(capturePoint: CapturePoint, location: Location) {
        if (capturePoints.containsKey(capturePoint)) return
        capturePoints[capturePoint] = location
        capturePoint.location = location
        capturePointTask(capturePoint)
    }

    private fun removeCapturePoint(capturePoint: CapturePoint) {
        capturePointTasks[capturePoint]?.cancel()
        capturePointTasks.remove(capturePoint)
        capturedPoints.remove(capturePoint)

        capturePoint.location = Location(Bukkit.getWorlds()[0], 0.5, 30.0, 0.5)
        capturePoint.plantProgress = 0
        capturePoint.zombieProgress = 0
        capturePoint.dominatingTeam = null
        capturePoint.lastCapturedTeam = null

        Bukkit.getWorlds().forEach { world ->
            world.getEntitiesByClass(TextDisplay::class.java)
                .filter { it.scoreboardTags.contains("burb.game.capture_point.text_display") }
                .forEach { it.remove() }
        }
    }

    fun getCapturePointData(capturePoint: CapturePoint): Pair<Int, BurbTeam>? = capturePointScores[capturePoint]


    fun updateSuburbination() {
        val teamCounts = capturedPoints.values.groupingBy { it }.eachCount()
        val newSuburbinationTeam = when {
            teamCounts[BurbTeam.PLANTS] == 3 -> BurbTeam.PLANTS
            teamCounts[BurbTeam.ZOMBIES] == 3 -> BurbTeam.ZOMBIES
            else -> null
        }

        suburbinatingTeam?.let { Scores.addScore(it, 2) }

        if (newSuburbinationTeam != suburbinatingTeam) {
            suburbinatingTeam = newSuburbinationTeam

            val message = when (newSuburbinationTeam) {
                BurbTeam.PLANTS -> "<newline><plantscolour><bold>SUBURBINATION<reset>: The plants are now suburbinating.<newline>"
                BurbTeam.ZOMBIES -> "<newline><zombiescolour><bold>SUBURBINATION<reset>: The zombies are now suburbinating.<newline>"
                else -> "<newline><speccolour><bold>SUBURBINATION<reset>: Suburbination is no longer active.<newline>"
            }
            Bukkit.getOnlinePlayers().forEach { player ->
                player.sendMessage(Formatting.allTags.deserialize(message))
                if (Timer.getTimer() > 120 && !SpecialEvents.isEventRunning()) {
                    val music = when (newSuburbinationTeam) {
                        BurbTeam.PLANTS -> Music.SUBURBINATION_PLANTS
                        BurbTeam.ZOMBIES -> Music.SUBURBINATION_ZOMBIES
                        else -> if (Jukebox.getMusicStress() == MusicStress.LOW) Music.RANDOM_LOW else if (Jukebox.getMusicStress() == MusicStress.MEDIUM) Music.RANDOM_MEDIUM else if (Jukebox.getMusicStress() == MusicStress.HIGH) Music.RANDOM_HIGH else Music.NULL
                    }
                    Jukebox.startMusicLoop(player, music)
                }
            }
            when (newSuburbinationTeam) {
                BurbTeam.PLANTS -> for (player in Bukkit.getOnlinePlayers()
                    .filter { filter -> filter.burbPlayer().playerTeam == BurbTeam.PLANTS }) {
                    BurbPlayerData.appendExperience(player, 150)
                }

                BurbTeam.ZOMBIES -> for (player in Bukkit.getOnlinePlayers()
                    .filter { filter -> filter.burbPlayer().playerTeam == BurbTeam.ZOMBIES }) {
                    BurbPlayerData.appendExperience(player, 150)
                }

                else -> {}
            }

            if (newSuburbinationTeam != null) {
                GameVisuals.setDayTime(GameDayTime.NIGHT)
                BurbAreas.runSuburbinationShow(newSuburbinationTeam)
            } else {
                GameVisuals.setDayTime(GameDayTime.DAY)
            }
        }
    }

    private fun capturePointTask(capturePoint: CapturePoint) {
        val task = object : BukkitRunnable() {
            var textDisplay: TextDisplay? = null
            override fun run() {
                if (GameManager.getGameState() == GameState.GAME_END) {
                    capturePoint.plantProgress = 0
                    capturePoint.zombieProgress = 0
                    capturePoint.dominatingTeam = null
                    capturePoint.lastCapturedTeam = null
                    textDisplay?.remove()
                }
                if (GameManager.getGameState() !in listOf(GameState.IN_GAME, GameState.OVERTIME)) return

                val newScore =  if (capturePoint.plantProgress > capturePoint.zombieProgress)
                    Pair(capturePoint.plantProgress, BurbTeam.PLANTS)
                else if (capturePoint.zombieProgress > capturePoint.plantProgress)
                    Pair(
                        capturePoint.zombieProgress, BurbTeam.ZOMBIES
                    ) else null

                if (newScore != null) {
                    capturePointScores[capturePoint] = newScore
                } else {
                    capturePointScores -= capturePoint
                }

                val location = capturePoint.location
                if (textDisplay == null || textDisplay!!.isDead) {
                    textDisplay =
                        location.world.spawn(location.clone().add(0.0, 7.0, 0.0), TextDisplay::class.java).apply {
                            scoreboardTags.add("burb.game.capture_point.text_display")
                            alignment = TextDisplay.TextAlignment.CENTER
                            billboard = Display.Billboard.VERTICAL
                            transformation = Transformation(
                                transformation.translation,
                                transformation.leftRotation,
                                transformation.scale.add(1.5f, 1.5f, 1.5f),
                                transformation.rightRotation
                            )
                        }
                }

                val playersInRange = GameManager.teams.allParticipants()
                    .filter { !it.isDead && it.bukkitPlayer().location.distanceSquared(location) <= 25.0 }
                    .groupingBy { it.playerTeam }
                    .eachCount()

                val plants = playersInRange[BurbTeam.PLANTS] ?: 0
                val zombies = playersInRange[BurbTeam.ZOMBIES] ?: 0

                val contested = plants > 0 && zombies > 0

                capturePoint.dominatingTeam = when {
                    plants > zombies -> BurbTeam.PLANTS
                    zombies > plants -> BurbTeam.ZOMBIES
                    else -> null
                }

                // Uncontested progress adding
                if (!contested) {
                    when (capturePoint.dominatingTeam) {
                        BurbTeam.PLANTS -> {
                            if (capturePoint.zombieProgress > 0) capturePoint.zombieProgress--
                            else if (capturePoint.plantProgress < REQUIRED_CAPTURE_SCORE) {
                                capturePoint.plantProgress++
                            }
                        }

                        BurbTeam.ZOMBIES -> {
                            if (capturePoint.plantProgress > 0) capturePoint.plantProgress--
                            else if (capturePoint.zombieProgress < REQUIRED_CAPTURE_SCORE) {
                                capturePoint.zombieProgress++
                            }
                        }

                        else -> {}
                    }
                }

                // Bump capture progress back up if uncontested but team had last captured it
                if (capturePoint.lastCapturedTeam == BurbTeam.PLANTS && capturePoint.plantProgress >= 1 && capturePoint.plantProgress < REQUIRED_CAPTURE_SCORE && !contested && plants == 0 && zombies == 0) {
                    capturePoint.plantProgress++
                }
                if (capturePoint.lastCapturedTeam == BurbTeam.ZOMBIES && capturePoint.zombieProgress >= 1 && capturePoint.zombieProgress < REQUIRED_CAPTURE_SCORE && !contested && plants == 0 && zombies == 0) {
                    capturePoint.zombieProgress++
                }

                // Uncapture point
                if (capturePoint.plantProgress == 0 && capturePoint.zombieProgress == 0 && capturePoint.lastCapturedTeam in listOf(
                        BurbTeam.PLANTS,
                        BurbTeam.ZOMBIES
                    )
                ) {
                    Bukkit.getOnlinePlayers().forEach {
                        it.sendTranslated("burb.capture_point.lost", text(capturePoint.pointName), capturePoint.lastCapturedTeam ?: Component.empty())
                        it.playSound(if (it.burbPlayer().playerTeam == capturePoint.lastCapturedTeam) Sounds.Score.CAPTURE_UNFRIENDLY else Sounds.Score.CAPTURE_FRIENDLY)
                    }
                    capturedPoints.remove(capturePoint)
                    capturePoint.lastCapturedTeam = null
                }

                // Point text display information
                textDisplay?.text(
                    Formatting.allTags.deserialize(
                        "<font:burb:font><yellow><b>${capturePoint.pointName}<newline><newline></b>${
                            when {
                                capturePoint.plantProgress == REQUIRED_CAPTURE_SCORE -> "<font:burb:font><plantscolour>Plants own point"
                                capturePoint.zombieProgress == REQUIRED_CAPTURE_SCORE -> "<font:burb:font><zombiescolour>Zombies own point"
                                capturePoint.dominatingTeam == BurbTeam.PLANTS -> "<font:burb:font><plantscolour>Plants taking over"
                                capturePoint.dominatingTeam == BurbTeam.ZOMBIES -> "<font:burb:font><zombiescolour>Zombies taking over"
                                contested -> "<reset><font:burb:font><red><b>CONTESTED"
                                else -> "<reset><font:burb:font><b>UNCONTESTED"
                            }
                        }<reset><font:burb:font><newline><newline>${if (capturePoint.plantProgress > capturePoint.zombieProgress) "<plantscolour>${capturePoint.plantProgress}" else if (capturePoint.zombieProgress > capturePoint.plantProgress) "<zombiescolour>${capturePoint.zombieProgress}" else "<speccolour>0"}<newline>"
                    )
                )

                // Coloured particle ring to show point status
                spawnCaptureParticles(
                    location,
                    capturePoint.dominatingTeam,
                    capturePoint.plantProgress,
                    capturePoint.zombieProgress,
                    contested
                )

                // On capture point
                if ((capturePoint.plantProgress == REQUIRED_CAPTURE_SCORE || capturePoint.zombieProgress == REQUIRED_CAPTURE_SCORE) && capturePoint.lastCapturedTeam != capturePoint.dominatingTeam) {
                    capturePoint.dominatingTeam?.let { dominatingTeam ->
                        capturedPoints[capturePoint] = dominatingTeam
                        capturePoint.lastCapturedTeam = dominatingTeam
                        Bukkit.getOnlinePlayers().forEach {
                            it.sendTranslated("burb.capture_point.captured", text(capturePoint.pointName), dominatingTeam)
                            it.playSound(if (it.burbPlayer().playerTeam == capturePoint.lastCapturedTeam) Sounds.Score.CAPTURE_FRIENDLY else Sounds.Score.CAPTURE_UNFRIENDLY)
                            if (it.burbPlayer().playerTeam == capturePoint.dominatingTeam) BurbPlayerData.appendExperience(
                                it,
                                10
                            )
                        }
                    }
                }

                // Increment score if captured
                if (capturedPoints.containsKey(capturePoint)) {
                    capturedPoints[capturePoint]?.let { Scores.addScore(it, 1) }
                }

                // Check for suburbination
                updateSuburbination()
            }
        }
        task.runTaskTimer(plugin, 0L, 1L)
        capturePointTasks[capturePoint] = task
    }

    private fun spawnCaptureParticles(
        location: Location,
        dominatingTeam: BurbTeam?,
        plantProgress: Int,
        zombieProgress: Int,
        contested: Boolean
    ) {
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
                                dominatingTeam == BurbTeam.PLANTS -> Color.LIME
                                dominatingTeam == BurbTeam.ZOMBIES -> Color.PURPLE
                                else -> Color.WHITE
                            }, 0.75f
                        ),
                        true
                    )
                }
            }
        }.runTask(plugin)
    }

    fun getSuburbinatingTeam(): BurbTeam? {
        return suburbinatingTeam
    }

    fun isSuburbinating(): Boolean {
        return suburbinatingTeam != null
    }
}

enum class CapturePoint(
    val pointName: String,
    var location: Location,
    var plantProgress: Int,
    var zombieProgress: Int,
    var dominatingTeam: BurbTeam?,
    var lastCapturedTeam: BurbTeam?
) {
    A(
        "Perilous Park",
        Location(Bukkit.getWorlds()[0], 0.5, 30.0, 0.5),
        plantProgress = 0,
        zombieProgress = 0,
        null,
        null
    ),
    B(
        "Mount Burbmore",
        Location(Bukkit.getWorlds()[0], 0.5, 30.0, 0.5),
        plantProgress = 0,
        zombieProgress = 0,
        null,
        null
    ),
    C(
        "Treetop Towers",
        Location(Bukkit.getWorlds()[0], 0.5, 30.0, 0.5),
        plantProgress = 0,
        zombieProgress = 0,
        null,
        null
    );
}