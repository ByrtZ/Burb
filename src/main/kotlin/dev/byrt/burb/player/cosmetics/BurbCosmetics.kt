package dev.byrt.burb.player.cosmetics

import dev.byrt.burb.item.ItemRarity
import dev.byrt.burb.item.ItemType
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.library.Translation
import dev.byrt.burb.text.Formatting
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

@Suppress("unstableApiUsage")
object BurbCosmetics {
    fun getCosmeticItem(cosmetic: BurbCosmetic): ItemStack {
        val cosmeticItem = ItemStack(Material.RESIN_CLUMP, 1)
        val cosmeticItemMeta = cosmeticItem.itemMeta
        cosmeticItemMeta.displayName(Formatting.allTags.deserialize("<!i><${cosmetic.cosmeticRarity.rarityColour}>${cosmetic.cosmeticName}"))
        val cosmeticLore = listOf(Formatting.allTags.deserialize("<!i><white>${cosmetic.cosmeticRarity.rarityGlyph}${cosmetic.cosmeticType.typeGlyph}")) + listOf(Formatting.allTags.deserialize("<!i>")) + cosmetic.cosmeticLore + listOf(Formatting.allTags.deserialize("<!i>")) + cosmetic.cosmeticObtainment
        cosmeticItemMeta.lore(cosmeticLore)

        if(cosmetic.cosmeticType == ItemType.HAT) {
            val cosmeticEquippable = cosmeticItemMeta.equippable
            cosmeticEquippable.slot = EquipmentSlot.HEAD
            cosmeticItemMeta.setEquippable(cosmeticEquippable)
        }

        cosmeticItemMeta.itemModel = NamespacedKey("minecraft", cosmetic.cosmeticModel)
        cosmeticItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES)
        cosmeticItem.itemMeta = cosmeticItemMeta
        return cosmeticItem
    }

    fun giveCosmeticItem(player: Player, cosmetic: BurbCosmetic) {
        val cosmeticItem = getCosmeticItem(cosmetic)
        player.inventory.addItem(cosmeticItem)
        player.sendMessage(Formatting.allTags.deserialize("${Translation.Generic.ITEM_RECEIVED_PREFIX}<burbcolour>You received: ").append(cosmeticItem.effectiveName().hoverEvent(cosmeticItem)))
        player.playSound(Sounds.Score.CAPTURE_FRIENDLY)
    }
}

enum class BurbCosmetic(val cosmeticName: String, val cosmeticId: String, val cosmeticLore: List<Component>, val cosmeticObtainment: List<Component>, val cosmeticType: ItemType, val cosmeticModel: String, val cosmeticRarity: ItemRarity) {
    // Common
    HAT_GRAVESTONE("Gravestone", "burb.cosmetic.hat.gravestone", listOf(Formatting.allTags.deserialize("<i><gray>Insert lore lines")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "gravestone_point", ItemRarity.COMMON),
    HAT_CONDIMENTS("Condiments", "burb.cosmetic.hat.condiments", listOf(Formatting.allTags.deserialize("<i><gray>Insert lore lines")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "condiments", ItemRarity.COMMON),
    HAT_MAIL_BOX("Mail Box", "burb.cosmetic.hat.mail_box", listOf(Formatting.allTags.deserialize("<i><gray>Insert lore lines")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "mail_box", ItemRarity.COMMON),
    HAT_SOLDIER_HELMET("Soldier Helmet", "burb.cosmetic.hat.soldier_helmet", listOf(Formatting.allTags.deserialize("<i><gray>Insert lore lines")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "soldier_helmet", ItemRarity.COMMON),
    // Uncommon
    HAT_PLUSH_ALYSSA("Plushie (Alyssa)", "burb.cosmetic.hat.plushie_alyssa", listOf(Formatting.allTags.deserialize("<i><gray>Insert lore lines")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "alyssa", ItemRarity.UNCOMMON),
    HAT_PLUSH_AUSTIN("Plushie (Austin)", "burb.cosmetic.hat.plushie_austin", listOf(Formatting.allTags.deserialize("<i><gray>Insert lore lines")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "austin", ItemRarity.UNCOMMON),
    HAT_PLUSH_BYRT("Plushie (Byrt)", "burb.cosmetic.hat.plushie_byrt", listOf(Formatting.allTags.deserialize("<i><gray>Insert lore lines")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "byrt", ItemRarity.UNCOMMON),
    HAT_PLUSH_FLAMEY("Plushie (Flamey)", "burb.cosmetic.hat.plushie_flamey", listOf(Formatting.allTags.deserialize("<i><gray>Insert lore lines")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "flamey", ItemRarity.UNCOMMON),
    HAT_PLUSH_MASKY("Plushie (Masky)", "burb.cosmetic.hat.plushie_alyssa", listOf(Formatting.allTags.deserialize("<i><gray>Insert lore lines")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "masky", ItemRarity.UNCOMMON),
    // Rare
    HAT_EMOJI_EXPLODE("Emoji (Explode)", "burb.cosmetic.hat.emoji_explode", listOf(Formatting.allTags.deserialize("<i><gray>Insert lore lines")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "explode_emoji", ItemRarity.RARE),
    HAT_EMOJI_FLUSHED("Emoji (Flushed)", "burb.cosmetic.hat.emoji_flushed", listOf(Formatting.allTags.deserialize("<i><gray>Insert lore lines")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "flooshed_emoji", ItemRarity.RARE),
    HAT_EMOJI_NERD("Emoji (Nerd)", "burb.cosmetic.hat.emoji_nerd", listOf(Formatting.allTags.deserialize("<i><gray>Insert lore lines")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "nerd_emoji", ItemRarity.RARE),
    // Epic
    HAT_POTATO_MINE("Potato Mine", "burb.cosmetic.hat.potato_mine", listOf(Formatting.allTags.deserialize("<i><gray>Insert lore lines")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "potato_mine", ItemRarity.EPIC),
    HAT_TRAFFIC_LIGHT("Traffic Light", "burb.cosmetic.hat.traffic_light", listOf(Formatting.allTags.deserialize("<i><gray>Insert lore lines")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "traffic_light", ItemRarity.EPIC),
    HAT_CHILLI_BEAN("Chilli Bean", "burb.cosmetic.hat.chilli_bean", listOf(Formatting.allTags.deserialize("<i><gray>Insert lore lines")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "chilli_bean_bomb", ItemRarity.EPIC),
    // Legendary
    HAT_PENNY_RV("Penny", "burb.cosmetic.hat.penny_rv", listOf(Formatting.allTags.deserialize("<i><gray>Insert lore lines")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "penny_rv", ItemRarity.LEGENDARY),
}