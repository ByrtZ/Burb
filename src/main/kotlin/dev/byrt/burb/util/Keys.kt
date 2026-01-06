package dev.byrt.burb.util

import dev.byrt.burb.plugin
import org.bukkit.NamespacedKey

object Keys {
    /**
     * Fishing related
     */
    val FISH_RARITY = NamespacedKey(plugin, "fish.rarity")
    val FISH_IS_SHINY = NamespacedKey(plugin, "fish.is_shiny")
    val FISH_IS_SHADOW = NamespacedKey(plugin, "fish.is_shadow")
    val FISH_IS_OBFUSCATED = NamespacedKey(plugin, "fish.is_obfuscated")
}