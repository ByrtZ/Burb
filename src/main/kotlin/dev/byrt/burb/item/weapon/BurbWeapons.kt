package dev.byrt.burb.item.weapon

import dev.byrt.burb.item.ItemManager
import dev.byrt.burb.item.rarity.ItemRarity
import dev.byrt.burb.item.type.ItemType
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.player.PlayerVisuals
import dev.byrt.burb.plugin
import dev.byrt.burb.team.Teams
import dev.byrt.burb.text.ChatUtility
import dev.byrt.burb.text.Formatting
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.entity.Snowball
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import kotlin.random.Random

object BurbWeapons {
    fun useProjectileWeapon(player: Player, usedItem: ItemStack) {
        // Verify item if it has all necessary data to be used
        if(ItemManager.verifyMainWeapon(usedItem)) {
            val burbPlayer = player.burbPlayer()
            // Ammo decrement
            if(usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.current_ammo"), PersistentDataType.INTEGER)!! <= 1) {
                reloadProjectileWeapon(player, usedItem)
            } else {
                val newAmmoMeta = usedItem.itemMeta
                newAmmoMeta.lore(
                    listOf(
                        Formatting.allTags.deserialize("<!i><white>${ItemRarity.COMMON.asMiniMesssage()}${ItemType.WEAPON.asMiniMesssage()}"),
                        Formatting.allTags.deserialize("<!i><white>Damage: <yellow>${burbPlayer.playerCharacter.characterMainWeapon.weaponDamage}<red>${ChatUtility.HEART_UNICODE}<reset>"),
                        Formatting.allTags.deserialize("<!i><white>Ammo: <yellow>${usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.current_ammo"), PersistentDataType.INTEGER)!! - 1}<gray>/<yellow>${burbPlayer.playerCharacter.characterMainWeapon.maxAmmo}<reset>"),
                        Formatting.allTags.deserialize("<!i><white>Fire Rate: <yellow>${burbPlayer.playerCharacter.characterMainWeapon.fireRate}t<reset>"),
                        Formatting.allTags.deserialize("<!i><white>Reload Speed: <yellow>${burbPlayer.playerCharacter.characterMainWeapon.reloadSpeed}t<reset>"),
                        Formatting.allTags.deserialize("<!i><white>${burbPlayer.playerCharacter.characterMainWeapon.weaponLore}")
                    )
                )
                newAmmoMeta.persistentDataContainer.set(NamespacedKey(plugin, "burb.weapon.current_ammo"), PersistentDataType.INTEGER, usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.current_ammo"), PersistentDataType.INTEGER)!! - 1)
                usedItem.itemMeta = newAmmoMeta
                // Fire rate
                player.setCooldown(Material.POPPED_CHORUS_FRUIT, usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.fire_rate"), PersistentDataType.INTEGER)!!)
            }
            for(bullets in 0..if(player.burbPlayer().playerCharacter.characterMainWeapon.weaponType == BurbMainWeaponType.SHOTGUN) 6 else 0) {
                val snowball = player.world.spawn(player.eyeLocation.clone(), Snowball::class.java)
                snowball.shooter = player
                snowball.location.direction = player.location.direction
                // Projectile velocity
                val snowballVelocity = player.location.direction.multiply(usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.projectile_velocity"), PersistentDataType.DOUBLE)!!)
                snowball.velocity = snowballVelocity
                // Projectile bloom
                snowball.velocity = snowball.velocity.add(Vector(Random.nextDouble(-0.08, 0.08), Random.nextDouble(-0.07, 0.07), Random.nextDouble(-0.08, 0.08)))
                // Projectile damage
                usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.damage"), PersistentDataType.DOUBLE)?.let { snowball.persistentDataContainer.set(NamespacedKey(plugin, "burb.weapon.damage"), PersistentDataType.DOUBLE, it) }
                // Projectile trail
                object : BukkitRunnable() {
                    val shooter = player
                    override fun run() {
                        if(snowball.isDead || !player.isOnline) {
                            snowball.remove()
                            this.cancel()
                        } else {
                            snowball.location.world.spawnParticle(
                                Particle.DUST,
                                snowball.location,
                                1, 0.0, 0.0, 0.0, 0.0,
                                Particle.DustOptions(if (shooter.burbPlayer().playerTeam == Teams.PLANTS) Color.LIME else if (shooter.burbPlayer().playerTeam == Teams.ZOMBIES) Color.PURPLE else Color.GRAY, 0.75f),
                                true
                            )
                        }
                    }
                }.runTaskTimer(plugin, 0L, 2L)
            }
            // Use sound
            usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.sound"), PersistentDataType.STRING)?.let { player.world.playSound(player.location, it, 0.75f, 1f) }
        }
    }

    fun reloadProjectileWeapon(player: Player, usedItem: ItemStack) {
        val burbPlayer = player.burbPlayer()
        // Ammo decrement
        if(usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.current_ammo"), PersistentDataType.INTEGER)!! < usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.max_ammo"), PersistentDataType.INTEGER)!!) {
            val reloadCooldown = usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.reload_speed"), PersistentDataType.INTEGER)!!
            object : BukkitRunnable() {
                override fun run() {
                    val newAmmoMeta = usedItem.itemMeta
                    newAmmoMeta.lore(
                        listOf(
                            Formatting.allTags.deserialize("<!i><white>${ItemRarity.COMMON.asMiniMesssage()}${ItemType.WEAPON.asMiniMesssage()}"),
                            Formatting.allTags.deserialize("<!i><white>Damage: <yellow>${burbPlayer.playerCharacter.characterMainWeapon.weaponDamage}<red>${ChatUtility.HEART_UNICODE}<reset>"),
                            Formatting.allTags.deserialize("<!i><white>Ammo: <green>${usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.max_ammo"), PersistentDataType.INTEGER)}<gray>/<yellow>${burbPlayer.playerCharacter.characterMainWeapon.maxAmmo}<reset>"),
                            Formatting.allTags.deserialize("<!i><white>Fire Rate: <yellow>${burbPlayer.playerCharacter.characterMainWeapon.fireRate}t<reset>"),
                            Formatting.allTags.deserialize("<!i><white>Reload Speed: <yellow>${burbPlayer.playerCharacter.characterMainWeapon.reloadSpeed}t<reset>"),
                            Formatting.allTags.deserialize("<!i><white>${burbPlayer.playerCharacter.characterMainWeapon.weaponLore}")
                        )
                    )
                    newAmmoMeta.persistentDataContainer.set(NamespacedKey(plugin, "burb.weapon.current_ammo"), PersistentDataType.INTEGER, usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.max_ammo"), PersistentDataType.INTEGER)!!)
                    usedItem.itemMeta = newAmmoMeta
                }
            }.runTaskLater(plugin, reloadCooldown.toLong())
            player.setCooldown(Material.POPPED_CHORUS_FRUIT, reloadCooldown)
            PlayerVisuals.reloadWeapon(player, reloadCooldown)
        }
    }

    fun useMeleeWeapon(player: Player, usedItem: ItemStack) {
        // Use sound
        usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.sound"), PersistentDataType.STRING)?.let { player.world.playSound(player.location, it, 1f, 1f) }
    }
}

