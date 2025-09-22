package dev.byrt.burb.player

import dev.byrt.burb.exception.PlayerManagerException
import dev.byrt.burb.item.ItemManager
import dev.byrt.burb.logger
import dev.byrt.burb.player.progression.BurbProgression
import dev.byrt.burb.team.Teams
import dev.byrt.burb.util.ResourcePacker

import org.bukkit.entity.Player

object PlayerManager {
    private val burbPlayers = mutableSetOf<BurbPlayer>()
    fun registerPlayer(player: Player) {
        logger.info("Player Manager: Registering player ${player.name} as BurbPlayer.")
        val burbPlayer = BurbPlayer(player.uniqueId, player.name, PlayerType.INVALID, Teams.NULL, BurbCharacter.NULL)
        burbPlayers.add(burbPlayer)
        BurbProgression.getPlayerData(player)
        ResourcePacker.applyPackPlayer(player)
        ItemManager.clearItems(player)
        PlayerVisuals.showPlayer(player)
    }

    fun unregisterPlayer(burbPlayer: BurbPlayer) {
        burbPlayer.setTeam(Teams.NULL)
        burbPlayer.setType(PlayerType.INVALID)
        burbPlayer.setCharacter(BurbCharacter.NULL)
        burbPlayers.remove(burbPlayer)
    }

    fun getBurbPlayers(): Set<BurbPlayer> {
        return this.burbPlayers
    }

    fun Player.burbPlayer(): BurbPlayer {
        val burbPlayer = burbPlayers.find { it.uuid == player!!.uniqueId}
        if(burbPlayer == null) throw PlayerManagerException("Unable to find BurbPlayer.")
        return burbPlayer
    }
}