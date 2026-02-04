package dev.byrt.burb.item.rarity

import dev.byrt.burb.logger
import dev.byrt.burb.text.GlyphLike
import kotlin.collections.sumOf
import kotlin.random.Random

enum class SubRarity(val weight : Double, override val rawGlyph : String) : GlyphLike {
    NONE(99.95,""),
    SHINY(0.025, "\uE000"),
    SHADOW(0.015, "\uE001"),
    OBFUSCATED(0.01, "\uE002");

    companion object {
        fun getRandomSubRarity(): SubRarity {
            val totalWeight = SubRarity.entries.sumOf { it.weight }
            val randomValue = Random.nextDouble(totalWeight)

            var cumulativeWeight = 0.0
            for (rarity in SubRarity.entries) {
                cumulativeWeight += rarity.weight
                if (randomValue < cumulativeWeight) {
                    return rarity
                }
            }

            logger.warning("Unreachable code hit! No sub rarity selected")
            return NONE // Should be unreachable but default to null in case of issue
        }
    }
}