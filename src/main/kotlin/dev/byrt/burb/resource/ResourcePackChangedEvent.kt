package dev.byrt.burb.resource

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Emitted when the server's active resource pack has changed.
 */
data class ResourcePackChangedEvent(
    val newPack: LoadedPack?
) : Event() {

    public companion object {
        @JvmStatic val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList = handlerList
}

