package dev.byrt.burb.item.ability.combo

class BurbAbilityCombo {
    val combo = mutableListOf<BurbAbilityComboClick>()
        get() { return field }

    fun addClick(click: BurbAbilityComboClick) {
        combo.add(click)
    }

    fun cancelCombo() {
        combo.clear()
    }
}