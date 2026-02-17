package dev.byrt.burb.item.ability.combo

import dev.byrt.burb.item.ability.BurbAbility
import dev.byrt.burb.player.character.BurbCharacter
import dev.byrt.burb.player.character.BurbCharacterType
import dev.byrt.burb.text.Formatting
import net.kyori.adventure.text.Component

enum class BurbAbilityComboClicks(val comboClicks: List<BurbAbilityComboClick>) {
    RANGED_ABILITY_1(listOf(BurbAbilityComboClick.LEFT, BurbAbilityComboClick.RIGHT, BurbAbilityComboClick.LEFT)),
    RANGED_ABILITY_2(listOf(BurbAbilityComboClick.LEFT, BurbAbilityComboClick.RIGHT, BurbAbilityComboClick.RIGHT)),
    RANGED_ABILITY_3(listOf(BurbAbilityComboClick.LEFT, BurbAbilityComboClick.LEFT, BurbAbilityComboClick.LEFT)),
    MELEE_ABILITY_1(listOf(BurbAbilityComboClick.RIGHT, BurbAbilityComboClick.LEFT, BurbAbilityComboClick.RIGHT)),
    MELEE_ABILITY_2(listOf(BurbAbilityComboClick.RIGHT, BurbAbilityComboClick.LEFT, BurbAbilityComboClick.LEFT)),
    MELEE_ABILITY_3(listOf(BurbAbilityComboClick.RIGHT, BurbAbilityComboClick.RIGHT, BurbAbilityComboClick.RIGHT));
}

fun BurbAbility.getDisplayCombo(character: BurbCharacter): Component {
    if(character.characterType == BurbCharacterType.MELEE) {
        for(combo in BurbAbilityComboClicks.entries.filter { it.name.startsWith("MELEE") }) {
            val comboClicks = mutableListOf<String>()
            if(combo.name.removePrefix("MELEE_").contains(this.name.removePrefix("${character.name}_"))) {
                combo.comboClicks.forEach { comboClick -> comboClicks.add("<aqua>${comboClick.comboAbbreviation}") }
                return Formatting.allTags.deserialize("<!i><aqua>${comboClicks.joinToString("<gray>-").trim()}")
            }
        }
    }
    if(character.characterType == BurbCharacterType.RANGED) {
        for(combo in BurbAbilityComboClicks.entries.filter { it.name.startsWith("RANGED") }) {
            val comboClicks = mutableListOf<String>()
            if(combo.name.removePrefix("RANGED_").contains(this.name.removePrefix("${character.name}_"))) {
                combo.comboClicks.forEach { comboClick -> comboClicks.add("<aqua>${comboClick.comboAbbreviation}") }
                return Formatting.allTags.deserialize("<!i><aqua>${comboClicks.joinToString("<gray>-").trim()}")
            }
        }
    }
    return Component.empty()
}