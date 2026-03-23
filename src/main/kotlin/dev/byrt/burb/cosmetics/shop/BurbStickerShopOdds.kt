package dev.byrt.burb.cosmetics.shop

import dev.byrt.burb.cosmetics.BurbCosmetic
import dev.byrt.burb.cosmetics.BurbCosmeticObtainmentSource
import dev.byrt.burb.item.rarity.ItemRarity
import dev.byrt.burb.logger
import kotlin.random.Random

enum class BurbStickerShopOdds(val oddsName: String, val itemRarity: ItemRarity, val weight: Double) {
    COMMON("Common", ItemRarity.COMMON, 40.1),
    UNCOMMON("Uncommon", ItemRarity.UNCOMMON, 35.2),
    RARE("Rare", ItemRarity.RARE, 16.0),
    EPIC("Epic", ItemRarity.EPIC, 7.5),
    LEGENDARY("Legendary", ItemRarity.LEGENDARY, 1.0),
    MYTHIC("Mythic", ItemRarity.MYTHIC, 0.2);

    companion object {
        fun rollRandomCosmetic(): BurbCosmetic {
            return getRandomStickerShopCosmeticFromRarity(getRandomStickerShopRarity())
        }

        private fun getRandomStickerShopRarity(): BurbStickerShopOdds {
            val totalWeight = BurbStickerShopOdds.entries.sumOf { it.weight }
            val randomValue = Random.nextDouble(totalWeight)

            var cumulativeWeight = 0.0
            for (rarity in BurbStickerShopOdds.entries) {
                cumulativeWeight += rarity.weight
                if (randomValue < cumulativeWeight) {
                    return rarity
                }
            }

            logger.warning("Unreachable code hit! No rarity selected")
            return COMMON // Should be unreachable but default to null in case of issue
        }

        private fun getRandomStickerShopCosmeticFromRarity(rarity: BurbStickerShopOdds): BurbCosmetic {
            return BurbCosmetic.entries.filter { cosmetic ->
                cosmetic.cosmeticRarity == rarity.itemRarity &&
                cosmetic.cosmeticObtainment == BurbCosmeticObtainmentSource.STICKER_SHOP
            }.random()
        }
    }
}