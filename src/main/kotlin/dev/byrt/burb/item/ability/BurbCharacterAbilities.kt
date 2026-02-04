package dev.byrt.burb.item.ability

enum class BurbCharacterAbilities(val abilitiesName: String, val abilitySet: Set<BurbAbility>) {
    NULL("null", setOf(BurbAbility.NULL)),
    PLANTS_SCOUT_ABILITIES("Peashooter Abilities", setOf(BurbAbility.PLANTS_SCOUT_ABILITY_1, BurbAbility.PLANTS_SCOUT_ABILITY_2, BurbAbility.PLANTS_SCOUT_ABILITY_3)),
    PLANTS_HEAVY_ABILITIES("Chomper Abilities", setOf(BurbAbility.PLANTS_HEAVY_ABILITY_1, BurbAbility.PLANTS_HEAVY_ABILITY_2, BurbAbility.PLANTS_HEAVY_ABILITY_3)),
    PLANTS_HEALER_ABILITIES("Sunflower Abilities", setOf(BurbAbility.PLANTS_HEALER_ABILITY_1, BurbAbility.PLANTS_HEALER_ABILITY_2, BurbAbility.PLANTS_HEALER_ABILITY_3)),
    PLANTS_RANGED_ABILITIES("Cactus Abilities", setOf(BurbAbility.PLANTS_RANGED_ABILITY_1, BurbAbility.PLANTS_RANGED_ABILITY_2, BurbAbility.PLANTS_RANGED_ABILITY_3)),
    ZOMBIES_SCOUT_ABILITIES("Foot Soldier Abilities", setOf(BurbAbility.ZOMBIES_SCOUT_ABILITY_1, BurbAbility.ZOMBIES_SCOUT_ABILITY_2, BurbAbility.ZOMBIES_SCOUT_ABILITY_3)),
    ZOMBIES_HEAVY_ABILITIES("Super Brainz Abilities", setOf(BurbAbility.ZOMBIES_HEAVY_ABILITY_1, BurbAbility.ZOMBIES_HEAVY_ABILITY_2, BurbAbility.ZOMBIES_HEAVY_ABILITY_3)),
    ZOMBIES_HEALER_ABILITIES("Scientist Abilities", setOf(BurbAbility.ZOMBIES_HEALER_ABILITY_1, BurbAbility.ZOMBIES_HEALER_ABILITY_2, BurbAbility.ZOMBIES_HEALER_ABILITY_3)),
    ZOMBIES_RANGED_ABILITIES("Deadbeard Abilities", setOf(BurbAbility.ZOMBIES_RANGED_ABILITY_1, BurbAbility.ZOMBIES_RANGED_ABILITY_2, BurbAbility.ZOMBIES_RANGED_ABILITY_3))
}