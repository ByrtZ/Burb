package dev.byrt.burb.lobby

import dev.byrt.burb.text.Formatting
import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.library.Translation
import dev.byrt.burb.lobby.tutorial.BurbTutorialBoard
import dev.byrt.burb.music.Jukebox
import dev.byrt.burb.music.Music
import dev.byrt.burb.player.PlayerVisuals
import dev.byrt.burb.plugin

import net.kyori.adventure.translation.GlobalTranslator

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Display
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable

import java.util.Locale

object BurbLobby {
    fun playerJoinTitleScreen(player: Player) {
        player.gameMode = GameMode.ADVENTURE
        player.addPotionEffect(PotionEffect(PotionEffectType.HUNGER, PotionEffect.INFINITE_DURATION, 0, false, false))
        player.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 0, false, false))
        PlayerVisuals.hidePlayer(player)

        val titleVehicle = player.world.spawn(Location(player.world, 983.5, 6.0, 992.5, -63f, 10f), ItemDisplay::class.java)
        titleVehicle.addScoreboardTag("${player.uniqueId}-title-vehicle")
        player.teleport(titleVehicle)
        titleVehicle.addPassenger(player)
        player.playSound(Sounds.Misc.TITLE_SCREEN_ENTER)

        object : BukkitRunnable() {
            override fun run() {
                Jukebox.startMusicLoop(player, Music.LOBBY_TITLE_SCREEN)
            }
        }.runTaskLater(plugin, 20L)

        object : BukkitRunnable() {
            var i = 0
            override fun run() {
                if(player.vehicle == titleVehicle) {
                    player.sendActionBar(Formatting.allTags.deserialize(if(i <= 10) Translation.Generic.TITLE_SCREEN_ACTIONBAR.replace("<white>", "<yellow>") else Translation.Generic.TITLE_SCREEN_ACTIONBAR))
                    i++
                    if(i >= 20) i = 0
                } else {
                    player.sendActionBar(Formatting.allTags.deserialize(""))
                    cancel()
                }
            }
        }.runTaskTimer(plugin, 0L, 5L)
        player.removePotionEffect(PotionEffectType.BLINDNESS)
    }

    fun playerJoinHub(player: Player) {
        object : BukkitRunnable() {
            override fun run() {
                PlayerVisuals.respawn(player)
                object : BukkitRunnable() {
                    override fun run() {
                        PlayerVisuals.postRespawn(player, player.vehicle as ItemDisplay)
                        player.removePotionEffect(PotionEffectType.INVISIBILITY)
                        Jukebox.disconnect(player)
                        if(GameManager.getGameState() == GameState.IDLE) {
                            object : BukkitRunnable() {
                                override fun run() {
                                    player.playSound(Sounds.Music.LOBBY_INTRO)
                                    object : BukkitRunnable() {
                                        override fun run() {
                                            if(GameManager.getGameState() == GameState.IDLE) Jukebox.startMusicLoop(player, Music.LOBBY_WAITING)
                                        }
                                    }.runTaskLater(plugin, 1240L)
                                }
                            }.runTaskLater(plugin, 20L)
                        } else {
                            if(GameManager.getGameState() == GameState.IN_GAME) Jukebox.playCurrentMusic(player)
                            if(GameManager.getGameState() == GameState.GAME_END) Jukebox.startMusicLoop(player, Music.POST_GAME)
                            if(!GameManager.teams.isParticipating(player.uniqueId)) {
                                player.gameMode = GameMode.SPECTATOR
                            }
                        }
                    }
                }.runTaskLater(plugin, 20L)
            }
        }.runTaskLater(plugin, 2 * 20L)
    }

    private val tutorialBoards = mutableMapOf<TextDisplay, Int?>()
    fun createTutorialBoards() {
        for(board in BurbTutorialBoard.entries) {
            val display = board.boardLocation.world.spawn(board.boardLocation, TextDisplay::class.java).apply {
                text(GlobalTranslator.renderer().render(board.boardText, Locale.ENGLISH))
                billboard = Display.Billboard.VERTICAL
                isShadowed = true
                addScoreboardTag("burb.tutorial.text_display")
            }
            if(board.otherTexts.isNotEmpty()) {
                runTutorialBoardRotation(board, display)
            } else {
                tutorialBoards[display] = null
            }
        }
    }

    fun runTutorialBoardRotation(board: BurbTutorialBoard, display: TextDisplay) {
        val texts = listOf(board.boardText) + board.otherTexts
        var rotation = 0
        val maxRotation = texts.size - 1
        val boardRunnable = object : BukkitRunnable() {
            override fun run() {
                if(rotation > maxRotation) rotation = 0
                display.apply {
                    text(GlobalTranslator.renderer().render(texts[rotation], Locale.ENGLISH))
                }
                rotation += 1
            }
        }.runTaskTimer(plugin, 0L, 15 * 20L)
        tutorialBoards[display] = boardRunnable.taskId
    }

    fun destroyTutorialBoards() {
        // Catch any stray boards that might not have been cleaned up
        for(world in Bukkit.getWorlds()) {
            for(display in world.getEntitiesByClass(TextDisplay::class.java)) {
                if(display.scoreboardTags.contains("burb.tutorial.text_display")) display.remove()
            }
        }
        // Remove registered boards
        tutorialBoards.forEach {
            Bukkit.getScheduler().cancelTask(it.value ?: return@forEach)
            it.key.remove()
        }
        tutorialBoards.clear()
    }
}