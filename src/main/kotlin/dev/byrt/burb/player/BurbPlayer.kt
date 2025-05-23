package dev.byrt.burb.player

import dev.byrt.burb.chat.ChatUtility
import dev.byrt.burb.item.ItemManager
import dev.byrt.burb.team.TeamManager
import dev.byrt.burb.team.Teams

import org.bukkit.Bukkit
import org.bukkit.entity.Player

import java.util.UUID

class BurbPlayer(val uuid: UUID, val playerName: String, var playerType: PlayerType, var playerTeam: Teams, var playerCharacter: BurbCharacter) {
    init {
        setTeam(Teams.SPECTATOR)
        setCharacter(BurbCharacter.NULL)
        ChatUtility.broadcastDev("<dark_gray>Player Manager: Registered player ${this.playerName} as BurbPlayer.", true)
    }

    fun setType(newType: PlayerType) {
        if(newType == this.playerType) return
        this.playerType = newType
        ChatUtility.broadcastDev("<dark_gray>Type: ${this.playerName} now has value ${this.playerType}.", true)
    }

    fun setTeam(newTeam: Teams) {
        if(newTeam == this.playerTeam) return
        this.playerTeam = newTeam
        TeamManager.setTeam(this, this.playerTeam)
        ChatUtility.broadcastDev("<dark_gray>Teams: ${this.playerName} now has value ${this.playerTeam}.", true)
    }

    fun setCharacter(newCharacter: BurbCharacter) {
        if(newCharacter == this.playerCharacter) return
        this.playerCharacter = newCharacter
        if(this.playerCharacter != BurbCharacter.NULL && this.getBukkitPlayer().vehicle == null) {
            ItemManager.giveCharacterItems(this.getBukkitPlayer())
        } else {
            ItemManager.clearItems(this.getBukkitPlayer())
        }
        ChatUtility.broadcastDev("<dark_gray>Character: ${this.playerName} now has value ${this.playerCharacter}.", true)
    }

    fun getBukkitPlayer(): Player {
        return Bukkit.getPlayer(this.uuid)!!
    }
}

enum class PlayerType {
    SPECTATOR,
    PARTICIPANT,
    INVALID
}
