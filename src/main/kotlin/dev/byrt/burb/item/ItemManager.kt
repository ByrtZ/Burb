package dev.byrt.burb.item

import dev.byrt.burb.chat.ChatUtility
import dev.byrt.burb.chat.Formatting
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.plugin
import dev.byrt.burb.team.Teams

import net.kyori.adventure.text.format.TextDecoration

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.persistence.PersistentDataType

object ItemManager {
    fun givePlayerTeamBoots(player: Player, team: Teams) {
        val teamBoots = ItemStack(Material.LEATHER_BOOTS)
        val teamBootsMeta = teamBoots.itemMeta
        val teamBootsRarity = if(player.isOp) ItemRarity.SPECIAL else ItemRarity.COMMON
        val teamBootsType = ItemType.ARMOUR
        teamBootsMeta.displayName(Formatting.allTags.deserialize("<reset><${teamBootsRarity.rarityColour}>${team.teamName} Team Boots").decoration(TextDecoration.ITALIC, false))
        val teamBootsLore = listOf(
            Formatting.allTags.deserialize("<reset><white>${teamBootsRarity.rarityGlyph}${teamBootsType.typeGlyph}").decoration(TextDecoration.ITALIC, false),
            Formatting.allTags.deserialize("<reset><white>A snazzy pair of ${team.teamName} team's boots.").decoration(TextDecoration.ITALIC, false)
        )
        teamBootsMeta.lore(teamBootsLore)
        teamBootsMeta.isUnbreakable = true
        teamBootsMeta.addEnchant(Enchantment.BINDING_CURSE, 1, true)
        teamBootsMeta.setEnchantmentGlintOverride(false)
        teamBootsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES)
        teamBoots.itemMeta = teamBootsMeta
        val lm = teamBoots.itemMeta as LeatherArmorMeta
        lm.setColor(team.teamColour)
        teamBoots.itemMeta = lm
        player.inventory.boots = teamBoots
    }

    fun giveCharacterItems(player: Player) {
        val burbPlayer = player.burbPlayer()
        val burbPlayerCharacter = burbPlayer.playerCharacter
        val mainWeapon = ItemStack(burbPlayerCharacter.characterMainWeapon.weaponMaterial, 1)
        val mainWeaponMeta = mainWeapon.itemMeta
        mainWeaponMeta.displayName(Formatting.allTags.deserialize("<${ItemRarity.COMMON.rarityColour}>${burbPlayerCharacter.characterMainWeapon.weaponName}").decoration(TextDecoration.ITALIC, false))
        mainWeaponMeta.lore(listOf(
                Formatting.allTags.deserialize("<white>${ItemRarity.COMMON.rarityGlyph}${ItemType.WEAPON.typeGlyph}").decoration(TextDecoration.ITALIC, false),
                Formatting.allTags.deserialize("<white>Damage: <yellow>${burbPlayerCharacter.characterMainWeapon.weaponDamage}<red>${ChatUtility.HEART_UNICODE}<reset>").decoration(TextDecoration.ITALIC, false),
                Formatting.allTags.deserialize("<white>Ammo: <green>${burbPlayerCharacter.characterMainWeapon.maxAmmo}<gray>/<yellow>${burbPlayerCharacter.characterMainWeapon.maxAmmo}<reset>").decoration(TextDecoration.ITALIC, false),
                Formatting.allTags.deserialize("<white>Fire Rate: <yellow>${burbPlayerCharacter.characterMainWeapon.fireRate}t<reset>").decoration(TextDecoration.ITALIC, false),
                Formatting.allTags.deserialize("<white>Reload Speed: <yellow>${burbPlayerCharacter.characterMainWeapon.reloadSpeed}t<reset>").decoration(TextDecoration.ITALIC, false),
                Formatting.allTags.deserialize("<white>${burbPlayerCharacter.characterMainWeapon.weaponLore}").decoration(TextDecoration.ITALIC, false)
            )
        )
        mainWeaponMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES)
        mainWeaponMeta.persistentDataContainer.set(NamespacedKey(plugin,"burb.weapon.damage"), PersistentDataType.DOUBLE, burbPlayerCharacter.characterMainWeapon.weaponDamage)
        mainWeaponMeta.persistentDataContainer.set(NamespacedKey(plugin,"burb.weapon.current_ammo"), PersistentDataType.INTEGER, burbPlayerCharacter.characterMainWeapon.maxAmmo)
        mainWeaponMeta.persistentDataContainer.set(NamespacedKey(plugin,"burb.weapon.max_ammo"), PersistentDataType.INTEGER, burbPlayerCharacter.characterMainWeapon.maxAmmo)
        mainWeaponMeta.persistentDataContainer.set(NamespacedKey(plugin,"burb.weapon.fire_rate"), PersistentDataType.INTEGER, burbPlayerCharacter.characterMainWeapon.fireRate)
        mainWeaponMeta.persistentDataContainer.set(NamespacedKey(plugin,"burb.weapon.reload_speed"), PersistentDataType.INTEGER, burbPlayerCharacter.characterMainWeapon.reloadSpeed)
        mainWeaponMeta.persistentDataContainer.set(NamespacedKey(plugin,"burb.weapon.projectile_velocity"), PersistentDataType.DOUBLE, burbPlayerCharacter.characterMainWeapon.projectileVelocity)
        mainWeapon.itemMeta = mainWeaponMeta
        burbPlayer.getBukkitPlayer().inventory.addItem(mainWeapon)

        val abilityItem = ItemStack(burbPlayerCharacter.characterAbility.abilityMaterial, 1)
        val abilityItemMeta = abilityItem.itemMeta
        abilityItemMeta.displayName(Formatting.allTags.deserialize("<${ItemRarity.COMMON.rarityColour}>${burbPlayerCharacter.characterAbility.abilityName}").decoration(TextDecoration.ITALIC, false))
        abilityItemMeta.lore(listOf(
                Formatting.allTags.deserialize("<white>${ItemRarity.COMMON.rarityGlyph}${ItemType.UTILITY.typeGlyph}").decoration(TextDecoration.ITALIC, false),
                Formatting.allTags.deserialize("<white>${burbPlayerCharacter.characterAbility.abilityLore}").decoration(TextDecoration.ITALIC, false)
            )
        )
        abilityItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES)
        abilityItem.itemMeta = abilityItemMeta
        burbPlayer.getBukkitPlayer().inventory.addItem(abilityItem)
    }

    fun verifyItem(item: ItemStack):  Boolean {
        return (item.persistentDataContainer.has(NamespacedKey(plugin, "burb.weapon.damage"))
                && item.persistentDataContainer.has(NamespacedKey(plugin, "burb.weapon.current_ammo"))
                && item.persistentDataContainer.has(NamespacedKey(plugin, "burb.weapon.max_ammo"))
                && item.persistentDataContainer.has(NamespacedKey(plugin, "burb.weapon.fire_rate"))
                && item.persistentDataContainer.has(NamespacedKey(plugin, "burb.weapon.reload_speed"))
                && item.persistentDataContainer.has(NamespacedKey(plugin, "burb.weapon.projectile_velocity"))
        )
    }
}

/**
 * @param weaponName: Display name
 * @param weaponLore: Item lore
 * @param weaponDamage: Damage dealt by weapon in half hearts
 * @param fireRate: Delay of rate of fire in ticks
 * @param reloadSpeed: Reload delay in ticks
 * @param maxAmmo: Max amount of ammunition held by the weapon
 * @param weaponMaterial: Item material
 */
enum class BurbCharacterMainWeapon(val weaponName: String, val weaponLore: String, val weaponDamage: Double, val fireRate: Int, val reloadSpeed: Int, val maxAmmo: Int, val projectileVelocity: Double, val weaponMaterial: Material) {
    NULL("null", "null", 0.0, 0, 0,0, 0.0, Material.AIR),
    PLANTS_SCOUT_MAIN("Pea Cannon", "Shoots heavy hitting peas.", 2.25, 7, 40, 12, 1.75, Material.POPPED_CHORUS_FRUIT),
    PLANTS_HEAVY_MAIN("Chomper Fangs", "Sharp chomper fangs.", 3.5, 0, 0, 0, 0.0, Material.WOODEN_SWORD),
    PLANTS_HEALER_MAIN("Sun SMG", "Shoots bolts of light.", 1.0, 3, 55, 30, 3.0, Material.POPPED_CHORUS_FRUIT),
    PLANTS_RANGED_MAIN("Cactus Pine Launcher", "Shoots accurate cactus pines.", 5.0, 5, 35, 16, 3.5, Material.POPPED_CHORUS_FRUIT),
    ZOMBIES_SCOUT_MAIN("ZOMBIES_SCOUT_MAIN", "placeholder", 1.0, 0, 0, 0, 0.0, Material.POPPED_CHORUS_FRUIT),
    ZOMBIES_HEAVY_MAIN("ZOMBIES_HEAVY_MAIN", "placeholder", 1.0, 0, 0, 0, 0.0, Material.POPPED_CHORUS_FRUIT),
    ZOMBIES_HEALER_MAIN("ZOMBIES_HEALER_MAIN", "placeholder", 1.0, 0, 0, 0, 0.0, Material.POPPED_CHORUS_FRUIT),
    ZOMBIES_RANGED_MAIN("ZOMBIES_RANGED_MAIN", "placeholder", 1.0, 0, 0, 0, 0.0, Material.POPPED_CHORUS_FRUIT)
}

enum class BurbCharacterAbility(val abilityName: String, val abilityLore: String, val abilityMaterial: Material) {
    NULL("null", "null", Material.AIR),
    PLANTS_SCOUT_ABILITY("PLANTS_SCOUT_ABILITY", "placeholder", Material.POTATO),
    PLANTS_HEAVY_ABILITY("PLANTS_HEAVY_ABILITY", "placeholder", Material.POTATO),
    PLANTS_HEALER_ABILITY("PLANTS_HEALER_ABILITY", "placeholder", Material.POTATO),
    PLANTS_RANGED_ABILITY("PLANTS_RANGED_ABILITY", "placeholder", Material.POTATO),
    ZOMBIES_SCOUT_ABILITY("ZOMBIES_SCOUT_ABILITY", "placeholder", Material.POTATO),
    ZOMBIES_HEAVY_ABILITY("ZOMBIES_HEAVY_ABILITY", "placeholder", Material.POTATO),
    ZOMBIES_HEALER_ABILITY("ZOMBIES_HEALER_ABILITY", "placeholder", Material.POTATO),
    ZOMBIES_RANGED_ABILITY("ZOMBIES_RANGED_ABILITY", "placeholder", Material.POTATO)
}

enum class ItemRarity(val rarityName : String, val rarityColour : String, val rarityGlyph : String) {
    COMMON("Common", "#ffffff", "\uF001"),
    UNCOMMON("Uncommon", "#0ed145", "\uF002"),
    RARE("Rare", "#00a8f3", "\uF003"),
    EPIC("Epic", "#b83dba", "\uF004"),
    LEGENDARY("Legendary", "#ff7f27", "\uF005"),
    MYTHIC("Mythic", "#ff3374", "\uF006"),
    SPECIAL("Special", "#ec1c24", "\uF007"),
    UNREAL("Unreal", "#8666e6", "\uF008")
}

enum class ItemType(val typeName : String, val typeGlyph : String) {
    ARMOUR("Armour", "\uF009"),
    CONSUMABLE("Consumable", "\uF010"),
    TOOL("Tool", "\uF011"),
    UTILITY("Utility", "\uF012"),
    WEAPON("Weapon", "\uF013")
}