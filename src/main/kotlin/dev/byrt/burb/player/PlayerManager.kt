package dev.byrt.burb.player

import dev.byrt.burb.exception.PlayerManagerException
import dev.byrt.burb.item.ItemManager
import dev.byrt.burb.logger
import dev.byrt.burb.player.progression.BurbPlayerData
import dev.byrt.burb.team.Teams

import org.bukkit.entity.Player
import java.util.UUID

object PlayerManager {
    private val burbPlayers = mutableMapOf<UUID, BurbPlayer>()

    fun registerPlayer(player: Player) {
        logger.info("Player Manager: Registering player ${player.name} as BurbPlayer.")
        val burbPlayer =
            BurbPlayer(player.uniqueId, player.name, PlayerType.INVALID, BurbCharacter.NULL, isDead = false)
        burbPlayers[player.uniqueId] = burbPlayer
        BurbPlayerData.getPlayerData(player)
        ItemManager.clearItems(player)
        PlayerVisuals.showPlayer(player)
    }

    fun unregisterPlayer(burbPlayer: BurbPlayer) {
        burbPlayer.setType(PlayerType.INVALID)
        burbPlayer.setCharacter(BurbCharacter.NULL)
        burbPlayers.remove(burbPlayer.uuid)
    }

    fun UUID.burbPlayer(): BurbPlayer = burbPlayers[this] ?: throw PlayerManagerException("Unable to find BurbPlayer.")
    fun Player.burbPlayer(): BurbPlayer = uniqueId.burbPlayer()
}