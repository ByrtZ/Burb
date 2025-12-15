package dev.byrt.burb.music

import dev.byrt.burb.game.objective.CapturePoints
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.plugin

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
        }
    }

    fun playCurrentMusicStress(player: Player) {
        for(music in Music.entries) {
            stopMusicLoop(player, music)
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

    fun getJukeboxMap(): Map<UUID, BukkitRunnable> {
        return this.jukeboxMap
    }
}

enum class Music(val track: Sound, val trackLengthSecs: Int) {
    LOBBY_TITLE_SCREEN(Sounds.Music.LOBBY_TITLE_SCREEN, 139),
    LOBBY_WAITING(Sounds.Music.LOBBY_WAITING, 59),
    LOBBY_UNDERWORLD(Sounds.Music.LOBBY_UNDERWORLD, 66),
    LOBBY_UNDERWORLD_CHALLENGE_LOW(Sounds.Music.LOBBY_UNDERWORLD_CHALLENGE_LOW, 8),
    LOBBY_UNDERWORLD_CHALLENGE_MEDIUM(Sounds.Music.LOBBY_UNDERWORLD_CHALLENGE_MEDIUM, 14),
    LOBBY_UNDERWORLD_CHALLENGE_HIGH(Sounds.Music.LOBBY_UNDERWORLD_CHALLENGE_HIGH, 14),
    LOBBY_UNDERWORLD_CHALLENGE_INTENSE(Sounds.Music.LOBBY_UNDERWORLD_CHALLENGE_INTENSE, 19),
    LOADING_MELODY(Sounds.Music.LOADING_MELODY, 148),
    SUBURBINATION_PLANTS(Sounds.Music.SUBURBINATION_PLANTS, 58),
    SUBURBINATION_ZOMBIES(Sounds.Music.SUBURBINATION_ZOMBIES, 58),
    RANDOM_LOW(Sounds.Music.RANDOM_LOW, 60),
    RANDOM_MEDIUM(Sounds.Music.RANDOM_MEDIUM, 60),
    RANDOM_HIGH(Sounds.Music.RANDOM_HIGH, 60),
    TREASURE_TIME_LOW(Sounds.Music.TREASURE_TIME_LOW, 70),
    TREASURE_TIME_HIGH(Sounds.Music.TREASURE_TIME_HIGH, 70),
    OVERTIME(Sounds.Music.OVERTIME_MUSIC, 60),
    POST_GAME(Sounds.Music.POST_GAME_MUSIC, 80),
    DOWNTIME_LOOP(Sounds.Music.DOWNTIME_LOOP, 191),
    DOWNTIME_SUSPENSE(Sounds.Music.DOWNTIME_SUSPENSE, 219),
    NULL(Sounds.Music.NULL, Int.MAX_VALUE)
}

enum class MusicStress {
    NULL,
    LOW,
    MEDIUM,
    HIGH
}