package dev.byrt.burb.event

import dev.byrt.burb.text.Formatting
import dev.byrt.burb.player.PlayerManager

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoin: Listener {
    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        e.joinMessage(Formatting.allTags.deserialize("${if(e.player.isOp) "<dark_red>" else "<speccolour>"}${e.player.name}<reset> joined the game."))
        PlayerManager.registerPlayer(e.player)
    }
}