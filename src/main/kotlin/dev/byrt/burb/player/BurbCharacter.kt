package dev.byrt.burb.player

import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.interfaces.BurbInterface
import dev.byrt.burb.interfaces.BurbInterfaceType
import dev.byrt.burb.item.ability.BurbCharacterAbilities
import dev.byrt.burb.item.weapon.BurbMainWeapon
import dev.byrt.burb.item.ServerItem
import dev.byrt.burb.plugin
import dev.byrt.burb.team.Teams

import org.bukkit.scheduler.BukkitRunnable

enum class BurbCharacter(val characterName: String, val characterMainWeapon: BurbMainWeapon, val characterAbilities: BurbCharacterAbilities) {
    NULL("null", BurbMainWeapon.NULL, BurbCharacterAbilities.NULL),
    PLANTS_SCOUT("Peashooter", BurbMainWeapon.PLANTS_SCOUT_MAIN, BurbCharacterAbilities.PLANTS_SCOUT_ABILITIES),
    PLANTS_HEAVY("Chomper", BurbMainWeapon.PLANTS_HEAVY_MAIN, BurbCharacterAbilities.PLANTS_HEAVY_ABILITIES),
    PLANTS_HEALER("Sunflower", BurbMainWeapon.PLANTS_HEALER_MAIN, BurbCharacterAbilities.PLANTS_HEALER_ABILITIES),
    PLANTS_RANGED("Cactus", BurbMainWeapon.PLANTS_RANGED_MAIN, BurbCharacterAbilities.PLANTS_RANGED_ABILITIES),
    ZOMBIES_SCOUT("Foot Soldier", BurbMainWeapon.ZOMBIES_SCOUT_MAIN, BurbCharacterAbilities.ZOMBIES_SCOUT_ABILITIES),
    ZOMBIES_HEAVY("Super Brainz", BurbMainWeapon.ZOMBIES_HEAVY_MAIN, BurbCharacterAbilities.ZOMBIES_HEAVY_ABILITIES),
    ZOMBIES_HEALER("Scientist", BurbMainWeapon.ZOMBIES_HEALER_MAIN, BurbCharacterAbilities.ZOMBIES_HEALER_ABILITIES),
    ZOMBIES_RANGED("Deadbeard", BurbMainWeapon.ZOMBIES_RANGED_MAIN, BurbCharacterAbilities.ZOMBIES_RANGED_ABILITIES)
}

fun BurbPlayer.setRandomCharacter() {
    when(this.playerTeam) {
        Teams.PLANTS -> {
            val charactersList = mutableListOf(BurbCharacter.PLANTS_SCOUT, BurbCharacter.PLANTS_HEAVY, BurbCharacter.PLANTS_HEALER, BurbCharacter.PLANTS_RANGED)
            charactersList.remove(this.playerCharacter)
            this.setCharacter(charactersList.random())
        }
        Teams.ZOMBIES -> {
            val charactersList = mutableListOf(BurbCharacter.ZOMBIES_SCOUT, BurbCharacter.ZOMBIES_HEAVY, BurbCharacter.ZOMBIES_HEALER, BurbCharacter.ZOMBIES_RANGED)
            charactersList.remove(this.playerCharacter)
            this.setCharacter(charactersList.random())
        }
        else -> {}
    }
}

fun BurbPlayer.characterSelect() {
    setCharacter(BurbCharacter.NULL)
    object : BukkitRunnable() {
        override fun run() {
            if(playerTeam in listOf(Teams.PLANTS, Teams.ZOMBIES)) {
                BurbInterface(getBukkitPlayer(), BurbInterfaceType.CHARACTER_SELECT)
            }
            if(GameManager.getGameState() == GameState.IDLE) {
                getBukkitPlayer().inventory.setItem(8, ServerItem.getProfileItem())
            } else {
                getBukkitPlayer().inventory.remove(ServerItem.getProfileItem())
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