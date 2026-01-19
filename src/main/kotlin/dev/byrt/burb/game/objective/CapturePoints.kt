package dev.byrt.burb.game.objective

import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.game.Scores
import dev.byrt.burb.game.Timer
import dev.byrt.burb.game.events.SpecialEvents
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.library.Translation
import dev.byrt.burb.music.Jukebox
import dev.byrt.burb.music.Music
import dev.byrt.burb.music.MusicStress
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.player.progression.BurbPlayerData
import dev.byrt.burb.plugin
import dev.byrt.burb.team.TeamManager
import dev.byrt.burb.team.Teams
import dev.byrt.burb.text.Formatting
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
    private val capturedPoints = mutableMapOf<CapturePoint, Teams>()
    private val capturePointTasks = mutableMapOf<CapturePoint, BukkitRunnable>()
    private var capturePointScores = mutableMapOf<CapturePoint, Pair<Int, Teams>>()
    private var suburbinatingTeam = Teams.NULL
    const val REQUIRED_CAPTURE_SCORE = 100

    fun initializeCapturePoints() {
        listOf(
            CapturePoint.A to Location(Bukkit.getWorlds()[0], 80.5, 2.0, 104.5),
            CapturePoint.B to Location(Bukkit.getWorlds()[0], 17.5, 3.0, 153.5),
            CapturePoint.C to Location(Bukkit.getWorlds()[0], -43.5, 0.0, 108.5)
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
        capturePoint.plantProgress = 0
        capturePoint.zombieProgress = 0
        capturePoint.dominatingTeam = Teams.NULL
        capturePoint.lastCapturedTeam = Teams.NULL

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

        Scores.addScore(suburbinatingTeam, 3)

        if (newSuburbinationTeam != suburbinatingTeam) {
            suburbinatingTeam = newSuburbinationTeam

            val message = when (newSuburbinationTeam) {
                Teams.PLANTS -> "<newline><plantscolour><bold>SUBURBINATION<reset>: The plants are now suburbinating.<newline>"
                Teams.ZOMBIES -> "<newline><zombiescolour><bold>SUBURBINATION<reset>: The zombies are now suburbinating.<newline>"
                else -> "<newline><speccolour><bold>SUBURBINATION<reset>: Suburbination is no longer active.<newline>"
            }
            Bukkit.getOnlinePlayers().forEach { player ->
                player.sendMessage(Formatting.allTags.deserialize(message))
                if (Timer.getTimer() > 120 && !SpecialEvents.isEventRunning()) {
                    val music = when (newSuburbinationTeam) {
                        Teams.PLANTS -> Music.SUBURBINATION_PLANTS
                        Teams.ZOMBIES -> Music.SUBURBINATION_ZOMBIES
                        else -> if(Jukebox.getMusicStress() == MusicStress.LOW) Music.RANDOM_LOW else if(Jukebox.getMusicStress() == MusicStress.MEDIUM) Music.RANDOM_MEDIUM else if(Jukebox.getMusicStress() == MusicStress.HIGH) Music.RANDOM_HIGH else Music.NULL
                    }
                    Jukebox.startMusicLoop(player, music)
                }
            }
            when(newSuburbinationTeam) {
                Teams.PLANTS -> for(player in Bukkit.getOnlinePlayers()
                    .filter { filter -> filter.burbPlayer().playerTeam == Teams.PLANTS }) {
                    BurbPlayerData.appendExperience(player, 150)
                }
                Teams.ZOMBIES -> for(player in Bukkit.getOnlinePlayers()
                    .filter { filter -> filter.burbPlayer().playerTeam == Teams.ZOMBIES }) {
                    BurbPlayerData.appendExperience(player, 150)
                }
                else -> {}
            }
        }
    }

    private fun capturePointTask(capturePoint: CapturePoint) {
        val task = object : BukkitRunnable() {
            var textDisplay: TextDisplay? = null
            override fun run() {
                if(GameManager.getGameState() == GameState.GAME_END) {
                    capturePoint.plantProgress = 0
                    capturePoint.zombieProgress = 0
                    capturePoint.dominatingTeam = Teams.NULL
                    capturePoint.lastCapturedTeam = Teams.NULL
                    textDisplay?.remove()
                }
                if(GameManager.getGameState() !in listOf(GameState.IN_GAME, GameState.OVERTIME)) return

                capturePointScores[capturePoint] = if(capturePoint.plantProgress > capturePoint.zombieProgress)
                    Pair(capturePoint.plantProgress, Teams.PLANTS)
                else if(capturePoint.zombieProgress > capturePoint.plantProgress)
                    Pair(capturePoint.zombieProgress, Teams.ZOMBIES
                ) else Pair(0, Teams.NULL)

                val location = capturePoint.location
                if(textDisplay == null || textDisplay!!.isDead) {
                    textDisplay = location.world.spawn(location.clone().add(0.0, 7.0, 0.0), TextDisplay::class.java).apply {
                        scoreboardTags.add("burb.game.capture_point.text_display")
                        alignment = TextDisplay.TextAlignment.CENTER
                        billboard = Display.Billboard.VERTICAL
                        transformation = Transformation(transformation.translation, transformation.leftRotation, transformation.scale.add(1.5f, 1.5f, 1.5f), transformation.rightRotation)
                    }
                }

                val playersInRange = TeamManager.getParticipants().filter {
                    it.getBukkitPlayer().location.distanceSquared(location) <= 25.0 && !it.isDead
                }
                val plants = playersInRange.count { it.playerTeam == Teams.PLANTS }
                val zombies = playersInRange.count { it.playerTeam == Teams.ZOMBIES }
                val contested = plants > 0 && zombies > 0

                capturePoint.dominatingTeam = when {
                    plants > zombies -> Teams.PLANTS
                    zombies > plants -> Teams.ZOMBIES
                    else -> Teams.NULL
                }

                // Uncontested progress adding
                if(!contested) {
                    when(capturePoint.dominatingTeam) {
                        Teams.PLANTS -> {
                            if (capturePoint.zombieProgress > 0) capturePoint.zombieProgress--
                            else if (capturePoint.plantProgress < REQUIRED_CAPTURE_SCORE) {
                                capturePoint.plantProgress++
                            }
                        }
                        Teams.ZOMBIES -> {
                            if (capturePoint.plantProgress > 0) capturePoint.plantProgress--
                            else if (capturePoint.zombieProgress < REQUIRED_CAPTURE_SCORE) {
                                capturePoint.zombieProgress++
                            }
                        }
                        else -> {}
                    }
                }

                // Bump capture progress back up if uncontested but team had last captured it
                if(capturePoint.lastCapturedTeam == Teams.PLANTS && capturePoint.plantProgress >= 1 && capturePoint.plantProgress < REQUIRED_CAPTURE_SCORE && !contested && plants == 0 && zombies == 0) {
                    capturePoint.plantProgress++
                }
                if(capturePoint.lastCapturedTeam == Teams.ZOMBIES && capturePoint.zombieProgress >= 1 && capturePoint.zombieProgress < REQUIRED_CAPTURE_SCORE  && !contested && plants == 0 && zombies == 0) {
                    capturePoint.zombieProgress++
                }

                // Uncapture point
                if(capturePoint.plantProgress == 0 && capturePoint.zombieProgress == 0 && capturePoint.lastCapturedTeam in listOf(Teams.PLANTS, Teams.ZOMBIES)) {
                    Bukkit.getOnlinePlayers().forEach {
                        it.sendMessage(Formatting.allTags.deserialize("${Translation.Generic.ARROW_PREFIX}<yellow>${capturePoint.pointName}<reset> is no longer controlled by the ${capturePoint.lastCapturedTeam.teamColourTag}${capturePoint.lastCapturedTeam.teamName}<reset>."))
                        it.playSound(if(it.burbPlayer().playerTeam == capturePoint.lastCapturedTeam) Sounds.Score.CAPTURE_UNFRIENDLY else Sounds.Score.CAPTURE_FRIENDLY)
                    }
                    capturedPoints.remove(capturePoint)
                    capturePoint.lastCapturedTeam = Teams.NULL
                }

                // Point text display information
                textDisplay?.text(
                    Formatting.allTags.deserialize("<font:burb:font><yellow><b>${capturePoint.pointName}<newline><newline></b>${
                    when {
                        capturePoint.plantProgress == REQUIRED_CAPTURE_SCORE -> "<font:burb:font><plantscolour>Plants own point"
                        capturePoint.zombieProgress == REQUIRED_CAPTURE_SCORE -> "<font:burb:font><zombiescolour>Zombies own point"
                        capturePoint.dominatingTeam == Teams.PLANTS -> "<font:burb:font><plantscolour>Plants taking over"
                        capturePoint.dominatingTeam == Teams.ZOMBIES -> "<font:burb:font><zombiescolour>Zombies taking over"
                        contested -> "<reset><font:burb:font><red><b>CONTESTED"
                        else -> "<reset><font:burb:font><b>UNCONTESTED"
                    }
                }<reset><font:burb:font><newline><newline>${if(capturePoint.plantProgress > capturePoint.zombieProgress) "<plantscolour>${capturePoint.plantProgress}" else if(capturePoint.zombieProgress > capturePoint.plantProgress) "<zombiescolour>${capturePoint.zombieProgress}" else "<speccolour>0"}<newline>"))

                // Coloured particle ring to show point status
                spawnCaptureParticles(location, capturePoint.dominatingTeam, capturePoint.plantProgress, capturePoint.zombieProgress, contested)

                // On capture point
                if((capturePoint.plantProgress == REQUIRED_CAPTURE_SCORE || capturePoint.zombieProgress == REQUIRED_CAPTURE_SCORE) && capturePoint.lastCapturedTeam != capturePoint.dominatingTeam) {
                    if(capturePoint.dominatingTeam !in listOf(Teams.NULL, Teams.SPECTATOR)) {
                        capturedPoints[capturePoint] = capturePoint.dominatingTeam
                        capturePoint.lastCapturedTeam = capturePoint.dominatingTeam
                        Bukkit.getOnlinePlayers().forEach {
                            it.sendMessage(Formatting.allTags.deserialize("${Translation.Generic.ARROW_PREFIX}<yellow>${capturePoint.pointName}<reset> is now controlled by the ${capturePoint.dominatingTeam.teamColourTag}${capturePoint.dominatingTeam.teamName}<reset>."))
                            it.playSound(if(it.burbPlayer().playerTeam == capturePoint.lastCapturedTeam) Sounds.Score.CAPTURE_FRIENDLY else Sounds.Score.CAPTURE_UNFRIENDLY)
                            if(it.burbPlayer().playerTeam == capturePoint.dominatingTeam) BurbPlayerData.appendExperience(it, 10)
                        }
                    }
                }

                // Increment score if captured
                if(capturedPoints.containsKey(capturePoint)) {
                    capturedPoints[capturePoint]?.let { Scores.addScore(it, 1) }
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

enum class CapturePoint(val pointName: String, var location: Location, var plantProgress: Int, var zombieProgress: Int, var dominatingTeam: Teams, var lastCapturedTeam: Teams) {
    A("Perilous Park", Location(Bukkit.getWorlds()[0], 0.5, 30.0, 0.5), plantProgress = 0, zombieProgress = 0, Teams.NULL, Teams.NULL),
    B("Mount Burbmore", Location(Bukkit.getWorlds()[0], 0.5, 30.0, 0.5), plantProgress = 0, zombieProgress = 0, Teams.NULL, Teams.NULL),
    C("Treetop Towers", Location(Bukkit.getWorlds()[0], 0.5, 30.0, 0.5), plantProgress = 0, zombieProgress = 0, Teams.NULL, Teams.NULL);
}