package dev.byrt.burb.player.cosmetics

import dev.byrt.burb.item.ItemType
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.library.Translation
import dev.byrt.burb.player.progression.BurbPlayerData
import dev.byrt.burb.plugin
import dev.byrt.burb.text.Formatting

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

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
        val cosmeticLore = listOf(Formatting.allTags.deserialize("<!i><white>${cosmetic.cosmeticRarity.asMiniMesssage()}${cosmetic.cosmeticType.asMiniMesssage()}")) + listOf(Formatting.allTags.deserialize("<!i>")) + cosmetic.cosmeticLore + listOf(Formatting.allTags.deserialize("<!i>")) + cosmetic.cosmeticObtainment
        cosmeticItemMeta.lore(cosmeticLore)

        if(cosmetic.cosmeticType == ItemType.HAT) {
            val cosmeticEquippable = cosmeticItemMeta.equippable
            cosmeticEquippable.slot = EquipmentSlot.HEAD
            cosmeticItemMeta.setEquippable(cosmeticEquippable)
        }

        cosmeticItemMeta.persistentDataContainer.set(NamespacedKey(plugin, "cosmetic"), PersistentDataType.STRING, cosmetic.cosmeticId)
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

    fun getEquippedCosmetic(player: Player, cosmeticType: ItemType): BurbCosmetic {
        val playerConfig = BurbPlayerData.getPlayerConfiguration(player)
        return when(cosmeticType) {
            ItemType.HAT -> getCosmeticById(playerConfig.get("${player.uniqueId}.cosmetic_equipped_hat").toString())
            ItemType.ACCESSORY -> getCosmeticById(playerConfig.get("${player.uniqueId}.cosmetic_equipped_accessory").toString())
            else -> BurbCosmetic.INVALID_COSMETIC
        }
    }

    fun getCosmeticById(string: String): BurbCosmetic {
        for(cosmetic in BurbCosmetic.entries) {
            if(cosmetic.cosmeticId == string) {
                return cosmetic
            }
        }
        return BurbCosmetic.INVALID_COSMETIC
    }
}