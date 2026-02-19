package dev.byrt.burb.player.progression

import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

/**
 * Emitted when a player levels up.
 */
class PlayerLevelUpEvent(player: Player, val newLevel: BurbLevel) : PlayerEvent(player) {
    public companion object {
        @JvmStatic val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList = handlerList
}