package dev.byrt.burb.event

import dev.byrt.burb.chat.ChatUtility
import dev.byrt.burb.chat.Formatting
import dev.byrt.burb.item.ItemManager
import dev.byrt.burb.item.ItemRarity
import dev.byrt.burb.item.ItemType
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.plugin

import net.kyori.adventure.text.format.TextDecoration

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Snowball
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable

@Suppress("unused")
class InteractEvent: Listener {
    @EventHandler
    private fun onInteract(e: PlayerInteractEvent) {
        if(e.player.inventory.itemInMainHand.type == Material.POPPED_CHORUS_FRUIT && e.action.isRightClick && !e.player.hasCooldown(Material.POPPED_CHORUS_FRUIT)) {
            val usedItem = e.player.inventory.itemInMainHand
            // Verify item if it has all necessary data to be used
            if(ItemManager.verifyItem(usedItem)) {
                val burbPlayer = e.player.burbPlayer()

                // Ammo decrement
                if(usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.current_ammo"), PersistentDataType.INTEGER)!! <= 1) {
                    object : BukkitRunnable() {
                        override fun run() {
                            val newAmmoMeta = usedItem.itemMeta
                            newAmmoMeta.lore(
                                listOf(
                                    Formatting.allTags.deserialize("<white>${ItemRarity.COMMON.rarityGlyph}${ItemType.WEAPON.typeGlyph}").decoration(TextDecoration.ITALIC, false),
                                    Formatting.allTags.deserialize("<white>Damage: <yellow>${burbPlayer.playerCharacter.characterMainWeapon.weaponDamage}<red>${ChatUtility.HEART_UNICODE}<reset>").decoration(TextDecoration.ITALIC, false),
                                    Formatting.allTags.deserialize("<white>Ammo: <green>${usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.max_ammo"), PersistentDataType.INTEGER)}<gray>/<yellow>${burbPlayer.playerCharacter.characterMainWeapon.maxAmmo}<reset>").decoration(TextDecoration.ITALIC, false),
                                    Formatting.allTags.deserialize("<white>Fire Rate: <yellow>${burbPlayer.playerCharacter.characterMainWeapon.fireRate}t<reset>").decoration(TextDecoration.ITALIC, false),
                                    Formatting.allTags.deserialize("<white>Reload Speed: <yellow>${burbPlayer.playerCharacter.characterMainWeapon.reloadSpeed}t<reset>").decoration(TextDecoration.ITALIC, false),
                                    Formatting.allTags.deserialize("<white>${burbPlayer.playerCharacter.characterMainWeapon.weaponLore}").decoration(TextDecoration.ITALIC, false)
                                )
                            )
                            newAmmoMeta.persistentDataContainer.set(NamespacedKey(plugin, "burb.weapon.current_ammo"), PersistentDataType.INTEGER, usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.max_ammo"), PersistentDataType.INTEGER)!!)
                            usedItem.itemMeta = newAmmoMeta
                        }
                    }.runTaskLater(plugin, usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.reload_speed"), PersistentDataType.INTEGER)!!.toLong())
                    e.player.setCooldown(Material.POPPED_CHORUS_FRUIT, usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.reload_speed"), PersistentDataType.INTEGER)!!)
                } else {
                    val newAmmoMeta = usedItem.itemMeta
                    newAmmoMeta.lore(
                        listOf(
                            Formatting.allTags.deserialize("<white>${ItemRarity.COMMON.rarityGlyph}${ItemType.WEAPON.typeGlyph}").decoration(TextDecoration.ITALIC, false),
                            Formatting.allTags.deserialize("<white>Damage: <yellow>${burbPlayer.playerCharacter.characterMainWeapon.weaponDamage}<red>${ChatUtility.HEART_UNICODE}<reset>").decoration(TextDecoration.ITALIC, false),
                            Formatting.allTags.deserialize("<white>Ammo: <yellow>${usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.current_ammo"), PersistentDataType.INTEGER)!! - 1}<gray>/<yellow>${burbPlayer.playerCharacter.characterMainWeapon.maxAmmo}<reset>").decoration(TextDecoration.ITALIC, false),
                            Formatting.allTags.deserialize("<white>Fire Rate: <yellow>${burbPlayer.playerCharacter.characterMainWeapon.fireRate}t<reset>").decoration(TextDecoration.ITALIC, false),
                            Formatting.allTags.deserialize("<white>Reload Speed: <yellow>${burbPlayer.playerCharacter.characterMainWeapon.reloadSpeed}t<reset>").decoration(TextDecoration.ITALIC, false),
                            Formatting.allTags.deserialize("<white>${burbPlayer.playerCharacter.characterMainWeapon.weaponLore}").decoration(TextDecoration.ITALIC, false)
                        )
                    )
                    newAmmoMeta.persistentDataContainer.set(NamespacedKey(plugin, "burb.weapon.current_ammo"), PersistentDataType.INTEGER, usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.current_ammo"), PersistentDataType.INTEGER)!! - 1)
                    usedItem.itemMeta = newAmmoMeta
                    // Fire rate
                    e.player.setCooldown(Material.POPPED_CHORUS_FRUIT, usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.fire_rate"), PersistentDataType.INTEGER)!!)
                }
                val snowball = e.player.world.spawn(e.player.eyeLocation, Snowball::class.java)
                snowball.shooter = e.player

                // Projectile velocity
                val snowballVelocity = e.player.location.direction.multiply(usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.projectile_velocity"), PersistentDataType.DOUBLE)!!)
                snowball.velocity = snowballVelocity

                // Projectile damage
                usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.damage"), PersistentDataType.DOUBLE)?.let { snowball.persistentDataContainer.set(NamespacedKey(plugin, "burb.weapon.damage"), PersistentDataType.DOUBLE, it) }
                e.player.playSound(Sounds.Weapon.FOOT_SOLDIER_WEAPON_FIRE)
            }
        }
    }
}