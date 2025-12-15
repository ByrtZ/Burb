package dev.byrt.burb.item

import dev.byrt.burb.player.BurbCharacter
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.plugin
import dev.byrt.burb.team.Teams
import dev.byrt.burb.text.ChatUtility
import dev.byrt.burb.text.Formatting
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.entity.Snowball
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
        if(player.burbPlayer().playerTeam !in listOf(Teams.PLANTS, Teams.ZOMBIES)) return
        if(player.burbPlayer().isDead) {
            clearItems(player)
            return
        }
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
            mainWeaponMeta.isUnbreakable = true
            // TODO: Set melee weapon damage
            // mainWeaponMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE, AttributeModifier(NamespacedKey.minecraft("generic.attack_damage"), burbPlayerCharacter.characterMainWeapon.weaponDamage, AttributeModifier.Operation.ADD_NUMBER))
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

        mainWeaponMeta.itemModel = NamespacedKey("minecraft", burbPlayerCharacter.characterMainWeapon.itemModel)

        mainWeapon.itemMeta = mainWeaponMeta
        burbPlayer.getBukkitPlayer().inventory.addItem(mainWeapon)

        // If melee main weapon, add opposing hand display weapon.
        if(burbPlayerCharacter.characterMainWeapon.weaponType == BurbMainWeaponType.MELEE) {
            if(burbPlayerCharacter == BurbCharacter.ZOMBIES_HEAVY) {
                val offhandItemMeta = mainWeapon.itemMeta
                offhandItemMeta.itemModel = NamespacedKey("minecraft", "melee_gloves_r")
                mainWeapon.itemMeta = offhandItemMeta
                burbPlayer.getBukkitPlayer().inventory.setItemInOffHand(mainWeapon)
            } else {
                burbPlayer.getBukkitPlayer().inventory.setItemInOffHand(mainWeapon)
            }
        }

        for(ability in burbPlayerCharacter.characterAbilities.abilitySet) {
            val abilityItem = ItemStack(ability.abilityMaterial, 1)
            val abilityItemMeta = abilityItem.itemMeta
            abilityItemMeta.setJukeboxPlayable(null)
            abilityItemMeta.persistentDataContainer.set(NamespacedKey(plugin, "burb.ability.id"), PersistentDataType.STRING, ability.abilityId)
            abilityItemMeta.persistentDataContainer.set(NamespacedKey(plugin, "burb.ability.cooldown"), PersistentDataType.INTEGER, ability.abilityCooldown)
            abilityItemMeta.displayName(Formatting.allTags.deserialize("<${ItemRarity.COMMON.rarityColour}>${if(ability.abilityName == "") ability.abilityId + ".name" else ability.abilityName}").decoration(TextDecoration.ITALIC, false))
            abilityItemMeta.lore(listOf(
                    Formatting.allTags.deserialize("<white>${ItemRarity.COMMON.rarityGlyph}${ItemType.UTILITY.typeGlyph}").decoration(TextDecoration.ITALIC, false),
                    Formatting.allTags.deserialize("<white>${if(ability.abilityLore == "") ability.abilityId + ".lore" else ability.abilityLore}").decoration(TextDecoration.ITALIC, false)
                )
            )
            abilityItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES)
            abilityItemMeta.itemModel = NamespacedKey("minecraft", ability.abilityModel)
            abilityItem.itemMeta = abilityItemMeta
            burbPlayer.getBukkitPlayer().inventory.addItem(abilityItem)
        }
    }

    fun clearItems(player: Player) {
        player.inventory.setItemInMainHand(null)
        player.inventory.setItemInOffHand(null)
        player.inventory.remove(Material.RED_DYE)
        player.inventory.remove(Material.ORANGE_DYE)
        player.inventory.remove(Material.YELLOW_DYE)
        player.inventory.remove(Material.POPPED_CHORUS_FRUIT)
        player.inventory.remove(Material.WOODEN_SWORD)
        player.inventory.remove(Material.BREEZE_ROD)
        player.inventory.remove(ServerItem.getProfileItem())
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

    fun verifyAbility(item: ItemStack): Boolean {
        return (item.persistentDataContainer.has(NamespacedKey(plugin, "burb.ability.id"))
                && item.persistentDataContainer.has(NamespacedKey(plugin, "burb.ability.cooldown"))
                )
    }

    fun destroyBullets() {
        for(world in Bukkit.getWorlds()) {
            for(snowball in world.getEntitiesByClass(Snowball::class.java)) {
                snowball.remove()
            }
        }
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
enum class BurbCharacterMainWeapon(val weaponName: String, val weaponLore: String, val weaponType: BurbMainWeaponType, val weaponDamage: Double, val fireRate: Int, val reloadSpeed: Int, val maxAmmo: Int, val projectileVelocity: Double, val weaponMaterial: Material, val useSound: String, val itemModel: String) {
    NULL("null", "null", BurbMainWeaponType.NULL, 0.0, 0, 0,0, 0.0, Material.AIR, "null", "null"),
    PLANTS_SCOUT_MAIN("Pea Cannon", "Shoots heavy hitting peas.", BurbMainWeaponType.RIFLE,3.25, 12, 50, 12, 1.95, Material.POPPED_CHORUS_FRUIT, "burb.weapon.peashooter.fire","pea_cannon"),
    PLANTS_HEAVY_MAIN("Chomp", "Sharp chomper fangs.", BurbMainWeaponType.MELEE,4.0, 0, 0, 0, 0.0, Material.WOODEN_SWORD, "burb.weapon.chomper.fire","chomper_fangs"),
    PLANTS_HEALER_MAIN("Sun Pulse", "Shoots bolts of light.", BurbMainWeaponType.RIFLE,0.65, 3, 65, 35, 2.75, Material.POPPED_CHORUS_FRUIT, "burb.weapon.sunflower.fire","sunflower_weapon"),
    PLANTS_RANGED_MAIN("Spike Shot", "Shoots accurate cactus pines.", BurbMainWeaponType.RIFLE,5.0, 18, 60, 10, 4.5, Material.POPPED_CHORUS_FRUIT, "burb.weapon.cactus.fire","spike_shot"),
    ZOMBIES_SCOUT_MAIN("Z-1 Assault Blaster", "Shoots Z1 pellets.", BurbMainWeaponType.RIFLE,1.0, 3, 55, 25, 2.25, Material.POPPED_CHORUS_FRUIT, "burb.weapon.foot_soldier.fire","blaster"),
    ZOMBIES_HEAVY_MAIN("Heroic Fists", "Super Brainz' powerful fists.", BurbMainWeaponType.MELEE,4.0, 0, 0, 0, 0.0, Material.WOODEN_SWORD, "entity.player.attack.knockback","melee_gloves_l"),
    ZOMBIES_HEALER_MAIN("Goo Blaster", "Shoots yucky clumps of goo.", BurbMainWeaponType.SHOTGUN,0.85, 20, 65, 5, 1.5, Material.POPPED_CHORUS_FRUIT, "burb.weapon.scientist.fire","goo_blaster"),
    ZOMBIES_RANGED_MAIN("Spyglass Shot", "Shoots accurate glass shards.", BurbMainWeaponType.RIFLE,6.0, 25, 75, 6, 4.0, Material.POPPED_CHORUS_FRUIT, "block.glass.break","spyglass_shot")
}

enum class BurbMainWeaponType(val weaponTypeName: String) {
    NULL("null"),
    RIFLE("Rifle"),
    SHOTGUN("Shotgun"),
    MELEE("Melee")
}

enum class BurbAbility(val abilityName: String, val abilityLore: String, val abilityId: String, val abilityModel: String, val abilityMaterial: Material, val abilityCooldown: Int) {
    NULL("null", "null", "null", "null", Material.AIR, 0),

    /** Chilli Bean Bomb **/
    PLANTS_SCOUT_ABILITY_1("Chilli Bean Bomb", "A chilli bean with a short temper.","burb.character.plants_scout.ability.1", "red_dye",  Material.RED_DYE, 500),
    /** Pea Gatling **/
    PLANTS_SCOUT_ABILITY_2("Pea Gatling", "Line 'em up and knock 'em down.", "burb.character.plants_scout.ability.2", "orange_dye",  Material.ORANGE_DYE, 700),
    /** Hyper **/
    PLANTS_SCOUT_ABILITY_3("Hyper", "ZOOMIES!", "burb.character.plants_scout.ability.3", "yellow_dye",  Material.YELLOW_DYE, 400),

    /** Goop **/
    PLANTS_HEAVY_ABILITY_1("Goop", "Sticky goop that slows enemies.", "burb.character.plants_heavy.ability.1", "red_dye",  Material.RED_DYE, 160),
    /** Burrow **/
    PLANTS_HEAVY_ABILITY_2("Burrow", "Burrow into the ground and leap out.", "burb.character.plants_heavy.ability.2", "orange_dye",  Material.ORANGE_DYE, 200),
    /** Spikeweed **/
    PLANTS_HEAVY_ABILITY_3("Spikeweed", "Snare the zombies.", "burb.character.plants_heavy.ability.3", "yellow_dye",  Material.YELLOW_DYE, 120),

    /** Heal Beam **/
    PLANTS_HEALER_ABILITY_1("Heal Beam", "", "burb.character.plants_healer.ability.1", "red_dye",  Material.RED_DYE, 40),
    /** Sunbeam **/
    PLANTS_HEALER_ABILITY_2("Sunbeam", "", "burb.character.plants_healer.ability.2", "orange_dye",  Material.ORANGE_DYE, 550),
    /** Heal Flower **/
    PLANTS_HEALER_ABILITY_3("Heal Flower", "", "burb.character.plants_healer.ability.3", "yellow_dye",  Material.YELLOW_DYE, 400),

    /** Potato Mine **/
    PLANTS_RANGED_ABILITY_1("Potato Mine", "So cute, yet so deadly.", "burb.character.plants_ranged.ability.1", "red_dye",  Material.RED_DYE, 200),
    /** Garlic Drone **/
    PLANTS_RANGED_ABILITY_2("Garlic Drone", "", "burb.character.plants_ranged.ability.2", "orange_dye",  Material.ORANGE_DYE, 475),
    /** Tallnut Battlement **/
    PLANTS_RANGED_ABILITY_3("Tallnut Battlement", "Create your own cover.", "burb.character.plants_ranged.ability.3", "yellow_dye",  Material.YELLOW_DYE, 300),

    /** Zombie Stink Cloud **/
    ZOMBIES_SCOUT_ABILITY_1("Zombie Stink Cloud", "Whoever smelt it, dealt it.", "burb.character.zombies_scout.ability.1", "footsoldier_ability_stink_cloud",  Material.RED_DYE, 350),
    /** ZPG **/
    ZOMBIES_SCOUT_ABILITY_2("ZPG", "Who gave this zombie a rocket?", "burb.character.zombies_scout.ability.2", "footsoldier_ability_zpg",  Material.ORANGE_DYE, 675),
    /** Rocket Jump **/
    ZOMBIES_SCOUT_ABILITY_3("Rocket Jump", "I have the high ground.", "burb.character.zombies_scout.ability.3", "footsoldier_ability_rocket_jump",  Material.YELLOW_DYE, 450),

    /** Super Ultra Ball **/
    ZOMBIES_HEAVY_ABILITY_1("Super Ultra Ball", "", "burb.character.zombies_heavy.ability.1", "red_dye",  Material.RED_DYE, 450),
    /** Turbo Twister **/
    ZOMBIES_HEAVY_ABILITY_2("Turbo Twister", "", "burb.character.zombies_heavy.ability.2", "orange_dye",  Material.ORANGE_DYE, 700),
    /** Heroic Kick **/
    ZOMBIES_HEAVY_ABILITY_3("Heroic Kick", "Strangely powerful toes.", "burb.character.zombies_heavy.ability.3", "yellow_dye",  Material.YELLOW_DYE, 250),

    /** Heal Beam of Science **/
    ZOMBIES_HEALER_ABILITY_1("Heal Beam of Science", "", "burb.character.zombies_healer.ability.1", "red_dye",  Material.RED_DYE, 40),
    /** Warp **/
    ZOMBIES_HEALER_ABILITY_2("Warp", "Transcend time and space, a few blocks forward.", "burb.character.zombies_healer.ability.2", "orange_dye",  Material.ORANGE_DYE, 175),
    /** Sticky Explody Ball **/
    ZOMBIES_HEALER_ABILITY_3("Sticky Explody Ball", "", "burb.character.zombies_healer.ability.3", "yellow_dye",  Material.YELLOW_DYE, 275),

    /** Barrel Blast **/
    ZOMBIES_RANGED_ABILITY_1("Barrel Blast", "", "burb.character.zombies_ranged.ability.1", "red_dye",  Material.RED_DYE, 750),
    /** Parrot Pal **/
    ZOMBIES_RANGED_ABILITY_2("Parrot Pal", "", "burb.character.zombies_ranged.ability.2", "orange_dye",  Material.ORANGE_DYE, 575),
    /** Cannon Rodeo **/
    ZOMBIES_RANGED_ABILITY_3("Cannon Rodeo", "", "burb.character.zombies_ranged.ability.3", "yellow_dye",  Material.YELLOW_DYE, 625)
}

enum class BurbCharacterAbilities(val abilitiesName: String, val abilitySet: Set<BurbAbility>) {
    NULL("null", setOf(BurbAbility.NULL)),
    PLANTS_SCOUT_ABILITIES("Peashooter Abilities", setOf(BurbAbility.PLANTS_SCOUT_ABILITY_1, BurbAbility.PLANTS_SCOUT_ABILITY_2, BurbAbility.PLANTS_SCOUT_ABILITY_3)),
    PLANTS_HEAVY_ABILITIES("Chomper Abilities", setOf(BurbAbility.PLANTS_HEAVY_ABILITY_1, BurbAbility.PLANTS_HEAVY_ABILITY_2, BurbAbility.PLANTS_HEAVY_ABILITY_3)),
    PLANTS_HEALER_ABILITIES("Sunflower Abilities", setOf(BurbAbility.PLANTS_HEALER_ABILITY_1, BurbAbility.PLANTS_HEALER_ABILITY_2, BurbAbility.PLANTS_HEALER_ABILITY_3)),
    PLANTS_RANGED_ABILITIES("Cactus Abilities", setOf(BurbAbility.PLANTS_RANGED_ABILITY_1, BurbAbility.PLANTS_RANGED_ABILITY_2, BurbAbility.PLANTS_RANGED_ABILITY_3)),
    ZOMBIES_SCOUT_ABILITIES("Foot Soldier Abilities", setOf(BurbAbility.ZOMBIES_SCOUT_ABILITY_1, BurbAbility.ZOMBIES_SCOUT_ABILITY_2, BurbAbility.ZOMBIES_SCOUT_ABILITY_3)),
    ZOMBIES_HEAVY_ABILITIES("Super Brainz Abilities", setOf(BurbAbility.ZOMBIES_HEAVY_ABILITY_1, BurbAbility.ZOMBIES_HEAVY_ABILITY_2, BurbAbility.ZOMBIES_HEAVY_ABILITY_3)),
    ZOMBIES_HEALER_ABILITIES("Scientist Abilities", setOf(BurbAbility.ZOMBIES_HEALER_ABILITY_1, BurbAbility.ZOMBIES_HEALER_ABILITY_2, BurbAbility.ZOMBIES_HEALER_ABILITY_3)),
    ZOMBIES_RANGED_ABILITIES("Deadbeard Abilities", setOf(BurbAbility.ZOMBIES_RANGED_ABILITY_1, BurbAbility.ZOMBIES_RANGED_ABILITY_2, BurbAbility.ZOMBIES_RANGED_ABILITY_3))
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
    WEAPON("Weapon", "\uF013"),
    HAT("Hat", "\uF015"),
    ACCESSORY("Accessory", "\uF014")
}