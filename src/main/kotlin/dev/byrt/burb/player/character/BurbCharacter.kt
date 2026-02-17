package dev.byrt.burb.player.character

import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.interfaces.BurbInterface
import dev.byrt.burb.interfaces.BurbInterfaceType
import dev.byrt.burb.item.ServerItem
import dev.byrt.burb.item.ability.BurbCharacterAbilities
import dev.byrt.burb.item.weapon.BurbMainWeapon
import dev.byrt.burb.player.BurbPlayer
import dev.byrt.burb.plugin
import dev.byrt.burb.team.BurbTeam
import org.bukkit.scheduler.BukkitRunnable

enum class BurbCharacter(val characterName: String, val characterType: BurbCharacterType, val characterMainWeapon: BurbMainWeapon, val characterAbilities: BurbCharacterAbilities) {
    NULL("null", BurbCharacterType.NULL, BurbMainWeapon.NULL, BurbCharacterAbilities.NULL),
    PLANTS_SCOUT("Peashooter", BurbCharacterType.RANGED, BurbMainWeapon.PLANTS_SCOUT_MAIN, BurbCharacterAbilities.PLANTS_SCOUT_ABILITIES),
    PLANTS_HEAVY("Chomper", BurbCharacterType.MELEE, BurbMainWeapon.PLANTS_HEAVY_MAIN, BurbCharacterAbilities.PLANTS_HEAVY_ABILITIES),
    PLANTS_HEALER("Sunflower", BurbCharacterType.RANGED, BurbMainWeapon.PLANTS_HEALER_MAIN, BurbCharacterAbilities.PLANTS_HEALER_ABILITIES),
    PLANTS_RANGED("Cactus", BurbCharacterType.RANGED, BurbMainWeapon.PLANTS_RANGED_MAIN, BurbCharacterAbilities.PLANTS_RANGED_ABILITIES),
    ZOMBIES_SCOUT("Foot Soldier", BurbCharacterType.RANGED, BurbMainWeapon.ZOMBIES_SCOUT_MAIN, BurbCharacterAbilities.ZOMBIES_SCOUT_ABILITIES),
    ZOMBIES_HEAVY("Super Brainz", BurbCharacterType.MELEE, BurbMainWeapon.ZOMBIES_HEAVY_MAIN, BurbCharacterAbilities.ZOMBIES_HEAVY_ABILITIES),
    ZOMBIES_HEALER("Scientist", BurbCharacterType.RANGED, BurbMainWeapon.ZOMBIES_HEALER_MAIN, BurbCharacterAbilities.ZOMBIES_HEALER_ABILITIES),
    ZOMBIES_RANGED("Deadbeard", BurbCharacterType.RANGED, BurbMainWeapon.ZOMBIES_RANGED_MAIN, BurbCharacterAbilities.ZOMBIES_RANGED_ABILITIES)
}

fun BurbPlayer.setRandomCharacter() {
    when(GameManager.teams.getTeam(this.uuid)) {
        BurbTeam.PLANTS -> {
            val charactersList = mutableListOf(BurbCharacter.PLANTS_SCOUT, BurbCharacter.PLANTS_HEAVY, BurbCharacter.PLANTS_HEALER, BurbCharacter.PLANTS_RANGED)
            charactersList.remove(this.playerCharacter)
            this.setCharacter(charactersList.random())
        }
        BurbTeam.ZOMBIES -> {
            val charactersList = mutableListOf(BurbCharacter.ZOMBIES_SCOUT, BurbCharacter.ZOMBIES_HEAVY, BurbCharacter.ZOMBIES_HEALER, BurbCharacter.ZOMBIES_RANGED)
            charactersList.remove(this.playerCharacter)
            this.setCharacter(charactersList.random())
        }
        else -> {}
    }
}

/** Resets the character for non-participants and opens character select interface for participants. **/
fun BurbPlayer.characterSelect() {
    setCharacter(BurbCharacter.NULL)
    object : BukkitRunnable() {
        override fun run() {
            if(GameManager.teams.isParticipating(uuid)) {
                BurbInterface(bukkitPlayer(), BurbInterfaceType.CHARACTER_SELECT)
            }
            if(GameManager.getGameState() == GameState.IDLE) {
                bukkitPlayer().inventory.setItem(0, ServerItem.getProfileItem())
            } else {
                bukkitPlayer().inventory.remove(ServerItem.getProfileItem())
            }
        }
    }.runTaskLater(plugin, 2 * 20L)
}

fun String.getCharacter(): BurbCharacter {
    for(character in BurbCharacter.entries) {
        if(character.characterName == this) {
            return character
        }
    }
    return BurbCharacter.NULL
}