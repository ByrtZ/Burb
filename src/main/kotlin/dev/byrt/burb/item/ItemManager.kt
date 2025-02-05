package dev.byrt.burb.item

import dev.byrt.burb.chat.ChatUtility
import dev.byrt.burb.chat.Formatting
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.plugin
import dev.byrt.burb.team.Teams

import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Color

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.persistence.PersistentDataType

@Suppress("unstableApiUsage")
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
        if(team == Teams.SPECTATOR && player.isOp) {
            lm.setColor(Color.MAROON)
        } else {
            lm.setColor(team.teamColour)
        }
        teamBoots.itemMeta = lm
        player.inventory.boots = teamBoots
    }

    fun giveCharacterItems(player: Player) {
        val burbPlayer = player.burbPlayer()
        val burbPlayerCharacter = burbPlayer.playerCharacter

        clearItems(player)

        val mainWeapon = ItemStack(burbPlayerCharacter.characterMainWeapon.weaponMaterial, 1)
        val mainWeaponMeta = mainWeapon.itemMeta
        mainWeaponMeta.displayName(Formatting.allTags.deserialize("<${ItemRarity.COMMON.rarityColour}>${burbPlayerCharacter.characterMainWeapon.weaponName}").decoration(TextDecoration.ITALIC, false))
        if(burbPlayerCharacter.characterMainWeapon.weaponType == BurbMainWeaponType.MELEE) {
            mainWeaponMeta.lore(listOf(
                Formatting.allTags.deserialize("<white>${ItemRarity.COMMON.rarityGlyph}${ItemType.WEAPON.typeGlyph}").decoration(TextDecoration.ITALIC, false),
                Formatting.allTags.deserialize("<white>Damage: <yellow>${burbPlayerCharacter.characterMainWeapon.weaponDamage}<red>${ChatUtility.HEART_UNICODE}<reset>").decoration(TextDecoration.ITALIC, false),
                Formatting.allTags.deserialize("<white>${burbPlayerCharacter.characterMainWeapon.weaponLore}").decoration(TextDecoration.ITALIC, false)
            ))
        } else {
            mainWeaponMeta.lore(listOf(
                Formatting.allTags.deserialize("<white>${ItemRarity.COMMON.rarityGlyph}${ItemType.WEAPON.typeGlyph}").decoration(TextDecoration.ITALIC, false),
                Formatting.allTags.deserialize("<white>Damage: <yellow>${burbPlayerCharacter.characterMainWeapon.weaponDamage}<red>${ChatUtility.HEART_UNICODE}<reset>").decoration(TextDecoration.ITALIC, false),
                Formatting.allTags.deserialize("<white>Ammo: <green>${burbPlayerCharacter.characterMainWeapon.maxAmmo}<gray>/<yellow>${burbPlayerCharacter.characterMainWeapon.maxAmmo}<reset>").decoration(TextDecoration.ITALIC, false),
                Formatting.allTags.deserialize("<white>Fire Rate: <yellow>${burbPlayerCharacter.characterMainWeapon.fireRate}t<reset>").decoration(TextDecoration.ITALIC, false),
                Formatting.allTags.deserialize("<white>Reload Speed: <yellow>${burbPlayerCharacter.characterMainWeapon.reloadSpeed}t<reset>").decoration(TextDecoration.ITALIC, false),
                Formatting.allTags.deserialize("<white>${burbPlayerCharacter.characterMainWeapon.weaponLore}").decoration(TextDecoration.ITALIC, false)
            ))
        }
        mainWeaponMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES)

        mainWeaponMeta.persistentDataContainer.set(NamespacedKey(plugin,"burb.weapon.damage"), PersistentDataType.DOUBLE, burbPlayerCharacter.characterMainWeapon.weaponDamage)
        mainWeaponMeta.persistentDataContainer.set(NamespacedKey(plugin,"burb.weapon.sound"), PersistentDataType.STRING, burbPlayerCharacter.characterMainWeapon.useSound)

        if(burbPlayerCharacter.characterMainWeapon.weaponType !in listOf(BurbMainWeaponType.MELEE, BurbMainWeaponType.NULL)) {
            mainWeaponMeta.persistentDataContainer.set(NamespacedKey(plugin,"burb.weapon.current_ammo"), PersistentDataType.INTEGER, burbPlayerCharacter.characterMainWeapon.maxAmmo)
            mainWeaponMeta.persistentDataContainer.set(NamespacedKey(plugin,"burb.weapon.max_ammo"), PersistentDataType.INTEGER, burbPlayerCharacter.characterMainWeapon.maxAmmo)
            mainWeaponMeta.persistentDataContainer.set(NamespacedKey(plugin,"burb.weapon.fire_rate"), PersistentDataType.INTEGER, burbPlayerCharacter.characterMainWeapon.fireRate)
            mainWeaponMeta.persistentDataContainer.set(NamespacedKey(plugin,"burb.weapon.reload_speed"), PersistentDataType.INTEGER, burbPlayerCharacter.characterMainWeapon.reloadSpeed)
            mainWeaponMeta.persistentDataContainer.set(NamespacedKey(plugin,"burb.weapon.projectile_velocity"), PersistentDataType.DOUBLE, burbPlayerCharacter.characterMainWeapon.projectileVelocity)
        }

        mainWeaponMeta.setCustomModelData(burbPlayerCharacter.characterMainWeapon.customModelData)

        mainWeapon.itemMeta = mainWeaponMeta
        burbPlayer.getBukkitPlayer().inventory.addItem(mainWeapon)

        // If melee main weapon, add opposing hand display weapon. Ensure model data is incremented by one in pack.
        if(burbPlayerCharacter.characterMainWeapon.weaponType == BurbMainWeaponType.MELEE) {
            mainWeaponMeta.setCustomModelData(burbPlayerCharacter.characterMainWeapon.customModelData + 1)
            mainWeapon.itemMeta = mainWeaponMeta
            burbPlayer.getBukkitPlayer().inventory.setItemInOffHand(mainWeapon)
        }

        for(ability in burbPlayerCharacter.characterAbilities.abilitySet) {
            val abilityItem = ItemStack(ability.abilityMaterial, 1)
            val abilityItemMeta = abilityItem.itemMeta
            abilityItemMeta.setJukeboxPlayable(null)
            abilityItemMeta.persistentDataContainer.set(NamespacedKey(plugin, "burb.ability.id"), PersistentDataType.STRING, ability.abilityId)
            abilityItemMeta.displayName(Formatting.allTags.deserialize("<${ItemRarity.COMMON.rarityColour}>${if(ability.abilityName == "") ability.abilityId + ".name" else ability.abilityName}").decoration(TextDecoration.ITALIC, false))
            abilityItemMeta.lore(listOf(
                    Formatting.allTags.deserialize("<white>${ItemRarity.COMMON.rarityGlyph}${ItemType.UTILITY.typeGlyph}").decoration(TextDecoration.ITALIC, false),
                    Formatting.allTags.deserialize("<white>${if(ability.abilityLore == "") ability.abilityId + ".lore" else ability.abilityLore}").decoration(TextDecoration.ITALIC, false)
                )
            )
            abilityItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES)
            abilityItem.itemMeta = abilityItemMeta
            burbPlayer.getBukkitPlayer().inventory.addItem(abilityItem)
        }
    }

    fun clearItems(player: Player) {
        player.inventory.setItemInMainHand(null)
        player.inventory.setItemInOffHand(null)
        player.inventory.remove(Material.POTATO)
        player.inventory.remove(Material.DISC_FRAGMENT_5)
        player.inventory.remove(Material.POPPED_CHORUS_FRUIT)
        player.inventory.remove(Material.WOODEN_SWORD)
    }

    fun verifyItem(item: ItemStack):  Boolean {
        return (item.persistentDataContainer.has(NamespacedKey(plugin, "burb.weapon.damage"))
                && item.persistentDataContainer.has(NamespacedKey(plugin, "burb.weapon.current_ammo"))
                && item.persistentDataContainer.has(NamespacedKey(plugin, "burb.weapon.max_ammo"))
                && item.persistentDataContainer.has(NamespacedKey(plugin, "burb.weapon.fire_rate"))
                && item.persistentDataContainer.has(NamespacedKey(plugin, "burb.weapon.reload_speed"))
                && item.persistentDataContainer.has(NamespacedKey(plugin, "burb.weapon.projectile_velocity"))
                && item.persistentDataContainer.has(NamespacedKey(plugin, "burb.weapon.sound"))
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
enum class BurbCharacterMainWeapon(val weaponName: String, val weaponLore: String, val weaponType: BurbMainWeaponType, val weaponDamage: Double, val fireRate: Int, val reloadSpeed: Int, val maxAmmo: Int, val projectileVelocity: Double, val weaponMaterial: Material, val useSound: String, val customModelData: Int) {
    NULL("null", "null", BurbMainWeaponType.NULL, 0.0, 0, 0,0, 0.0, Material.AIR, "null", 0),
    PLANTS_SCOUT_MAIN("Pea Cannon", "Shoots heavy hitting peas.", BurbMainWeaponType.RIFLE,2.25, 8, 40, 12, 1.75, Material.POPPED_CHORUS_FRUIT, "null",0),
    PLANTS_HEAVY_MAIN("Chomp", "Sharp chomper fangs.", BurbMainWeaponType.MELEE,3.5, 0, 0, 0, 0.0, Material.WOODEN_SWORD, "null",3),
    PLANTS_HEALER_MAIN("Sun Pulse", "Shoots bolts of light.", BurbMainWeaponType.RIFLE,1.0, 3, 55, 30, 3.0, Material.POPPED_CHORUS_FRUIT, "null",0),
    PLANTS_RANGED_MAIN("Spike Shot", "Shoots accurate cactus pines.", BurbMainWeaponType.RIFLE,5.0, 12, 35, 16, 4.0, Material.POPPED_CHORUS_FRUIT, "null",0),
    ZOMBIES_SCOUT_MAIN("Z-1 Assault Blaster", "Shoots Z1 pellets.", BurbMainWeaponType.RIFLE,1.5, 2, 50, 30, 2.25, Material.POPPED_CHORUS_FRUIT, "burb.weapon.foot_soldier.fire",1),
    ZOMBIES_HEAVY_MAIN("Heroic Fists", "Super Brainz' powerful fists.", BurbMainWeaponType.MELEE,3.5, 0, 0, 0, 0.0, Material.WOODEN_SWORD, "null",1),
    ZOMBIES_HEALER_MAIN("Goo Blaster", "Shoots yucky clumps of goo.", BurbMainWeaponType.SHOTGUN,4.0, 14, 65, 16, 2.5, Material.POPPED_CHORUS_FRUIT, "null",4),
    ZOMBIES_RANGED_MAIN("Spyglass Shot", "Shoots accurate glass shards.", BurbMainWeaponType.RIFLE,6.0, 16, 60, 12, 4.25, Material.POPPED_CHORUS_FRUIT, "null",6)
}

enum class BurbMainWeaponType(val weaponTypeName: String) {
    NULL("null"),
    RIFLE("Rifle"),
    SHOTGUN("Shotgun"),
    MELEE("Melee")
}

enum class BurbAbility(val abilityName: String, val abilityLore: String, val abilityId: String, val abilityMaterial: Material) {
    NULL("null", "null", "null", Material.AIR),
    PLANTS_SCOUT_ABILITY_1("Chilli Bean Bomb", "A chilli bean with a short temper.","burb.character.plants_scout.ability.1", Material.DISC_FRAGMENT_5),
    PLANTS_SCOUT_ABILITY_2("Hyper", "Zoomies!", "burb.character.plants_scout.ability.2", Material.DISC_FRAGMENT_5),
    PLANTS_HEAVY_ABILITY_1("", "", "burb.character.plants_heavy.ability.1", Material.POTATO),
    PLANTS_HEALER_ABILITY_1("", "", "burb.character.plants_healer.ability.1", Material.POTATO),
    PLANTS_RANGED_ABILITY_1("", "", "burb.character.plants_ranged.ability.1", Material.POTATO),
    ZOMBIES_SCOUT_ABILITY_1("", "", "burb.character.zombies_scout.ability.1", Material.POTATO),
    ZOMBIES_HEAVY_ABILITY_1("", "", "burb.character.zombies_heavy.ability.1", Material.POTATO),
    ZOMBIES_HEALER_ABILITY_1("", "", "burb.character.zombies_healer.ability.1", Material.POTATO),
    ZOMBIES_RANGED_ABILITY_1("", "", "burb.character.zombies_ranged.ability.1", Material.POTATO)
}

enum class BurbCharacterAbilities(val abilitiesName: String, val abilitySet: Set<BurbAbility>) {
    NULL("null", setOf(BurbAbility.NULL)),
    PLANTS_SCOUT_ABILITIES("Peashooter Abilities", setOf(BurbAbility.PLANTS_SCOUT_ABILITY_1, BurbAbility.PLANTS_SCOUT_ABILITY_2)),
    PLANTS_HEAVY_ABILITIES("Chomper Abilities", setOf(BurbAbility.PLANTS_HEAVY_ABILITY_1)),
    PLANTS_HEALER_ABILITIES("Sunflower Abilities", setOf(BurbAbility.PLANTS_HEALER_ABILITY_1)),
    PLANTS_RANGED_ABILITIES("Cactus Abilities", setOf(BurbAbility.PLANTS_RANGED_ABILITY_1)),
    ZOMBIES_SCOUT_ABILITIES("Foot Soldier Abilities", setOf(BurbAbility.ZOMBIES_SCOUT_ABILITY_1)),
    ZOMBIES_HEAVY_ABILITIES("Super Brainz Abilities", setOf(BurbAbility.ZOMBIES_HEAVY_ABILITY_1)),
    ZOMBIES_HEALER_ABILITIES("Scientist Abilities", setOf(BurbAbility.ZOMBIES_HEALER_ABILITY_1)),
    ZOMBIES_RANGED_ABILITIES("Deadbeard Abilities", setOf(BurbAbility.ZOMBIES_RANGED_ABILITY_1))
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