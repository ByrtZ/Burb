package dev.byrt.burb.item

enum class BurbCharacter(val characterName: String, val characterMainWeapon: BurbCharacterMainWeapon, val characterAbility: BurbCharacterAbility) {
    NULL("null", BurbCharacterMainWeapon.NULL, BurbCharacterAbility.NULL),
    PLANTS_SCOUT("Peashooter", BurbCharacterMainWeapon.PLANTS_SCOUT_MAIN, BurbCharacterAbility.PLANTS_SCOUT_ABILITY),
    PLANTS_HEAVY("Chomper", BurbCharacterMainWeapon.PLANTS_HEAVY_MAIN, BurbCharacterAbility.PLANTS_HEAVY_ABILITY),
    PLANTS_HEALER("Sunflower", BurbCharacterMainWeapon.PLANTS_HEALER_MAIN, BurbCharacterAbility.PLANTS_HEALER_ABILITY),
    PLANTS_RANGED("Cactus", BurbCharacterMainWeapon.PLANTS_RANGED_MAIN, BurbCharacterAbility.PLANTS_RANGED_ABILITY),
    ZOMBIES_SCOUT("Foot Soldier", BurbCharacterMainWeapon.ZOMBIES_SCOUT_MAIN, BurbCharacterAbility.ZOMBIES_SCOUT_ABILITY),
    ZOMBIES_HEAVY("All-Star", BurbCharacterMainWeapon.ZOMBIES_HEAVY_MAIN, BurbCharacterAbility.ZOMBIES_HEAVY_ABILITY),
    ZOMBIES_HEALER("Scientist", BurbCharacterMainWeapon.ZOMBIES_HEALER_MAIN, BurbCharacterAbility.ZOMBIES_HEALER_ABILITY),
    ZOMBIES_RANGED("Engineer", BurbCharacterMainWeapon.ZOMBIES_RANGED_MAIN, BurbCharacterAbility.ZOMBIES_RANGED_ABILITY)
}