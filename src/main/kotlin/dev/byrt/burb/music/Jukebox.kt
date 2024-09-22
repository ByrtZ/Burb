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
    OVERTIME(Sounds.Music.OVERTIME_MUSIC, 23),
    LOBBY_WAITING(Sounds.Music.LOBBY_WAITING, 59),
    NULL(Sounds.Music.NULL, 0)
}