package dev.byrt.burb.item

import dev.byrt.burb.chat.ChatUtility
import dev.byrt.burb.chat.Formatting
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.player.PlayerVisuals
import dev.byrt.burb.plugin
import dev.byrt.burb.team.Teams

import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.title.Title

import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.entity.Snowball
import org.bukkit.entity.TNTPrimed
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

import java.time.Duration

import kotlin.random.Random

object ItemUsage {
    fun useProjectileWeapon(player: Player, usedItem: ItemStack) {
        // Verify item if it has all necessary data to be used
        if(ItemManager.verifyItem(usedItem)) {
            val burbPlayer = player.burbPlayer()
            // Ammo decrement
            if(usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.current_ammo"), PersistentDataType.INTEGER)!! <= 1) {
                reloadProjectileWeapon(player, usedItem)
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
                player.setCooldown(Material.POPPED_CHORUS_FRUIT, usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.fire_rate"), PersistentDataType.INTEGER)!!)
            }
            for(bullets in 0..if(player.burbPlayer().playerCharacter.characterMainWeapon.weaponType == BurbMainWeaponType.SHOTGUN) 6 else 0) {
                val snowball = player.world.spawn(player.eyeLocation.clone().add(Random.nextDouble(-0.25, 0.25), Random.nextDouble(-0.25, 0.25), Random.nextDouble(-0.25, 0.25)), Snowball::class.java)
                snowball.shooter = player
                // Projectile velocity
                val snowballVelocity = player.location.direction.multiply(usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.projectile_velocity"), PersistentDataType.DOUBLE)!!)
                snowball.velocity = snowballVelocity
                // Offset projectile direction
                snowball.location.direction = snowball.location.direction.add(Vector(Random.nextDouble(-0.25, 0.25), Random.nextDouble(-0.25, 0.25), Random.nextDouble(-0.25, 0.25)))
                // Projectile damage
                usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.damage"), PersistentDataType.DOUBLE)?.let { snowball.persistentDataContainer.set(NamespacedKey(plugin, "burb.weapon.damage"), PersistentDataType.DOUBLE, it) }
                // Projectile trail
                object : BukkitRunnable() {
                    val shooter = player
                    override fun run() {
                        if(snowball.isDead || !player.isOnline) {
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
            player.world.playSound(player.location, usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.sound"), PersistentDataType.STRING).toString(), 0.75f, 1f)
        }
    }

    fun reloadProjectileWeapon(player: Player, usedItem: ItemStack) {
        val burbPlayer = player.burbPlayer()
        // Ammo decrement
        if(usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.current_ammo"), PersistentDataType.INTEGER)!! < usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.max_ammo"), PersistentDataType.INTEGER)!!) {
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
            player.setCooldown(Material.POPPED_CHORUS_FRUIT, usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.reload_speed"), PersistentDataType.INTEGER)!!)
            PlayerVisuals.reloadWeapon(player, usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.reload_speed"), PersistentDataType.INTEGER)!!)
        }
    }

    fun useMeleeWeapon(player: Player, usedItem: ItemStack) {
        // Use sound
        player.world.playSound(player.location, usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.sound"), PersistentDataType.STRING).toString(), 1f, 1f)
    }

    fun useAbility(player: Player, usedItem: ItemStack) {
        val abilityId = usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.ability.id"), PersistentDataType.STRING)!!
        when(abilityId) {
            BurbAbility.PLANTS_SCOUT_ABILITY_1.abilityId -> {
                player.setCooldown(Material.DISC_FRAGMENT_5, 500)
                player.world.playSound(player.location, "burb.weapon.peashooter.ability.explosive.fire", SoundCategory.VOICE, 1f, 1f)
                val tnt = player.world.spawn(player.eyeLocation, TNTPrimed::class.java)
                tnt.source = player
                val tntVelocity = player.location.direction.multiply(1.15)
                tnt.velocity = tntVelocity
                tnt.fuseTicks = Int.MAX_VALUE
                object : BukkitRunnable() {
                    override fun run() {
                        if(tnt.isOnGround && tnt.velocity.x <= 0.025 && tnt.velocity.y <= 0.025 && tnt.velocity.z <= 0.025) {
                            tnt.fuseTicks = 90
                            player.world.playSound(tnt.location, "burb.weapon.peashooter.ability.explosive.voice", SoundCategory.VOICE, 2.5f, 1f)
                            object : BukkitRunnable() {
                                override fun run() {
                                    player.world.playSound(tnt.location, "burb.weapon.peashooter.ability.explosive.explode", SoundCategory.VOICE, 3f, 1f)
                                }
                            }.runTaskLater(plugin, 90L)
                            this.cancel()
                        }
                    }
                }.runTaskTimer(plugin, 0L, 1L)
            }
            BurbAbility.PLANTS_SCOUT_ABILITY_3.abilityId -> {
                player.setCooldown(Material.DISC_FRAGMENT_5, 450)
                player.world.playSound(player.location, "burb.weapon.peashooter.ability.zoom.use", SoundCategory.VOICE, 2f, 1f)
                player.addPotionEffects(listOf(
                    PotionEffect(PotionEffectType.JUMP_BOOST, 20 * 12, 6, false, true),
                    PotionEffect(PotionEffectType.SPEED, 20 * 12, 6, false, true)
                ))
            }
            BurbAbility.ZOMBIES_SCOUT_ABILITY_3.abilityId -> {
                player.setCooldown(Material.DISC_FRAGMENT_5, 400)
                player.world.playSound(player.location, "entity.breeze.shoot", SoundCategory.VOICE, 1f, 0.75f)
                player.velocity = player.velocity.setY(1.3)
            }
            BurbAbility.ZOMBIES_HEALER_ABILITY_2.abilityId -> {
                player.setCooldown(Material.DISC_FRAGMENT_5, 300)
                val block = player.getTargetBlock(null, 12)
                val location = block.location
                val pitch = player.eyeLocation.pitch
                val yaw = player.eyeLocation.yaw
                location.add(0.5, 1.0, 0.5)
                location.yaw = yaw
                location.pitch = pitch
                player.teleport(location)
                player.world.playSound(player.location, "entity.enderman.teleport", SoundCategory.VOICE, 1f, 0.75f)
            }
        }
    }
}
