package dev.byrt.burb.item

import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.item.rarity.ItemRarity
import dev.byrt.burb.item.type.ItemType
import dev.byrt.burb.item.weapon.BurbMainWeaponType
import dev.byrt.burb.player.BurbCharacter
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.plugin
import dev.byrt.burb.team.Teams
import dev.byrt.burb.text.ChatUtility
import dev.byrt.burb.text.Formatting

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

object ItemManager {
    fun givePlayerTeamBoots(player: Player, team: Teams) {
        val teamBoots = ItemStack(Material.LEATHER_BOOTS)
        val teamBootsMeta = teamBoots.itemMeta
        val teamBootsRarity = if(player.isOp) ItemRarity.SPECIAL else ItemRarity.COMMON
        val teamBootsType = ItemType.ARMOUR
        teamBootsMeta.displayName(Formatting.allTags.deserialize("<!i><${teamBootsRarity.rarityColour}>${team.teamName} Team Boots"))
        val teamBootsLore = listOf(
            Formatting.allTags.deserialize("<!i><white>${teamBootsRarity.asMiniMesssage()}${teamBootsType.asMiniMesssage()}"),
            Formatting.allTags.deserialize("<!i><white>A snazzy pair of ${team.teamName} team's boots.")
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
        val burbPlayer = player.burbPlayer()
        val burbPlayerCharacter = burbPlayer.playerCharacter
        clearItems(player)
        if(player.burbPlayer().isDead) return

        val mainWeapon = ItemStack(burbPlayerCharacter.characterMainWeapon.weaponMaterial, 1)
        val mainWeaponMeta = mainWeapon.itemMeta
        mainWeaponMeta.displayName(Formatting.allTags.deserialize("<!i><${ItemRarity.COMMON.rarityColour}>${burbPlayerCharacter.characterMainWeapon.weaponName}"))
        if(burbPlayerCharacter.characterMainWeapon.weaponType == BurbMainWeaponType.MELEE) {
            mainWeaponMeta.lore(listOf(
                Formatting.allTags.deserialize("<!i><white>${ItemRarity.COMMON.asMiniMesssage()}${ItemType.WEAPON.asMiniMesssage()}"),
                Formatting.allTags.deserialize("<!i><white>Damage: <yellow>${burbPlayerCharacter.characterMainWeapon.weaponDamage}<red>${ChatUtility.HEART_UNICODE}<reset>"),
                Formatting.allTags.deserialize("<!i><white>${burbPlayerCharacter.characterMainWeapon.weaponLore}")
            ))
            mainWeaponMeta.isUnbreakable = true
        } else {
            mainWeaponMeta.lore(listOf(
                Formatting.allTags.deserialize("<!i><white>${ItemRarity.COMMON.asMiniMesssage()}${ItemType.WEAPON.asMiniMesssage()}"),
                Formatting.allTags.deserialize("<!i><white>Damage: <yellow>${burbPlayerCharacter.characterMainWeapon.weaponDamage}<red>${ChatUtility.HEART_UNICODE}<reset>"),
                Formatting.allTags.deserialize("<!i><white>Ammo: <green>${burbPlayerCharacter.characterMainWeapon.maxAmmo}<gray>/<yellow>${burbPlayerCharacter.characterMainWeapon.maxAmmo}<reset>"),
                Formatting.allTags.deserialize("<!i><white>Fire Rate: <yellow>${burbPlayerCharacter.characterMainWeapon.fireRate}t<reset>"),
                Formatting.allTags.deserialize("<!i><white>Reload Speed: <yellow>${burbPlayerCharacter.characterMainWeapon.reloadSpeed}t<reset>"),
                Formatting.allTags.deserialize("<!i><white>${burbPlayerCharacter.characterMainWeapon.weaponLore}")
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
        } else {
            mainWeaponMeta.persistentDataContainer.set(NamespacedKey(plugin,"burb.weapon.current_ammo"), PersistentDataType.INTEGER, 0)
            mainWeaponMeta.persistentDataContainer.set(NamespacedKey(plugin,"burb.weapon.max_ammo"), PersistentDataType.INTEGER, 0)
            mainWeaponMeta.persistentDataContainer.set(NamespacedKey(plugin,"burb.weapon.fire_rate"), PersistentDataType.INTEGER, 0)
            mainWeaponMeta.persistentDataContainer.set(NamespacedKey(plugin,"burb.weapon.reload_speed"), PersistentDataType.INTEGER, 0)
            mainWeaponMeta.persistentDataContainer.set(NamespacedKey(plugin,"burb.weapon.projectile_velocity"), PersistentDataType.DOUBLE, 0.0)
        }

        mainWeaponMeta.itemModel = NamespacedKey("minecraft", burbPlayerCharacter.characterMainWeapon.itemModel)

        mainWeapon.itemMeta = mainWeaponMeta
        burbPlayer.bukkitPlayer().inventory.setItem(4, mainWeapon)

        // If melee main weapon, add opposing hand display weapon.
        if(burbPlayerCharacter.characterMainWeapon.weaponType == BurbMainWeaponType.MELEE) {
            if(burbPlayerCharacter == BurbCharacter.ZOMBIES_HEAVY) {
                val offhandItemMeta = mainWeapon.itemMeta
                offhandItemMeta.itemModel = NamespacedKey("minecraft", "melee_gloves_r")
                mainWeapon.itemMeta = offhandItemMeta
                burbPlayer.bukkitPlayer().inventory.setItemInOffHand(mainWeapon)
            } else {
                burbPlayer.bukkitPlayer().inventory.setItemInOffHand(mainWeapon)
            }
        }

        // Give profile item if game is IDLE
        if(GameManager.getGameState() == GameState.IDLE) player.inventory.setItem(8, ServerItem.getProfileItem())
    }

    fun clearItems(player: Player) {
        player.inventory.setItemInMainHand(null)
        player.inventory.setItemInOffHand(null)
        player.inventory.remove(Material.POPPED_CHORUS_FRUIT)
        player.inventory.remove(Material.WOODEN_SWORD)
        player.inventory.remove(Material.BREEZE_ROD)
        player.inventory.remove(Material.FISHING_ROD)
        player.inventory.remove(ServerItem.getProfileItem())
    }

    fun verifyMainWeapon(item: ItemStack):  Boolean {
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