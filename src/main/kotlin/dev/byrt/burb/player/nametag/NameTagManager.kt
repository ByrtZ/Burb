package dev.byrt.burb.player.nametag

import dev.byrt.burb.Main
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList

/**
 * Manages players' above-head nametags.
 */
class NameTagManager(private val plugin: Main) {
    public var provider: NameTagProvider? = null
        set(value) {
            field?.let {
                it.close()
                HandlerList.unregisterAll(it)
            }
            field = value
            value?.let {
                Bukkit.getPluginManager().registerEvents(it, plugin)
                Bukkit.getOnlinePlayers().forEach { player -> it.create(player) }
            }
        }

    init {
        // re-set to run setter
        provider = LobbyNameTagProvider()
    }
}