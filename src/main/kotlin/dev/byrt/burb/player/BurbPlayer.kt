package dev.byrt.burb.player

import dev.byrt.burb.item.ItemManager
import dev.byrt.burb.logger
import dev.byrt.burb.team.TeamManager
import dev.byrt.burb.team.Teams

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

import java.util.UUID

class BurbPlayer(val uuid: UUID, val playerName: String, var playerType: PlayerType, var playerTeam: Teams, var playerCharacter: BurbCharacter, var isDead: Boolean) {
    init {
        setTeam(Teams.SPECTATOR)
        setCharacter(BurbCharacter.NULL)
        logger.info("Player Manager: Registered player ${this.playerName} as BurbPlayer.")
    }

    fun setType(newType: PlayerType) {
        if(newType == this.playerType) return
        this.playerType = newType
        logger.info("Type: ${this.playerName} now has value ${this.playerType}.")
    }

    fun setTeam(newTeam: Teams) {
        if(newTeam == this.playerTeam) return
        this.playerTeam = newTeam
        TeamManager.setTeam(this, this.playerTeam)
        logger.info("Teams: ${this.playerName} now has value ${this.playerTeam}.")
    }

    fun setCharacter(newCharacter: BurbCharacter) {
        if(newCharacter == this.playerCharacter) return
        this.playerCharacter = newCharacter
        if(this.playerCharacter != BurbCharacter.NULL && this.getBukkitPlayer().vehicle == null) {
            ItemManager.giveCharacterItems(this.getBukkitPlayer())
        } else {
            ItemManager.clearItems(this.getBukkitPlayer())
        }
        this.getBukkitPlayer().activePotionEffects.forEach { e -> if(e.type !in listOf(PotionEffectType.HUNGER, PotionEffectType.INVISIBILITY)) this.getBukkitPlayer().removePotionEffect(e.type)}
        if(this.playerCharacter == BurbCharacter.ZOMBIES_HEAVY) {
            this.getBukkitPlayer().addPotionEffect(PotionEffect(PotionEffectType.JUMP_BOOST, PotionEffect.INFINITE_DURATION, 3, false, false))
        }
        logger.info("Character: ${this.playerName} now has value ${this.playerCharacter}.")
    }

    fun setIsDead(newIsDead: Boolean) {
        if(newIsDead == isDead) return
        this.isDead = newIsDead
        logger.info("Dead State: ${this.playerName} now has value ${this.isDead}")
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
