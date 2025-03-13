package dev.byrt.burb.player

import dev.byrt.burb.chat.ChatUtility
import dev.byrt.burb.chat.InfoBoardManager
import dev.byrt.burb.exception.PlayerManagerException
import dev.byrt.burb.item.ItemManager
import dev.byrt.burb.team.Teams
import dev.byrt.burb.util.ResourcePacker

import org.bukkit.entity.Player

object PlayerManager {
    private val burbPlayers = mutableSetOf<BurbPlayer>()
    fun registerPlayer(player: Player) {
        ChatUtility.broadcastDev("<dark_gray>Player Manager: Registering player ${player.name} as BurbPlayer.", true)
        val burbPlayer = BurbPlayer(player.uniqueId, player.name, PlayerType.INVALID, Teams.NULL, BurbCharacter.NULL)
        burbPlayers.add(burbPlayer)
        ResourcePacker.applyPackPlayer(player)
        InfoBoardManager.showScoreboard(player)
        ItemManager.clearItems(player)
        PlayerVisuals.showPlayer(player)
    }

    fun unregisterPlayer(burbPlayer: BurbPlayer) {
        burbPlayer.setTeam(Teams.NULL)
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