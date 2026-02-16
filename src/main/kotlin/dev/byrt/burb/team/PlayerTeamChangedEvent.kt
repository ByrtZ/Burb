package dev.byrt.burb.team

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Emitted when a player's team has changed.
 */
data class PlayerTeamChangedEvent(
    val player: Player,
    val newTeam: GameTeam?
) : Event() {

    public companion object {
        @JvmStatic val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList = handlerList
}

