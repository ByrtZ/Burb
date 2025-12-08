package dev.byrt.burb.command

import dev.byrt.burb.text.ChatUtility
import dev.byrt.burb.music.Jukebox
import dev.byrt.burb.music.Music

import io.papermc.paper.command.brigadier.CommandSourceStack

import org.bukkit.entity.Player

import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.annotations.processing.CommandContainer

@Suppress("unused", "unstableApiUsage")
@CommandContainer
class JukeboxCommands {
    @Command("jukebox start <music>")
    @Permission("burb.cmd.jukebox")
    fun echo(css: CommandSourceStack, music: Music) {
        if(music == Music.NULL) return
        if(css.sender is Player) {
            val player = css.sender as Player
            Jukebox.startMusicLoop(player, music)
            player.sendMessage(ChatUtility.formatMessage("<green>Now playing music track <yellow>$music<green>.", false))
        }
    }

    @Command("jukebox stop")
    @Permission("burb.cmd.jukebox")
    fun echo(css: CommandSourceStack) {
        if(css.sender is Player) {
            val player = css.sender as Player
            for(music in Music.entries) {
                Jukebox.stopMusicLoop(player, music)
            }
            player.sendMessage(ChatUtility.formatMessage("<red>All active music tracks cancelled.", false))
        }
    }
}