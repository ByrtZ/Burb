package dev.byrt.burb.item

import dev.byrt.burb.text.Formatting
import dev.byrt.burb.player.BurbCharacter

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack

object ServerItem {
    fun getCharacterBreakdownItem(character: BurbCharacter): ItemStack {
        val characterItem = ItemStack(if(character.name.startsWith("PLANTS")) Material.LIME_DYE else if(character.name.startsWith("ZOMBIES")) Material.PURPLE_DYE else Material.BLACK_DYE, 1)
        val characterItemMeta = characterItem.itemMeta
        characterItemMeta.displayName(Formatting.allTags.deserialize("<!i>${if(character.name.startsWith("PLANTS")) "<plantscolour>" else if(character.name.startsWith("ZOMBIES")) "<zombiescolour>" else "<#000000>"}<b>${character.characterName.uppercase()}"))
        val loreList = mutableListOf(
            Formatting.allTags.deserialize("<!i>"),
            Formatting.allTags.deserialize("<!i><yellow>Main Weapon:"),
            Formatting.allTags.deserialize("<!i><gray>-<white> ${character.characterMainWeapon.weaponName} <gray>[${character.characterMainWeapon.weaponType.weaponTypeName}]"),
            Formatting.allTags.deserialize("<!i>"),
            Formatting.allTags.deserialize("<!i><light_purple>Abilities:")
        )
        character.characterAbilities.abilitySet.forEach { ability -> loreList.add(Formatting.allTags.deserialize("<!i><gray>-<white> ${ability.abilityName}")) }
        characterItemMeta.lore(loreList)
        characterItem.itemMeta = characterItemMeta
        return characterItem
    }

    fun getProfileItem(): ItemStack {
        val profileItem = ItemStack(Material.RESIN_CLUMP, 1)
        val profileItemMeta = profileItem.itemMeta
        profileItemMeta.displayName(Formatting.allTags.deserialize("<!i><burbcolour>My Profile"))
        val loreList = mutableListOf(
            Formatting.allTags.deserialize("<!i>"),
            Formatting.allTags.deserialize("<!i><yellow>View your profile and cosmetics."),
            Formatting.allTags.deserialize("<!i>")
        )
        profileItemMeta.lore(loreList)
        profileItemMeta.itemModel = NamespacedKey("minecraft", "profile")
        profileItem.itemMeta = profileItemMeta
        return profileItem
    }
}