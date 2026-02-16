package dev.byrt.burb.player.displayname

import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

/**
 * Emitted when a player's display name changes.
 */
class DisplayNameChangeEvent(player: Player) : PlayerEvent(player) {

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList = handlerList
}