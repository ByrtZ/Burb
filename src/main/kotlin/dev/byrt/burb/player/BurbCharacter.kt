package dev.byrt.burb.player

import dev.byrt.burb.item.BurbCharacterAbilities
import dev.byrt.burb.item.BurbCharacterMainWeapon

enum class BurbCharacter(val characterName: String, val characterMainWeapon: BurbCharacterMainWeapon, val characterAbilities: BurbCharacterAbilities) {
    NULL("null", BurbCharacterMainWeapon.NULL, BurbCharacterAbilities.NULL),
    PLANTS_SCOUT("Peashooter", BurbCharacterMainWeapon.PLANTS_SCOUT_MAIN, BurbCharacterAbilities.PLANTS_SCOUT_ABILITIES),
    PLANTS_HEAVY("Chomper", BurbCharacterMainWeapon.PLANTS_HEAVY_MAIN, BurbCharacterAbilities.PLANTS_HEAVY_ABILITIES),
    PLANTS_HEALER("Sunflower",
        BurbCharacterMainWeapon.PLANTS_HEALER_MAIN,
        BurbCharacterAbilities.PLANTS_HEALER_ABILITIES
    ),
    PLANTS_RANGED("Cactus", BurbCharacterMainWeapon.PLANTS_RANGED_MAIN, BurbCharacterAbilities.PLANTS_RANGED_ABILITIES),
    ZOMBIES_SCOUT("Foot Soldier",
        BurbCharacterMainWeapon.ZOMBIES_SCOUT_MAIN,
        BurbCharacterAbilities.ZOMBIES_SCOUT_ABILITIES
    ),
    ZOMBIES_HEAVY("Super Brainz",
        BurbCharacterMainWeapon.ZOMBIES_HEAVY_MAIN,
        BurbCharacterAbilities.ZOMBIES_HEAVY_ABILITIES
    ),
    ZOMBIES_HEALER("Scientist",
        BurbCharacterMainWeapon.ZOMBIES_HEALER_MAIN,
        BurbCharacterAbilities.ZOMBIES_HEALER_ABILITIES
    ),
    ZOMBIES_RANGED("Deadbeard",
        BurbCharacterMainWeapon.ZOMBIES_RANGED_MAIN,
        BurbCharacterAbilities.ZOMBIES_RANGED_ABILITIES
    )
}