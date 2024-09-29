package dev.byrt.burb.event

import dev.byrt.burb.chat.Formatting
import dev.byrt.burb.chat.InfoBoardManager
import dev.byrt.burb.util.ResourcePacker

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoin: Listener {
    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        e.joinMessage(Formatting.allTags.deserialize("${if(e.player.isOp) "<dark_red>" else "<white>"}${e.player.name}<reset> joined the game."))
        ResourcePacker.applyPackPlayer(e.player)
        InfoBoardManager.showScoreboard(e.player)
    }
}