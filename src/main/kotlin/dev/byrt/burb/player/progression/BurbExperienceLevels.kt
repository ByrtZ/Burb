package dev.byrt.burb.player.progression

import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.game.Timer
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.music.Jukebox
import dev.byrt.burb.music.Music
import dev.byrt.burb.player.data.BurbPlayerData
import dev.byrt.burb.plugin
import dev.byrt.burb.team.BurbTeam
import dev.byrt.burb.text.Formatting
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.io.File
import java.time.Duration

object BurbExperienceLevels {
    fun setLevel(player: Player, newLevel: BurbLevel) {
        val config = BurbPlayerData.getPlayerConfiguration(player)
        config.set("${player.uniqueId}${BurbPlayerData.LEVEL_PATH}", newLevel.name)
        config.set("${player.uniqueId}${BurbPlayerData.EXPERIENCE_PATH}", 0)
        config.save(File(plugin.dataFolder, "${player.uniqueId}.yml"))

        val level = BurbLevel.valueOf(config.get("${player.uniqueId}${BurbPlayerData.LEVEL_PATH}").toString()).ordinal + 1
        val exp = config.get("${player.uniqueId}${BurbPlayerData.EXPERIENCE_PATH}") as Int / BurbLevel.valueOf(config.get("${player.uniqueId}${BurbPlayerData.LEVEL_PATH}").toString()).requiredXp.toFloat()
        player.level = level
        player.exp = if(exp > 1f) 1f else exp
    }

    fun appendExperience(player: Player, experienceGained: Int) {
        player.sendActionBar(Formatting.allTags.deserialize("<font:burb:font>+${experienceGained} <yellow>XP"))
        val config = BurbPlayerData.getPlayerConfiguration(player)
        val currentExperience = config.get("${player.uniqueId}${BurbPlayerData.EXPERIENCE_PATH}") as Int
        val currentLevel = BurbLevel.valueOf(config.get("${player.uniqueId}${BurbPlayerData.LEVEL_PATH}").toString())

        if(currentLevel != BurbLevel.LEVEL_100) {
            if(currentExperience + experienceGained >= currentLevel.requiredXp) {
                if(currentExperience + experienceGained > currentLevel.requiredXp) {
                    val overflowXp = (currentExperience + experienceGained) - currentLevel.requiredXp
                    config.set("${player.uniqueId}${BurbPlayerData.EXPERIENCE_PATH}", overflowXp)
                } else {
                    config.set("${player.uniqueId}${BurbPlayerData.EXPERIENCE_PATH}", 0)
                }
                // Level up
                val newEvolution = BurbLevel.entries[currentLevel.ordinal + 1].levelName.endsWith("0")

                val prefix = Component.translatable("burb.level_up.prefix", Component.text(BurbLevel.entries[currentLevel.ordinal + 1].levelColourTag))

                val newLevelText = Component.translatable("burb.level_up.new_level_text",
                    Component.text(
                        BurbLevel.entries[currentLevel.ordinal + 1].levelName,
                        GameManager.teams.getTeam(player.uniqueId)?.textColour ?: NamedTextColor.WHITE,
                    )
                )

                player.showTitle(
                    Title.title(
                        prefix,
                        newLevelText,
                        Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(2), Duration.ofMillis(500))
                    )
                )
                player.sendMessage(Component.text().append(prefix).appendSpace().append(newLevelText).build())
                Jukebox.disconnect(player)
                if(newEvolution) {
                    player.playSound(Sounds.Misc.ODE_TO_JOY)
                } else {
                    player.playSound(when(GameManager.teams.getTeam(player.uniqueId)) {
                        BurbTeam.PLANTS -> Sounds.Misc.LEVEL_UP_PLANTS
                        BurbTeam.ZOMBIES -> Sounds.Misc.LEVEL_UP_ZOMBIES
                        else -> Sounds.Misc.SUCCESS
                    })
                }
                object : BukkitRunnable() {
                    override fun run() {
                        when(GameManager.getGameState()) {
                            GameState.IDLE -> {
                                Jukebox.startMusicLoop(player, Music.LOBBY_WAITING)
                            }
                            GameState.IN_GAME -> {
                                if(Timer.getTimer() <= 120) {
                                    Jukebox.startMusicLoop(player, Music.OVERTIME)
                                } else {
                                    Jukebox.playCurrentMusic(player)
                                }
                            }
                            GameState.GAME_END -> {
                                Jukebox.startMusicLoop(player, Music.POST_GAME)
                            }
                            else -> {}
                        }
                    }
                }.runTaskLater(plugin, if(newEvolution) 14 * 20L else 6 * 20L)
                config.set("${player.uniqueId}${BurbPlayerData.LEVEL_PATH}", BurbLevel.entries[currentLevel.ordinal + 1].name)
            } else {
                config.set("${player.uniqueId}${BurbPlayerData.EXPERIENCE_PATH}", currentExperience + experienceGained)
            }
            config.save(File(plugin.dataFolder,"${player.uniqueId}.yml"))

            // Set visuals for level and experience
            val level = BurbLevel.valueOf(config.get("${player.uniqueId}${BurbPlayerData.LEVEL_PATH}").toString()).ordinal + 1
            val exp = config.get("${player.uniqueId}${BurbPlayerData.EXPERIENCE_PATH}") as Int / BurbLevel.valueOf(config.get("${player.uniqueId}${BurbPlayerData.LEVEL_PATH}").toString()).requiredXp.toFloat()
            player.level = level
            player.exp = if(exp > 1f) 1f else exp
        } else {
            config.set("${player.uniqueId}${BurbPlayerData.LEVEL_PATH}", BurbLevel.LEVEL_100.name)
            config.set("${player.uniqueId}${BurbPlayerData.EXPERIENCE_PATH}", 0)
            config.save(File(plugin.dataFolder,"${player.uniqueId}.yml"))
            player.level = 100
            player.exp = 0f
        }
    }
}

