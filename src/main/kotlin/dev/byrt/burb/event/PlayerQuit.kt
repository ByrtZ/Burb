package dev.byrt.burb.event

import dev.byrt.burb.chat.Formatting
import dev.byrt.burb.music.Jukebox
import dev.byrt.burb.util.Noxesium

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerQuit: Listener {
    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        Noxesium.removeNoxesiumUser(e.player)
        e.quitMessage(Formatting.allTags.deserialize("${if(e.player.isOp) "<dark_red>" else "<white>"}${e.player.name}<reset> left the game."))
        Jukebox.disconnect(e.player)
    }
}