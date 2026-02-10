package dev.byrt.burb.item

import dev.byrt.burb.item.ability.combo.BurbAbilityComboClicks
import dev.byrt.burb.item.rarity.SubRarity
import dev.byrt.burb.item.type.ItemType
import dev.byrt.burb.lobby.npc.BurbNPC
import dev.byrt.burb.lobby.fishing.FishRarity
import dev.byrt.burb.text.Formatting
import dev.byrt.burb.player.BurbCharacter
import dev.byrt.burb.util.extension.fullDecimal

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

object ServerItem {
    fun getCharacterBreakdownItem(character: BurbCharacter): ItemStack {
        val characterItem = ItemStack(if(character.name.startsWith("PLANTS")) Material.LIME_DYE else if(character.name.startsWith("ZOMBIES")) Material.PURPLE_DYE else Material.BLACK_DYE, 1)
        val characterItemMeta = characterItem.itemMeta
        characterItemMeta.displayName(Formatting.allTags.deserialize("<!i>${if(character.name.startsWith("PLANTS")) "<plantscolour>" else if(character.name.startsWith("ZOMBIES")) "<zombiescolour>" else "<#000000>"}<b>${character.characterName.uppercase()}"))
        val loreList = mutableListOf(
            Formatting.allTags.deserialize("<!i>"),
            Formatting.allTags.deserialize("<!i><#ffff00>Main Weapon:"),
            Formatting.allTags.deserialize("<!i><gray>-<white> ${character.characterMainWeapon.weaponName} <gray>[${character.characterMainWeapon.weaponType.weaponTypeName}]"),
            Formatting.allTags.deserialize("<!i>"),
            Formatting.allTags.deserialize("<!i><light_purple>Abilities:")
        )

        for(ability in character.characterAbilities.abilitySet) {
            val comboClicks = mutableListOf<String>()
            for(combo in BurbAbilityComboClicks.entries) {
                if(character in listOf(BurbCharacter.PLANTS_HEAVY, BurbCharacter.ZOMBIES_HEAVY)) {
                    if(combo.name.removePrefix("MELEE_").contains(ability.name.removePrefix("${character.name}_"))) {
                        combo.comboClicks.forEach { comboClick -> comboClicks.add("<aqua>${comboClick.comboAbbreviation}") }
                        loreList.add(Formatting.allTags.deserialize("<!i><gray>-<white> ${ability.abilityName} <gray>- <aqua>${comboClicks.joinToString("<gray>-").trim()}"))
                    }
                }
                if(character in listOf(BurbCharacter.PLANTS_SCOUT, BurbCharacter.PLANTS_RANGED, BurbCharacter.PLANTS_HEALER, BurbCharacter.ZOMBIES_SCOUT, BurbCharacter.ZOMBIES_RANGED, BurbCharacter.ZOMBIES_HEALER)) {
                    if(combo.name.removePrefix("RANGED_").contains(ability.name.removePrefix("${character.name}_"))) {
                        combo.comboClicks.forEach { comboClick -> comboClicks.add("<aqua>${comboClick.comboAbbreviation}") }
                        loreList.add(Formatting.allTags.deserialize("<!i><gray>-<white> ${ability.abilityName} <gray>- <aqua>${comboClicks.joinToString("<gray>-").trim()}"))
                    }
                }
            }
        }
        characterItemMeta.lore(loreList)
        characterItem.itemMeta = characterItemMeta
        return characterItem
    }

    fun getTeamSwitcherItem(): ItemStack {
        val teamSwitcherItem = ItemStack(Material.IRON_SWORD, 1)
        val teamSwitcherItemMeta = teamSwitcherItem.itemMeta
        teamSwitcherItemMeta.displayName(Formatting.allTags.deserialize("<!i><burbcolour>Team & Character Switcher"))
        val loreList = mutableListOf(
            Formatting.allTags.deserialize("<!i>"),
            Formatting.allTags.deserialize("<!i><#ffff00>Change your team and character."),
            Formatting.allTags.deserialize("<!i>")
        )

        teamSwitcherItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        teamSwitcherItemMeta.lore(loreList)
        teamSwitcherItem.itemMeta = teamSwitcherItemMeta
        return teamSwitcherItem
    }

    fun getCosmeticsItem(): ItemStack {
        val cosmeticsItem = ItemStack(Material.CAKE, 1)
        val cosmeticsItemMeta = cosmeticsItem.itemMeta
        cosmeticsItemMeta.displayName(Formatting.allTags.deserialize("<!i><burbcolour>Cosmetics"))
        val loreList = mutableListOf(
            Formatting.allTags.deserialize("<!i>"),
            Formatting.allTags.deserialize("<!i><#ffff00>View your collection of cosmetics."),
            Formatting.allTags.deserialize("<!i>")
        )
        cosmeticsItemMeta.lore(loreList)
        cosmeticsItem.itemMeta = cosmeticsItemMeta
        return cosmeticsItem
    }

    fun getProfileItem(): ItemStack {
        val profileItem = ItemStack(Material.RESIN_BRICK, 1)
        val profileItemMeta = profileItem.itemMeta
        profileItemMeta.displayName(Formatting.allTags.deserialize("<!i><burbcolour>My Profile"))
        val loreList = mutableListOf(
            Formatting.allTags.deserialize("<!i>"),
            Formatting.allTags.deserialize("<!i><#ffff00>Change your team and view your cosmetics."),
            Formatting.allTags.deserialize("<!i>")
        )
        profileItemMeta.lore(loreList)
        profileItemMeta.itemModel = NamespacedKey("minecraft", "profile")
        profileItem.itemMeta = profileItemMeta
        return profileItem
    }

    fun getAdminPanelItem(): ItemStack {
        val adminItem = ItemStack(Material.DIAMOND, 1)
        val adminItemMeta = adminItem.itemMeta
        adminItemMeta.displayName(Formatting.allTags.deserialize("<!i><burbcolour>Admin Panel"))
        val loreList = mutableListOf(
            Formatting.allTags.deserialize("<!i>"),
            Formatting.allTags.deserialize("<!i><red><unicodeprefix:warning> Coming soon... <unicodeprefix:warning>"),
            Formatting.allTags.deserialize("<!i><#ffff00>Access exclusive settings."),
            Formatting.allTags.deserialize("<!i>")
        )
        adminItemMeta.lore(loreList)
        adminItem.itemMeta = adminItemMeta
        return adminItem
    }

    fun getFishingCatalogueItem(): ItemStack {
        val catalogueItem = ItemStack(Material.COD, 1)
        val catalogueItemMeta = catalogueItem.itemMeta
        catalogueItemMeta.displayName(Formatting.allTags.deserialize("<!i><burbcolour>Catalogue"))
        val loreList = mutableListOf(
            Formatting.allTags.deserialize("<!i>"),
            Formatting.allTags.deserialize("<!i><#ffff00>View your collection & statistics"),
            Formatting.allTags.deserialize("<!i><#ffff00>of caught fish."),
            Formatting.allTags.deserialize("<!i>")
        )
        catalogueItemMeta.lore(loreList)
        catalogueItem.itemMeta = catalogueItemMeta
        return catalogueItem
    }

    fun getFishingChancesItem(): ItemStack {
        val chancesItem = ItemStack(Material.FISHING_ROD, 1)
        val chancesItemMeta = chancesItem.itemMeta
        chancesItemMeta.displayName(Formatting.allTags.deserialize("<!i><burbcolour>Chances & Odds"))
        val loreList = mutableListOf(
            Formatting.allTags.deserialize("<!i>"),
            Formatting.allTags.deserialize("<!i><#ffff00>View chances & odds of the"),
            Formatting.allTags.deserialize("<!i><#ffff00>fishing system."),
            Formatting.allTags.deserialize("<!i>")
        )
        chancesItemMeta.lore(loreList)
        chancesItem.itemMeta = chancesItemMeta
        return chancesItem
    }

    fun getFishingChanceItem(rarity: FishRarity): ItemStack {
        val chancesItem = ItemStack(Material.COD, 1)
        val chancesItemMeta = chancesItem.itemMeta
        chancesItemMeta.displayName(Formatting.allTags.deserialize("<!i><${rarity.itemRarity.rarityColour}>${rarity.itemRarity.rarityName} Fish"))
        val loreList = mutableListOf(
            Formatting.allTags.deserialize("<!i><white>${rarity.itemRarity.asMiniMesssage()}${ItemType.FISH.asMiniMesssage()}"),
            Formatting.allTags.deserialize("<!i><#ffff00>Base Chance: <aqua>${rarity.weight}%"),
            Formatting.allTags.deserialize("<!i>"),
            Formatting.allTags.deserialize("<!i><#ffff00>Sub Rarity Chances:"),
            Formatting.allTags.deserialize("<!i><white>${SubRarity.SHINY.asMiniMesssage()} <#ffff00>Chance: <dark_aqua>${(rarity.weight * SubRarity.SHINY.weight).fullDecimal()}%"),
            Formatting.allTags.deserialize("<!i><white>${SubRarity.SHADOW.asMiniMesssage()} <#ffff00>Chance: <dark_aqua>${(rarity.weight * SubRarity.SHADOW.weight).fullDecimal()}%"),
            Formatting.allTags.deserialize("<!i><white>${SubRarity.OBFUSCATED.asMiniMesssage()} <#ffff00>Chance: <dark_aqua>${(rarity.weight * SubRarity.OBFUSCATED.weight).fullDecimal()}%"),
            Formatting.allTags.deserialize("<!i>"),
        )
        chancesItemMeta.lore(loreList)
        chancesItem.itemMeta = chancesItemMeta
        return chancesItem
    }

    fun getFishingRodItem(): ItemStack {
        val rodItem = ItemStack(Material.FISHING_ROD, 1)
        val rodItemMeta = rodItem.itemMeta
        rodItemMeta.displayName(Formatting.allTags.deserialize("<!i><burbcolour>Grab a Fishing rod!"))
        val loreList = mutableListOf(
            Formatting.allTags.deserialize("<!i>"),
            Formatting.allTags.deserialize("<!i><#ffff00>Take a fishing rod to the seas"),
            Formatting.allTags.deserialize("<!i><#ffff00>and catch the rarest of them all!"),
            Formatting.allTags.deserialize("<!i>"),
            Formatting.allTags.deserialize("<!i><burbcolour>[Click to start fishing]"),
            Formatting.allTags.deserialize("<!i>")
        )
        rodItemMeta.lore(loreList)
        rodItem.itemMeta = rodItemMeta
        return rodItem
    }

    fun getUsableFishingRodItem(): ItemStack {
        val rodItem = ItemStack(Material.FISHING_ROD, 1)
        val rodItemMeta = rodItem.itemMeta
        rodItemMeta.displayName(Formatting.allTags.deserialize("<!i><burbcolour>Simple Trusty Rod"))
        val loreList = mutableListOf(
            Formatting.allTags.deserialize("<!i>"),
            Formatting.allTags.deserialize("<!i><#ffff00>Loaned to you by ${BurbNPC.LOBBY_FISHING_ROD_GIVER.npcName},"),
            Formatting.allTags.deserialize("<!i><#ffff00>make sure to look after it!"),
            Formatting.allTags.deserialize("<!i>")
        )
        rodItemMeta.lore(loreList)
        rodItemMeta.isUnbreakable = true
        rodItemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
        rodItem.itemMeta = rodItemMeta
        return rodItem
    }

    fun getUnconfiguredItem(): ItemStack {
        val unconfiguredItem = ItemStack(Material.STRUCTURE_VOID, 1)
        val unconfiguredItemMeta = unconfiguredItem.itemMeta
        unconfiguredItemMeta.displayName(Formatting.allTags.deserialize("<!i><b><#ff3333>Unconfigured Feature"))
        val loreList = mutableListOf(
            Formatting.allTags.deserialize("<!i>"),
            Formatting.allTags.deserialize("<!i><#ffff00>This feature is currently unconfigured,"),
            Formatting.allTags.deserialize("<!i><#ffff00>please check back again soon!"),
            Formatting.allTags.deserialize("<!i>")
        )
        unconfiguredItemMeta.lore(loreList)
        unconfiguredItem.itemMeta = unconfiguredItemMeta
        return unconfiguredItem
    }

    fun getFrumaItem(): ItemStack {
        val frumaItem = ItemStack(Material.PAPER, 1)
        val frumaItemMeta = frumaItem.itemMeta
        frumaItemMeta.displayName(Formatting.allTags.deserialize("<!i><gold>Royal Report XII"))
        val loreList = mutableListOf(
            Formatting.allTags.deserialize("<!i>"),
            Formatting.allTags.deserialize("<!i><white><underlined>Report IA-120.-39, \"SUBURBIA\""),
            Formatting.allTags.deserialize("<!i>"),
            Formatting.allTags.deserialize("<!i><gray>- Multiverse traversal attempt: #4."),
            Formatting.allTags.deserialize("<!i><gray>- War over the past 2 years: Steady."),
            Formatting.allTags.deserialize("<!i><gray>- Zombie to Plant ratio down by 21%."),
            Formatting.allTags.deserialize("<!i><gray>- No sign of elevated invasion levels."),
            Formatting.allTags.deserialize("<!i><gray>- Observation complete, returning to Fruma."),
            Formatting.allTags.deserialize("<!i>"),
            Formatting.allTags.deserialize("<!i><gray>Conclusion: <b>STABLE"),
            Formatting.allTags.deserialize("<!i>")
        )
        frumaItemMeta.lore(loreList)
        frumaItem.itemMeta = frumaItemMeta
        return frumaItem
    }
}