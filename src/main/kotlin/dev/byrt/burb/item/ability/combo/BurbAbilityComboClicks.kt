package dev.byrt.burb.item.ability.combo

enum class BurbAbilityComboClicks(val comboClicks: List<BurbAbilityComboClick>) {
    RANGED_ABILITY_1(listOf(BurbAbilityComboClick.LEFT, BurbAbilityComboClick.RIGHT, BurbAbilityComboClick.LEFT)),
    RANGED_ABILITY_2(listOf(BurbAbilityComboClick.LEFT, BurbAbilityComboClick.RIGHT, BurbAbilityComboClick.RIGHT)),
    RANGED_ABILITY_3(listOf(BurbAbilityComboClick.LEFT, BurbAbilityComboClick.LEFT, BurbAbilityComboClick.LEFT)),
    MELEE_ABILITY_1(listOf(BurbAbilityComboClick.RIGHT, BurbAbilityComboClick.LEFT, BurbAbilityComboClick.RIGHT)),
    MELEE_ABILITY_2(listOf(BurbAbilityComboClick.RIGHT, BurbAbilityComboClick.LEFT, BurbAbilityComboClick.LEFT)),
    MELEE_ABILITY_3(listOf(BurbAbilityComboClick.RIGHT, BurbAbilityComboClick.RIGHT, BurbAbilityComboClick.RIGHT));
}