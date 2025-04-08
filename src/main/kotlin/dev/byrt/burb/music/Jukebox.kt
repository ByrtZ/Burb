package dev.byrt.burb.music

import dev.byrt.burb.game.CapturePointManager
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.plugin

import net.kyori.adventure.sound.Sound

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable

import java.util.*

object Jukebox {
    private val jukeboxMap = mutableMapOf<UUID, BukkitRunnable>()
    private var musicStress = MusicStress.NULL
    fun startMusicLoop(player : Player, plugin : Plugin, music : Music) {
        if(jukeboxMap.containsKey(player.uniqueId)) {
            for(tracks in Music.entries) {
                stopMusicLoop(player, tracks)
            }
        }
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
        bukkitRunnable.runTaskTimer(plugin, 0L, 20L)
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
        if(!CapturePointManager.isSuburbinating()) {
            when(this.musicStress) {
                MusicStress.LOW -> {
                    for(player in Bukkit.getOnlinePlayers()) {
                        startMusicLoop(player, plugin, Music.RANDOM_LOW)
                    }
                }
                MusicStress.MEDIUM -> {
                    for(player in Bukkit.getOnlinePlayers()) {
                        startMusicLoop(player, plugin, Music.RANDOM_MEDIUM)
                    }
                }
                MusicStress.HIGH -> {
                    for(player in Bukkit.getOnlinePlayers()) {
                        startMusicLoop(player, plugin, Music.RANDOM_HIGH)
                    }
                } else -> {}
            }
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
    SUBURBINATION_PLANTS(Sounds.Music.SUBURBINATION_PLANTS, 58),
    SUBURBINATION_ZOMBIES(Sounds.Music.SUBURBINATION_ZOMBIES, 58),
    RANDOM_LOW(Sounds.Music.RANDOM_LOW, 60),
    RANDOM_MEDIUM(Sounds.Music.RANDOM_MEDIUM, 60),
    RANDOM_HIGH(Sounds.Music.RANDOM_HIGH, 60),
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