package dev.byrt.burb.music

import dev.byrt.burb.library.Sounds

import net.kyori.adventure.sound.Sound

import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable

import java.util.*

object Jukebox {
    private val jukeboxMap = mutableMapOf<UUID, BukkitRunnable>()
    fun startMusicLoop(player : Player, plugin : Plugin, music : Music) {
        if(jukeboxMap.containsKey(player.uniqueId)) {
            for(tracks in Music.entries) {
                if(tracks != music) stopMusicLoop(player, tracks)
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

    fun getJukeboxMap(): Map<UUID, BukkitRunnable> {
        return this.jukeboxMap
    }
}

enum class Music(val track: Sound, val trackLengthSecs: Int) {
    MAIN(Sounds.Music.GAME_MUSIC, 30),
    OVERTIME(Sounds.Music.OVERTIME_MUSIC, 60),
    SUBURBINATION_PLANTS(Sounds.Music.SUBURBINATION_PLANTS, 58),
    SUBURBINATION_ZOMBIES(Sounds.Music.SUBURBINATION_ZOMBIES, 58),
    LOBBY_WAITING(Sounds.Music.LOBBY_WAITING, 59),
    DOWNTIME_LOOP(Sounds.Music.DOWNTIME_LOOP, 191),
    DOWNTIME_SUSPENSE(Sounds.Music.DOWNTIME_SUSPENSE, 219),
    NULL(Sounds.Music.NULL, Int.MAX_VALUE)
}