package dev.byrt.burb.player.cosmetics

import dev.byrt.burb.item.ItemRarity
import dev.byrt.burb.item.ItemType
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.library.Translation
import dev.byrt.burb.player.progression.BurbPlayerData
import dev.byrt.burb.plugin
import dev.byrt.burb.text.Formatting
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.io.File

@Suppress("unstableApiUsage")
object BurbCosmetics {
    fun unlockCosmetic(player: Player, cosmetic: BurbCosmetic) {
        val playerConfig = BurbPlayerData.getPlayerConfiguration(player)
        val rawUnlockedCosmetics = playerConfig.getList("${player.uniqueId}.cosmetics") ?: emptyList()
        val unlockedCosmetics = rawUnlockedCosmetics.mapNotNull { it as? String }.toMutableList()
        if(unlockedCosmetics.contains(cosmetic.cosmeticId)) return

        unlockedCosmetics.add(cosmetic.cosmeticId)
        playerConfig.set("${player.uniqueId}.cosmetics", unlockedCosmetics)
        playerConfig.save(File(plugin.dataFolder, "${player.uniqueId}.yml"))

        val cosmeticItem = getCosmeticItem(cosmetic)
        player.sendMessage(Formatting.allTags.deserialize("${Translation.Generic.ITEM_RECEIVED_PREFIX}<burbcolour>You received: ").append(cosmeticItem.effectiveName().hoverEvent(cosmeticItem)))
        player.playSound(Sounds.Score.CAPTURE_FRIENDLY)
    }

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

    fun equipCosmetic(player: Player, cosmetic: BurbCosmetic, isReequipped: Boolean) {
        val playerConfig = BurbPlayerData.getPlayerConfiguration(player)
        val rawUnlockedCosmetics = playerConfig.getList("${player.uniqueId}.cosmetics") ?: emptyList()
        val unlockedCosmetics = rawUnlockedCosmetics.mapNotNull { it as? String }.toMutableList()
        if(unlockedCosmetics.contains(cosmetic.cosmeticId)) {
            val cosmeticItem = getCosmeticItem(cosmetic)
            when(cosmetic.cosmeticType) {
                ItemType.HAT -> {
                    player.inventory.helmet = cosmeticItem
                    playerConfig.set("${player.uniqueId}.cosmetic_equipped_hat", cosmetic.cosmeticId)
                }
                ItemType.ACCESSORY -> {
                    player.inventory.setItemInOffHand(cosmeticItem)
                    playerConfig.set("${player.uniqueId}.cosmetic_equipped_accessory", cosmetic.cosmeticId)
                }
                else -> { player.sendMessage(Formatting.allTags.deserialize("<red>Invalid cosmetic type, please contact an admin if you see this.")) }
            }
            playerConfig.save(File(plugin.dataFolder, "${player.uniqueId}.yml"))
            if(!isReequipped) {
                player.sendMessage(Formatting.allTags.deserialize("<burbcolour>You equipped the ").append(cosmeticItem.effectiveName().hoverEvent(cosmeticItem)).append(Formatting.allTags.deserialize(" <burbcolour>cosmetic.")))
                player.playSound(Sounds.Score.CAPTURE_FRIENDLY)
            }
        } else {
            player.sendMessage(Formatting.allTags.deserialize("<red>You do not have this cosmetic unlocked!"))
            player.playSound(Sounds.Score.CAPTURE_UNFRIENDLY)
        }
    }

    fun equipCosmetics(player: Player) {
        val equippedHat = getEquippedCosmetic(player, ItemType.HAT)
        val equippedAccessory = getEquippedCosmetic(player, ItemType.ACCESSORY)
        if(equippedHat != BurbCosmetic.INVALID_COSMETIC) {
            equipCosmetic(player, equippedHat, true)
        }
        if(equippedAccessory != BurbCosmetic.INVALID_COSMETIC) {
            equipCosmetic(player, equippedAccessory, true)
        }
    }

    fun unequipCosmetic(player: Player, cosmeticType: ItemType) {
        val playerConfig = BurbPlayerData.getPlayerConfiguration(player)
        when(cosmeticType) {
            ItemType.HAT -> {
                player.inventory.helmet = null
                playerConfig.set("${player.uniqueId}.cosmetic_equipped_hat", "")
            }
            ItemType.ACCESSORY -> {
                player.inventory.setItemInOffHand(null)
                playerConfig.set("${player.uniqueId}.cosmetic_equipped_accessory", "")
            }
            else -> { player.sendMessage(Formatting.allTags.deserialize("<red>Invalid cosmetic type, please contact an admin if you see this.")) }
        }
        playerConfig.save(File(plugin.dataFolder, "${player.uniqueId}.yml"))
        player.sendMessage(Formatting.allTags.deserialize("<burbcolour>You unequipped your ${cosmeticType.typeName.lowercase()}."))
        player.playSound(Sounds.Score.CAPTURE_UNFRIENDLY)
    }

    fun giveCosmeticItem(player: Player, cosmetic: BurbCosmetic) {
        val cosmeticItem = getCosmeticItem(cosmetic)
        player.inventory.addItem(cosmeticItem)
        player.sendMessage(Formatting.allTags.deserialize("${Translation.Generic.ITEM_RECEIVED_PREFIX}<burbcolour>You received: ").append(cosmeticItem.effectiveName().hoverEvent(cosmeticItem)))
        player.playSound(Sounds.Score.CAPTURE_FRIENDLY)
    }

    private fun getEquippedCosmetic(player: Player, cosmeticType: ItemType): BurbCosmetic {
        val playerConfig = BurbPlayerData.getPlayerConfiguration(player)
        return when(cosmeticType) {
            ItemType.HAT -> getCosmeticById(playerConfig.get("${player.uniqueId}.cosmetic_equipped_hat").toString())
            ItemType.ACCESSORY -> getCosmeticById(playerConfig.get("${player.uniqueId}.cosmetic_equipped_accessory").toString())
            else -> BurbCosmetic.INVALID_COSMETIC
        }
    }

    private fun getCosmeticById(string: String): BurbCosmetic {
        for(cosmetic in BurbCosmetic.entries) {
            if(cosmetic.cosmeticId == string) {
                return cosmetic
            }
        }
        return BurbCosmetic.INVALID_COSMETIC
    }
}

enum class BurbCosmetic(val cosmeticName: String, val cosmeticId: String, val cosmeticLore: List<Component>, val cosmeticObtainment: List<Component>, val cosmeticType: ItemType, val cosmeticModel: String, val cosmeticRarity: ItemRarity) {
    INVALID_COSMETIC("Invalid", "burb.cosmetic.invalid", listOf(Formatting.allTags.deserialize("<i><gray>How did you get this?")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained by encountering an error.")), ItemType.ACCESSORY, "barrier", ItemRarity.SPECIAL),
    // Common
    HAT_GRAVESTONE("Gravestone", "burb.cosmetic.hat.gravestone", listOf(Formatting.allTags.deserialize("<i><gray>You are die.")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "gravestone_point", ItemRarity.COMMON),
    HAT_CONDIMENTS("Condiments", "burb.cosmetic.hat.condiments", listOf(Formatting.allTags.deserialize("<i><gray>Pick your favourite!")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "condiments", ItemRarity.COMMON),
    HAT_MAIL_BOX("Mail Box", "burb.cosmetic.hat.mail_box", listOf(Formatting.allTags.deserialize("<i><gray>You've got mail.")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "mail_box", ItemRarity.COMMON),
    HAT_SOLDIER_HELMET("Soldier Helmet", "burb.cosmetic.hat.soldier_helmet", listOf(Formatting.allTags.deserialize("<i><gray>Safety first!")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "soldier_helmet", ItemRarity.COMMON),
    // Uncommon
    HAT_PLUSH_ALYSSA("Plushie (Alyssa)", "burb.cosmetic.hat.plushie_alyssa", listOf(Formatting.allTags.deserialize("<i><gray>Help! I'm a marketable plushie.")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "alyssa", ItemRarity.UNCOMMON),
    HAT_PLUSH_AUSTIN("Plushie (Austin)", "burb.cosmetic.hat.plushie_austin", listOf(Formatting.allTags.deserialize("<i><gray>Help! I'm a marketable plushie.")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "austin", ItemRarity.UNCOMMON),
    HAT_PLUSH_BYRT("Plushie (Byrt)", "burb.cosmetic.hat.plushie_byrt", listOf(Formatting.allTags.deserialize("<i><gray>Help! I'm a marketable plushie.")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "byrt", ItemRarity.UNCOMMON),
    HAT_PLUSH_FLAMEY("Plushie (Flamey)", "burb.cosmetic.hat.plushie_flamey", listOf(Formatting.allTags.deserialize("<i><gray>Help! I'm a marketable plushie.")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "flamey", ItemRarity.UNCOMMON),
    HAT_PLUSH_MASKY("Plushie (Masky)", "burb.cosmetic.hat.plushie_masky", listOf(Formatting.allTags.deserialize("<i><gray>Help! I'm a marketable plushie.")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "masky", ItemRarity.UNCOMMON),
    HAT_MOUSTACHE("Stylish Stache", "burb.cosmetic.hat.moustache", listOf(Formatting.allTags.deserialize("<i><gray>How sophisticated of you!")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "moustache", ItemRarity.UNCOMMON),
    HAT_TOP_HAT("Top Hat", "burb.cosmetic.hat.top_hat", listOf(Formatting.allTags.deserialize("<i><gray>*tips top hat*")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "tophat", ItemRarity.UNCOMMON),
    // Rare
    HAT_EMOJI_EXPLODE("Emoji (Explode)", "burb.cosmetic.hat.emoji_explode", listOf(Formatting.allTags.deserialize("<i><gray>MIND. BLOWN.")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "explode_emoji", ItemRarity.RARE),
    HAT_EMOJI_FLUSHED("Emoji (Flushed)", "burb.cosmetic.hat.emoji_flushed", listOf(Formatting.allTags.deserialize("<i><gray>:point_right: :point_left:")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "flooshed_emoji", ItemRarity.RARE),
    HAT_EMOJI_NERD("Emoji (Nerd)", "burb.cosmetic.hat.emoji_nerd", listOf(Formatting.allTags.deserialize("<i><gray>Erm, actually...")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "nerd_emoji", ItemRarity.RARE),
    HAT_EMOJI_THINKING("Emoji (Thinking)", "burb.cosmetic.hat.emoji_thinking", listOf(Formatting.allTags.deserialize("<i><gray>Let me get back to you on that.")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "thinking_emoji", ItemRarity.RARE),
    HAT_DEMON_HORNS("Demon Horns", "burb.cosmetic.hat.demon_horns", listOf(Formatting.allTags.deserialize("<i><gray>You can call me... <red>Mephisto<gray>.")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "demon_horns", ItemRarity.RARE),
    // Epic
    HAT_POTATO_MINE("Potato Mine", "burb.cosmetic.hat.potato_mine", listOf(Formatting.allTags.deserialize("<i><gray>SPUD-OW!")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "potato_mine", ItemRarity.EPIC),
    HAT_TRAFFIC_LIGHT("Traffic Light", "burb.cosmetic.hat.traffic_light", listOf(Formatting.allTags.deserialize("<i><gray>Don't run it, when it's red.")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "traffic_light", ItemRarity.EPIC),
    HAT_CHILLI_BEAN("Chilli Bean", "burb.cosmetic.hat.chilli_bean", listOf(Formatting.allTags.deserialize("<i><gray>UNDERLAY!")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "chilli_bean_bomb", ItemRarity.EPIC),
    HAT_FOOTBALL_HELMET("Football Helmet", "burb.cosmetic.hat.football_helmet", listOf(Formatting.allTags.deserialize("<i><gray>GAME ON!")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "football_helmet", ItemRarity.EPIC),
    ACCESSORY_GOO_BACKPACK("Goo Backpack", "burb.cosmetic.accessory.goo_backpack", listOf(Formatting.allTags.deserialize("<i><gray>You can never have enough goo.")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.ACCESSORY, "goo_backpack", ItemRarity.EPIC),
    // Legendary
    HAT_PENNY_RV("Penny", "burb.cosmetic.hat.penny_rv", listOf(Formatting.allTags.deserialize("<i><gray>Crazy Dave's best friend.")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "penny_rv", ItemRarity.LEGENDARY),
    HAT_ROCKET_SHUTTLE("Rocket Shuttle", "burb.cosmetic.hat.rocket_shuttle", listOf(Formatting.allTags.deserialize("<i><gray>Take off in, T-minus 3 seconds.")), listOf(Formatting.allTags.deserialize("<!i><burbcolour>Obtained from the sticker shop.")), ItemType.HAT, "rocket_shuttle", ItemRarity.LEGENDARY),
}