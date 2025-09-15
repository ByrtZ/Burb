package dev.byrt.burb.event

import dev.byrt.burb.text.Formatting
import dev.byrt.burb.music.Jukebox
import dev.byrt.burb.player.PlayerManager
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.player.PlayerVisuals
import dev.byrt.burb.util.Noxesium

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

@Suppress("unused")
class PlayerQuit: Listener {
    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        PlayerManager.unregisterPlayer(e.player.burbPlayer())
        Noxesium.removeNoxesiumUser(e.player)
        Jukebox.disconnect(e.player)
        PlayerVisuals.disconnectInterrupt(e.player)
        e.quitMessage(Formatting.allTags.deserialize("${if(e.player.isOp) "<dark_red>" else "<speccolour>"}${e.player.name}<reset> left the game."))
    }
}