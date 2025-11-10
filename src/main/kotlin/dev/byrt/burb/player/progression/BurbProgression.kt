package dev.byrt.burb.player.progression

import dev.byrt.burb.game.objective.CapturePoints
import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.game.Timer
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.logger
import dev.byrt.burb.music.Jukebox
import dev.byrt.burb.music.Music
import dev.byrt.burb.music.MusicStress
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.plugin
import dev.byrt.burb.team.Teams
import dev.byrt.burb.text.ChatUtility
import dev.byrt.burb.text.Formatting
import net.kyori.adventure.title.Title
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.io.File
import java.time.Duration
import java.util.*

object BurbProgression {
    private const val EXPERIENCE_PATH = ".experience"
    private const val LEVEL_PATH = ".level"
    private val playerData = mutableMapOf<UUID, FileConfiguration>()

    fun getPlayerData(player: Player) {
        try {
            val folder = plugin.dataFolder
            if(!folder.exists()) folder.mkdirs()
            val playerFile = File(folder,"${player.uniqueId}.yml")
            if(!playerFile.exists()) playerFile.createNewFile()
            val fileConfiguration = YamlConfiguration.loadConfiguration(playerFile)
            if(fileConfiguration.get("${player.uniqueId}${EXPERIENCE_PATH}") == null) {
                fileConfiguration.set("${player.uniqueId}${EXPERIENCE_PATH}", 0)
            }
            if(fileConfiguration.get("${player.uniqueId}${LEVEL_PATH}") == null) {
                fileConfiguration.set("${player.uniqueId}${LEVEL_PATH}", BurbLevel.LEVEL_1.name)
            }
            val level = BurbLevel.valueOf(fileConfiguration.get("${player.uniqueId}${LEVEL_PATH}").toString()).ordinal + 1
            val exp = fileConfiguration.get("${player.uniqueId}${EXPERIENCE_PATH}") as Int / BurbLevel.valueOf(fileConfiguration.get("${player.uniqueId}${LEVEL_PATH}").toString()).requiredXp.toFloat()
            player.level = level
            player.exp = exp
            fileConfiguration.save(playerFile)
            if(playerData.containsKey(player.uniqueId)) playerData.remove(player.uniqueId)
            playerData[player.uniqueId] = fileConfiguration
            logger.info("Player data fetched for player ${player.name} (${player.uniqueId})")
        } catch(e: Exception) {
            ChatUtility.broadcastDev("<#ffff00>Something went wrong while trying to fetch player data for player ${player.name} (${player.uniqueId}).", false)
            logger.warning("Something went wrong while trying to fetch player data for player ${player.name} (${player.uniqueId}).")
        }
    }

    private fun getPlayerConfiguration(player: Player): FileConfiguration {
        return playerData[player.uniqueId]!!
    }

    fun appendExperience(player: Player, experienceGained: Int) {
        player.sendActionBar(Formatting.allTags.deserialize("<font:burb:font>+${experienceGained} <yellow>XP"))
        val config = getPlayerConfiguration(player)
        val currentExperience = config.get("${player.uniqueId}${EXPERIENCE_PATH}") as Int
        val currentLevel = BurbLevel.valueOf(config.get("${player.uniqueId}${LEVEL_PATH}").toString())

        if(currentLevel != BurbLevel.LEVEL_40) {
            if(currentExperience + experienceGained >= currentLevel.requiredXp) {
                if(currentExperience + experienceGained > currentLevel.requiredXp) {
                    val overflowXp = (currentExperience + experienceGained) - currentLevel.requiredXp
                    config.set("${player.uniqueId}${EXPERIENCE_PATH}", overflowXp)
                } else {
                    config.set("${player.uniqueId}${EXPERIENCE_PATH}", 0)
                }
                // Level up
                val newEvolution = BurbLevel.entries[currentLevel.ordinal + 1].levelName.endsWith("0")

                player.showTitle(
                    Title.title(
                        Formatting.allTags.deserialize(""),
                        Formatting.allTags.deserialize("You are now ${player.burbPlayer().playerTeam.teamColourTag}${BurbLevel.entries[currentLevel.ordinal + 1].levelName}<reset>."),
                        Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(2), Duration.ofMillis(500))
                    )
                )
                player.sendMessage(Formatting.allTags.deserialize("<b>${if(newEvolution) "<gradient:gold:yellow:gold:yellow:gold>" else "<burbcolour>"}LEVEL UP!<reset> You are now ${player.burbPlayer().playerTeam.teamColourTag}${BurbLevel.entries[currentLevel.ordinal + 1].levelName}<reset>."))
                Jukebox.disconnect(player)
                if(newEvolution) {
                    player.playSound(Sounds.Misc.ODE_TO_JOY)
                } else {
                    player.playSound(when(player.burbPlayer().playerTeam) {
                        Teams.PLANTS -> Sounds.Misc.LEVEL_UP_PLANTS
                        Teams.ZOMBIES -> Sounds.Misc.LEVEL_UP_ZOMBIES
                        else -> Sounds.Misc.SUCCESS
                    })
                }
                object : BukkitRunnable() {
                    override fun run() {
                        when(GameManager.getGameState()) {
                            GameState.IDLE -> {
                                Jukebox.startMusicLoop(player, plugin, Music.LOBBY_WAITING)
                            }
                            GameState.IN_GAME -> {
                                if(Timer.getTimer() <= 90) {
                                    Jukebox.startMusicLoop(player, plugin, Music.OVERTIME)
                                } else {
                                    val music = when(CapturePoints.getSuburbinatingTeam()) {
                                        Teams.PLANTS -> Music.SUBURBINATION_PLANTS
                                        Teams.ZOMBIES -> Music.SUBURBINATION_ZOMBIES
                                        else -> if(Jukebox.getMusicStress() == MusicStress.LOW) Music.RANDOM_LOW else if(Jukebox.getMusicStress() == MusicStress.MEDIUM) Music.RANDOM_MEDIUM else if(Jukebox.getMusicStress() == MusicStress.HIGH) Music.RANDOM_HIGH else Music.NULL
                                    }
                                    Jukebox.startMusicLoop(player, plugin, music)
                                }
                            }
                            GameState.GAME_END -> {
                                Jukebox.startMusicLoop(player, plugin, Music.POST_GAME)
                            }
                            else -> {}
                        }
                    }
                }.runTaskLater(plugin, if(newEvolution) 14 * 20L else 6 * 20L)
                config.set("${player.uniqueId}${LEVEL_PATH}", BurbLevel.entries[currentLevel.ordinal + 1].name)
            } else {
                config.set("${player.uniqueId}${EXPERIENCE_PATH}", currentExperience + experienceGained)
            }
            config.save(File(plugin.dataFolder,"${player.uniqueId}.yml"))

            // Set visuals for level and experience
            val level = BurbLevel.valueOf(config.get("${player.uniqueId}${LEVEL_PATH}").toString()).ordinal + 1
            val exp = config.get("${player.uniqueId}${EXPERIENCE_PATH}") as Int / BurbLevel.valueOf(config.get("${player.uniqueId}${LEVEL_PATH}").toString()).requiredXp.toFloat()
            player.level = level
            player.exp = if(exp > 1f) 1f else exp
        } else {
            config.set("${player.uniqueId}${LEVEL_PATH}", BurbLevel.LEVEL_40.name)
            config.set("${player.uniqueId}${EXPERIENCE_PATH}", 0)
            config.save(File(plugin.dataFolder,"${player.uniqueId}.yml"))
            player.level = 40
            player.exp = 0f
        }
    }

    fun setLevel(player: Player, newLevel: BurbLevel) {
        val config = getPlayerConfiguration(player)
        config.set("${player.uniqueId}${LEVEL_PATH}", newLevel.name)
        config.set("${player.uniqueId}${EXPERIENCE_PATH}", 0)
        config.save(File(plugin.dataFolder, "${player.uniqueId}.yml"))

        val level = BurbLevel.valueOf(config.get("${player.uniqueId}${LEVEL_PATH}").toString()).ordinal + 1
        val exp = config.get("${player.uniqueId}${EXPERIENCE_PATH}") as Int / BurbLevel.valueOf(config.get("${player.uniqueId}${LEVEL_PATH}").toString()).requiredXp.toFloat()
        player.level = level
        player.exp = if(exp > 1f) 1f else exp
    }
}