package dev.byrt.burb.player

import dev.byrt.burb.chat.ChatUtility
import dev.byrt.burb.team.TeamManager
import dev.byrt.burb.team.Teams

import org.bukkit.Bukkit
import org.bukkit.entity.Player

import java.util.UUID

/*, var selectedClass : BurbClass*/
class BurbPlayer(val uuid: UUID, val playerName: String, var playerType: PlayerType, var playerTeam: Teams) {
    init {
        setTeam(Teams.SPECTATOR)
        ChatUtility.broadcastDev("<dark_gray>Player Manager: Registered player ${this.playerName} as BurbPlayer.", false)
    }

    fun setType(newType: PlayerType) {
        if(newType == this.playerType) return
        this.playerType = newType
        ChatUtility.broadcastDev("<dark_gray>Type: ${this.playerName} now has value ${this.playerType}.", false)
    }

    fun setTeam(newTeam: Teams) {
        if(newTeam == this.playerTeam) return
        this.playerTeam = newTeam
        TeamManager.setTeam(this, this.playerTeam)
        ChatUtility.broadcastDev("<dark_gray>Teams: ${this.playerName} now has value ${this.playerTeam}.", false)
    }

    /*fun setClass(newClass: BurbClass) {

    }*/

    fun getBukkitPlayer(): Player {
        return Bukkit.getPlayer(this.uuid)!!
    }
}

enum class PlayerType {
    SPECTATOR,
    PARTICIPANT,
    INVALID
}
