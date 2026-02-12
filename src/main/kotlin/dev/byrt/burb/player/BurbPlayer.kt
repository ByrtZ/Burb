package dev.byrt.burb.player

import dev.byrt.burb.game.GameManager
import dev.byrt.burb.item.ItemManager
import dev.byrt.burb.logger
import dev.byrt.burb.player.character.BurbCharacter
import dev.byrt.burb.team.TeamManager
import dev.byrt.burb.team.BurbTeam

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

import java.util.UUID

class BurbPlayer(val uuid: UUID, val playerName: String, var playerType: PlayerType, var playerCharacter: BurbCharacter, var isDead: Boolean) {
    init {
        setCharacter(BurbCharacter.NULL)
        logger.info("Player Manager: Registered player ${this.playerName} as BurbPlayer.")
    }

    fun setType(newType: PlayerType) {
        if(newType == this.playerType) return
        this.playerType = newType
        logger.info("Type: ${this.playerName} now has value ${this.playerType}.")
    }

    @Deprecated("Prefer checking against GameManager.teams directly")
    val playerTeam: BurbTeam?
        get() = GameManager.teams.getTeam(uuid)

    fun setCharacter(newCharacter: BurbCharacter) {
        if(newCharacter == this.playerCharacter) return
        this.playerCharacter = newCharacter
        if(this.playerCharacter != BurbCharacter.NULL && this.bukkitPlayer().vehicle == null) {
            ItemManager.giveCharacterItems(this.bukkitPlayer())
        } else {
            ItemManager.clearItems(this.bukkitPlayer())
        }
        this.bukkitPlayer().activePotionEffects.forEach { e -> if(e.type !in listOf(PotionEffectType.HUNGER, PotionEffectType.INVISIBILITY)) this.bukkitPlayer().removePotionEffect(e.type)}
        if(this.playerCharacter == BurbCharacter.ZOMBIES_HEAVY) {
            this.bukkitPlayer().addPotionEffect(PotionEffect(PotionEffectType.JUMP_BOOST, PotionEffect.INFINITE_DURATION, 3, false, false))
        }
        logger.info("Character: ${this.playerName} now has value ${this.playerCharacter}.")
    }

    fun setIsDead(newIsDead: Boolean) {
        if(newIsDead == isDead) return
        this.isDead = newIsDead
        logger.info("Dead State: ${this.playerName} now has value ${this.isDead}")
    }

    fun bukkitPlayer(): Player {
        return Bukkit.getPlayer(this.uuid)!!
    }
}

enum class PlayerType {
    SPECTATOR,
    PARTICIPANT,
    INVALID
}
