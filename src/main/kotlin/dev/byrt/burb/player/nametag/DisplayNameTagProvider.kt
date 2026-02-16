package dev.byrt.burb.player.nametag

import dev.byrt.burb.player.displayname.DisplayNameChangeEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler

/**
 * Sets the player's nametag from their display name.
 * @see dev.byrt.burb.player.displayname.PlayerNameFormatter
 */
class DisplayNameTagProvider : NameTagProvider() {
    override val lines = 1
    override fun setUpForPlayer(player: Player, nametag: NameTag) {
        nametag[0] = player.displayName()
    }

    @EventHandler
    private fun onDisplayNameChange(e: DisplayNameChangeEvent) {
        val nametag = nametags[e.player.uniqueId] ?: return
        nametag[0] = e.player.displayName()
    }
}