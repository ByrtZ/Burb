package dev.byrt.burb.item.type

import dev.byrt.burb.text.GlyphLike

enum class ItemType(val typeName : String, override val rawGlyph: String) : GlyphLike {
    ARMOUR("Armour", "\uF009"),
    CONSUMABLE("Consumable", "\uF010"),
    TOOL("Tool", "\uF011"),
    UTILITY("Utility", "\uF012"),
    WEAPON("Weapon", "\uF013"),
    HAT("Hat", "\uF015"),
    ACCESSORY("Accessory", "\uF014"),
    FISH("Fish", "\uF016")
}