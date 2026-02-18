package dev.byrt.burb.music

import dev.byrt.burb.game.events.SpecialEvent
import dev.byrt.burb.game.events.SpecialEvents
import dev.byrt.burb.game.objective.CapturePoints
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.plugin
import dev.byrt.burb.team.BurbTeam

import net.kyori.adventure.sound.Sound

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

import java.util.*

object Jukebox {
    private val jukeboxMap = mutableMapOf<UUID, BukkitRunnable>()
    private var musicStress = MusicStress.NULL
    fun startMusicLoop(player: Player, music: Music) {
        if(jukeboxMap.containsKey(player.uniqueId)) {
            for(tracks in Music.entries) {
                stopMusicLoop(player, tracks)
            }
        }
        when(music) {
            Music.TREASURE_TIME_LOW -> {
                player.playSound(Sounds.Music.TREASURE_TIME_INTRO)
                runMusicTask(player, music, 25L)
            }
            else -> runMusicTask(player, music)
        }
    }

    private fun runMusicTask(player: Player, music: Music, delay: Long = 0L) {
        val bukkitRunnable = object: BukkitRunnable() {
            var musicTimer = 0
            override fun run() {
                if(musicTimer == 0) {
                    player.playSound(music.track, Sound.Emitter.self())
                }
                if(musicTimer == music.trackLengthSecs) {
                    musicTimer = -1
                }
                musicTimer++
            }
        }
        bukkitRunnable.runTaskTimer(plugin, delay, 20L)
        jukeboxMap[player.uniqueId] = bukkitRunnable
    }

    fun stopMusicLoop(player : Player, music : Music) {
        player.stopSound(music.track)
        jukeboxMap.remove(player.uniqueId)?.cancel()
    }

    fun disconnect(player: Player) {
        for(music in Music.entries) {
            stopMusicLoop(player, music)
        }
    }

    fun setMusicStress(newStress: MusicStress) {
        if(newStress == this.musicStress) return
        this.musicStress = newStress
        if(!CapturePoints.isSuburbinating()) {
            when(this.musicStress) {
                MusicStress.LOW -> {
                    for(player in Bukkit.getOnlinePlayers()) {
                        startMusicLoop(player, Music.RANDOM_LOW)
                    }
                }
                MusicStress.MEDIUM -> {
                    for(player in Bukkit.getOnlinePlayers()) {
                        startMusicLoop(player, Music.RANDOM_MEDIUM)
                    }
                }
                MusicStress.HIGH -> {
                    for(player in Bukkit.getOnlinePlayers()) {
                        startMusicLoop(player, Music.RANDOM_HIGH)
                    }
                } else -> {}
            }
        } else {
            when(CapturePoints.getSuburbinatingTeam()) {
                BurbTeam.PLANTS -> {
                    for(player in Bukkit.getOnlinePlayers()) {
                        startMusicLoop(player, Music.SUBURBINATION_PLANTS)
                    }
                }
                BurbTeam.ZOMBIES -> {
                    for(player in Bukkit.getOnlinePlayers()) {
                        startMusicLoop(player, Music.SUBURBINATION_ZOMBIES)
                    }
                }
                else -> {}
            }
        }
    }

    fun playCurrentMusic(player: Player) {
        for(music in Music.entries) {
            stopMusicLoop(player, music)
        }
        if(SpecialEvents.isEventRunning()) {
            when(SpecialEvents.getCurrentEvent()) {
                SpecialEvent.TREASURE_TIME -> startMusicLoop(player, Music.TREASURE_TIME_LOW)
                SpecialEvent.MOON_GRAVITY -> startMusicLoop(player, Music.LOBBY_UNDERWORLD)
                SpecialEvent.RANDOS_REVENGE -> startMusicLoop(player, Music.RANDOS_REVENGE)
                SpecialEvent.VANQUISH_SHOWDOWN -> startMusicLoop(player, Music.VANQUISH_SHOWDOWN)
                null -> {}
            }
            return
        }
        if(CapturePoints.isSuburbinating()) {
            when(CapturePoints.getSuburbinatingTeam()) {
                BurbTeam.PLANTS -> startMusicLoop(player, Music.SUBURBINATION_PLANTS)
                BurbTeam.ZOMBIES -> startMusicLoop(player, Music.SUBURBINATION_ZOMBIES)
                else -> {}
            }
            return
        }
        when(musicStress) {
            MusicStress.LOW -> startMusicLoop(player, Music.RANDOM_LOW)
            MusicStress.MEDIUM -> startMusicLoop(player, Music.RANDOM_MEDIUM)
            MusicStress.HIGH -> startMusicLoop(player, Music.RANDOM_HIGH)
            else -> {}
        }
    }

    fun getMusicStress(): MusicStress {
        return this.musicStress
    }

    fun resetMusicStress() {
        this.musicStress = MusicStress.NULL
    }
}