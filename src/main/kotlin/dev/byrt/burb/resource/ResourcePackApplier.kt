package dev.byrt.burb.resource

import dev.byrt.burb.util.extension.longAt
import net.kyori.adventure.resource.ResourcePackInfo
import net.kyori.adventure.resource.ResourcePackRequest
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.util.UUID

/**
 * Applies resource packs to players.
 */
class ResourcePackApplier(private val loader: ResourcePackLoader) : Listener {

    /**
     * Applies a pack to a player, or clears it if [pack] is null.
     */
    @OptIn(ExperimentalStdlibApi::class)
    private fun applyToPlayer(player: Player, pack: RemotePack?) {
        val request = ResourcePackRequest.resourcePackRequest().apply {
            replace(true)
            required(true)

            if (pack != null) {
                requireNotNull(pack.hash) { "Cannot apply a pack with no hash" }
                packs(
                    ResourcePackInfo.resourcePackInfo(
                        UUID(pack.hash.longAt(0), pack.hash.longAt(8)),
                        pack.url.toURI(),
                        pack.hash.toHexString(),
                    )
                )
            }
        }

        player.sendResourcePacks(request)
    }

    @EventHandler
    private fun onPlayerJoin(e: PlayerJoinEvent) {
        // No need to apply a null pack here as they've just joined.
        applyToPlayer(e.player, loader.currentPack ?: return)
    }
}