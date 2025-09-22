package dev.byrt.burb.item

import dev.byrt.burb.text.ChatUtility
import dev.byrt.burb.text.Formatting
import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.library.Translation
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.player.PlayerVisuals
import dev.byrt.burb.plugin
import dev.byrt.burb.team.Teams

import io.papermc.paper.math.Rotation

import net.kyori.adventure.text.format.TextDecoration

import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.block.BlockFace
import org.bukkit.entity.*
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Transformation
import org.bukkit.util.Vector
import org.joml.Vector3f

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
                val snowball = player.world.spawn(player.eyeLocation.clone(), Snowball::class.java) //.add(Random.nextDouble(-0.125, 0.125), Random.nextDouble(-0.125, 0.125), Random.nextDouble(-0.125, 0.125))
                snowball.shooter = player
                snowball.location.direction = player.location.direction
                // Projectile velocity
                val snowballVelocity = player.location.direction.multiply(usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.projectile_velocity"), PersistentDataType.DOUBLE)!!)
                snowball.velocity = snowballVelocity
                // Projectile bloom
                snowball.velocity = snowball.velocity.add(Vector(Random.nextDouble(-0.095, 0.095), Random.nextDouble(-0.075, 0.075), Random.nextDouble(-0.095, 0.095)))
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
        usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.weapon.sound"), PersistentDataType.STRING)?.let { player.world.playSound(player.location, it, 1f, 1f) }
    }

    fun useAbility(player: Player, usedItem: ItemStack) {
        if(ItemManager.verifyAbility(usedItem)) {
            val abilityId = usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.ability.id"), PersistentDataType.STRING)!!
            player.setCooldown(usedItem.type, usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.ability.cooldown"), PersistentDataType.INTEGER)!!)
            when(abilityId) {
                BurbAbility.PLANTS_SCOUT_ABILITY_1.abilityId -> {
                    player.world.playSound(player.location, "burb.ability.peashooter.explosive.fire", SoundCategory.VOICE, 1f, 1f)
                    val tnt = player.world.spawn(player.eyeLocation, TNTPrimed::class.java)
                    tnt.source = player
                    val tntVelocity = player.location.direction.multiply(1.15)
                    tnt.velocity = tntVelocity
                    tnt.fuseTicks = Int.MAX_VALUE
                    object : BukkitRunnable() {
                        override fun run() {
                            if(tnt.isOnGround && tnt.velocity.x <= 0.025 && tnt.velocity.y <= 0.025 && tnt.velocity.z <= 0.025) {
                                tnt.fuseTicks = 90
                                player.world.playSound(tnt.location, "burb.ability.peashooter.explosive.voice", SoundCategory.VOICE, 2.5f, 1f)
                                object : BukkitRunnable() {
                                    override fun run() {
                                        player.world.playSound(tnt.location, "burb.ability.peashooter.explosive.explode", SoundCategory.VOICE, 3f, 1f)
                                    }
                                }.runTaskLater(plugin, 90L)
                                cancel()
                            }
                        }
                    }.runTaskTimer(plugin, 0L, 1L)
                }
                BurbAbility.PLANTS_SCOUT_ABILITY_2.abilityId -> {
                    if(player.location.block.getRelative(BlockFace.DOWN).type != Material.AIR && player.location.block.getRelative(BlockFace.DOWN).isSolid) {
                        player.world.playSound(player.location, "burb.ability.peashooter.gatling.root", SoundCategory.VOICE, 1f, 1f)
                        ItemManager.clearItems(player)
                        player.inventory.setItemInMainHand(ItemStack(Material.BREEZE_ROD, 1))
                        player.inventory.setItem(player.inventory.heldItemSlot + 1, ItemStack(Material.BREEZE_ROD, 1))
                        player.inventory.setItem(player.inventory.heldItemSlot - 1, ItemStack(Material.BREEZE_ROD, 1))
                        object : BukkitRunnable() {
                            var bulletsRemaining = 100
                            val gatlingVehicle = player.location.world.spawn(player.location.clone(), ItemDisplay::class.java).apply {
                                addScoreboardTag("${player.uniqueId}-pea-gatling-vehicle")
                                player.teleport(this)
                                addPassenger(player)
                            }
                            override fun run() {
                                if(!player.isOnline) {
                                    gatlingVehicle.remove()
                                    cancel()
                                }
                                if(player.vehicle == gatlingVehicle) {
                                    player.sendActionBar(Formatting.allTags.deserialize(Translation.Weapon.GATLING_CONTROLS.replace("%s", bulletsRemaining.toString())))
                                    gatlingVehicle.setRotation(player.yaw, player.pitch)
                                }
                                if(player.isSneaking && player.inventory.itemInMainHand.type == Material.BREEZE_ROD) {
                                    val snowball = player.world.spawn(player.eyeLocation.clone(), Snowball::class.java)
                                    snowball.shooter = player
                                    snowball.location.direction = player.location.direction
                                    val snowballVelocity = player.location.direction.multiply(5.0)
                                    snowball.velocity = snowballVelocity
                                    snowball.persistentDataContainer.set(NamespacedKey(plugin, "burb.weapon.damage"), PersistentDataType.DOUBLE, 0.5)
                                    object : BukkitRunnable() {
                                        override fun run() {
                                            if(snowball.isDead || !player.isOnline) {
                                                this.cancel()
                                            } else {
                                                snowball.location.world.spawnParticle(
                                                    Particle.DUST,
                                                    snowball.location,
                                                    1, 0.0, 0.0, 0.0,
                                                    Particle.DustOptions(Color.LIME, 0.75f)
                                                )
                                            }
                                        }
                                    }.runTaskTimer(plugin, 1L, 1L)
                                    player.world.playSound(player.location, "burb.ability.peashooter.gatling.fire", SoundCategory.VOICE, 1f, 1f)
                                    bulletsRemaining--
                                }
                                if(player.inventory.itemInMainHand.type != Material.BREEZE_ROD || player.vehicle != gatlingVehicle || bulletsRemaining <= 0 || GameManager.getGameState() !in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
                                    player.sendActionBar(Formatting.allTags.deserialize(""))
                                    gatlingVehicle.eject()
                                    gatlingVehicle.remove()
                                    player.inventory.remove(Material.BREEZE_ROD)
                                    player.velocity = player.velocity.add(Vector(0.0, 0.5, 0.0))
                                    ItemManager.giveCharacterItems(player)
                                    player.setCooldown(usedItem.type, usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.ability.cooldown"), PersistentDataType.INTEGER)!!)
                                    if(bulletsRemaining == 100) player.setCooldown(BurbAbility.PLANTS_SCOUT_ABILITY_2.abilityMaterial, 0)
                                    player.world.playSound(player.location, "burb.ability.peashooter.gatling.unroot", SoundCategory.VOICE, 1f, 1f)
                                    cancel()
                                }
                            }
                        }.runTaskTimer(plugin, 0L, 1L)
                    } else {
                        player.setCooldown(usedItem, 0)
                        player.sendActionBar(Formatting.allTags.deserialize("<red>You cannot use this ability while in the air."))
                    }
                }
                BurbAbility.PLANTS_SCOUT_ABILITY_3.abilityId -> {
                    player.world.playSound(player.location, "burb.ability.peashooter.zoom.use", SoundCategory.VOICE, 1f, 1f)
                    player.addPotionEffects(listOf(
                        PotionEffect(PotionEffectType.JUMP_BOOST, 20 * 12, 6, false, true),
                        PotionEffect(PotionEffectType.SPEED, 20 * 12, 6, false, true)
                    ))
                }
                BurbAbility.PLANTS_HEAVY_ABILITY_1.abilityId -> {
                    player.world.playSound(player.location, "block.slime_block.place", SoundCategory.VOICE, 1f, 1f)
                    for(bullets in 0..7) {
                        val snowball = player.world.spawn(player.eyeLocation.clone(), Snowball::class.java)
                        snowball.shooter = player
                        snowball.location.direction = player.location.direction
                        // Projectile velocity
                        val snowballVelocity = player.location.direction.multiply(1.75)
                        snowball.velocity = snowballVelocity
                        // Projectile bloom
                        snowball.velocity = snowball.velocity.add(Vector(Random.nextDouble(-0.095, 0.095), Random.nextDouble(-0.075, 0.075), Random.nextDouble(-0.095, 0.095)))
                        // Projectile damage
                        snowball.persistentDataContainer.set(NamespacedKey(plugin, "burb.weapon.damage"), PersistentDataType.DOUBLE, 1.0)
                        // Projectile trail
                        object : BukkitRunnable() {
                            override fun run() {
                                if(snowball.isDead || !player.isOnline) {
                                    this.cancel()
                                } else {
                                    for(nearbyPlayer in snowball.location.getNearbyPlayers(0.25)) {
                                        if(nearbyPlayer.burbPlayer().playerTeam == Teams.ZOMBIES) {
                                            nearbyPlayer.addPotionEffect(PotionEffect(PotionEffectType.SLOWNESS, 20 * 6, 3, false, false))
                                            nearbyPlayer.world.playSound(player.location, "item.glow_ink_sac.use", SoundCategory.VOICE, 1f, 1f)
                                        }
                                    }
                                    snowball.location.world.spawnParticle(
                                        Particle.DUST,
                                        snowball.location,
                                        1, 0.0, 0.0, 0.0, 0.0,
                                        Particle.DustOptions(Color.FUCHSIA, 0.75f),
                                        true
                                    )
                                }
                            }
                        }.runTaskTimer(plugin, 0L, 1L)
                    }
                }
                BurbAbility.PLANTS_HEAVY_ABILITY_2.abilityId -> {
                    player.world.playSound(player.location, "block.grass.break", SoundCategory.VOICE, 1f, 1f)
                    player.getAttribute(Attribute.SCALE)?.baseValue = 0.25
                    player.addPotionEffects(
                        listOf(
                            PotionEffect(PotionEffectType.SPEED, 20 * 6, 4, false, false),
                            PotionEffect(PotionEffectType.INVISIBILITY, 20 * 6, 0, false, false)
                        )
                    )
                    ItemManager.clearItems(player)
                    player.inventory.boots = null
                    player.inventory.setItemInMainHand(ItemStack(Material.BREEZE_ROD, 1))
                    player.inventory.setItem(player.inventory.heldItemSlot + 1, ItemStack(Material.BREEZE_ROD, 1))
                    player.inventory.setItem(player.inventory.heldItemSlot - 1, ItemStack(Material.BREEZE_ROD, 1))
                    object : BukkitRunnable() {
                        var ticks = 0
                        override fun run() {
                            if(ticks <= 120) {
                                if(ticks % 5 == 0) {
                                    player.world.playSound(player.location, "block.grass.break", SoundCategory.VOICE, 1f, 1f)
                                }
                                player.world.spawnParticle(
                                    Particle.DUST,
                                    player.location,
                                    5, 0.05, 0.15, 0.05, 0.0,
                                    Particle.DustOptions(Color.fromRGB(20, 18, 11), 0.5f)
                                )
                            }
                            if(player.inventory.itemInMainHand.type != Material.BREEZE_ROD || ticks >= 120 || player.vehicle != null || GameManager.getGameState() !in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
                                player.sendActionBar(Formatting.allTags.deserialize(""))
                                player.inventory.remove(Material.BREEZE_ROD)
                                player.velocity = player.velocity.add(Vector(0.0, 0.75, 0.0))
                                player.removePotionEffect(PotionEffectType.SPEED)
                                player.removePotionEffect(PotionEffectType.INVISIBILITY)
                                player.getAttribute(Attribute.SCALE)?.baseValue = 1.0
                                ItemManager.giveCharacterItems(player)
                                ItemManager.givePlayerTeamBoots(player, player.burbPlayer().playerTeam)
                                player.setCooldown(usedItem.type, usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.ability.cooldown"), PersistentDataType.INTEGER)!!)
                                player.world.playSound(player.location, "block.grass.place", SoundCategory.VOICE, 1f, 1f)
                                cancel()
                            }
                            ticks++
                        }
                    }.runTaskTimer(plugin, 0L, 1L)
                }
                BurbAbility.PLANTS_HEAVY_ABILITY_3.abilityId -> {
                    if(player.location.block.getRelative(BlockFace.DOWN).type != Material.AIR) {
                        player.world.playSound(player.location, "entity.player.burp", SoundCategory.VOICE, 1f, 1f)
                        object : BukkitRunnable() {
                            var ticks = 0
                            var seconds = 0
                            val spikeweedEntity = player.world.spawn(player.location.setRotation(Rotation.rotation(player.yaw, 0f)), BlockDisplay::class.java).apply {
                                block = Material.FIREFLY_BUSH.createBlockData()
                                brightness = Display.Brightness(15, 15)
                                transformation = Transformation(Vector3f(-0.5f, 0f, -0.5f), transformation.leftRotation, transformation.scale, transformation.rightRotation)
                                addScoreboardTag("${player.uniqueId}.spikeweed")
                            }
                            override fun run() {
                                if(!spikeweedEntity.isDead) {
                                    if(spikeweedEntity.passengers.isEmpty()) {
                                        for(nearbyEnemy in spikeweedEntity.location.getNearbyPlayers(0.5).filter { p -> p.burbPlayer().playerTeam == Teams.ZOMBIES }) {
                                            spikeweedEntity.addPassenger(nearbyEnemy)
                                            object : BukkitRunnable() {
                                                var damageTimer = 0
                                                override fun run() {
                                                    spikeweedEntity.passengers.filterIsInstance<Player>().forEach{ passenger -> passenger.damage(0.75, player) }
                                                    damageTimer++
                                                    if(damageTimer >= 4) {
                                                        spikeweedEntity.world.playSound(spikeweedEntity.location, "block.sweet_berry_bush.break", 1f, 1f)
                                                        spikeweedEntity.removePassenger(nearbyEnemy)
                                                        spikeweedEntity.remove()
                                                        cancel()
                                                    }
                                                }
                                            }.runTaskTimer(plugin, 0L , 10L)
                                        }
                                    }

                                    if(seconds >= 60) {
                                        spikeweedEntity.world.playSound(spikeweedEntity.location, "block.sweet_berry_bush.break", 1f, 1f)
                                        spikeweedEntity.remove()
                                        cancel()
                                    }
                                } else {
                                    spikeweedEntity.world.playSound(spikeweedEntity.location, "block.sweet_berry_bush.break", 1f, 1f)
                                    spikeweedEntity.remove()
                                    cancel()
                                }
                                ticks++
                                if(ticks >= 20) {
                                    ticks = 0
                                    seconds++
                                }
                            }
                        }.runTaskTimer(plugin, 0L, 1L)
                    } else {
                        player.setCooldown(usedItem, 0)
                        player.sendActionBar(Formatting.allTags.deserialize("<red>${BurbAbility.PLANTS_HEAVY_ABILITY_3.abilityName} must be placed on solid ground."))
                    }
                }
                //TODO: HEAL BEAM
                //TODO: SUN BEAM
                //TODO: HEAL FLOWER
                BurbAbility.PLANTS_RANGED_ABILITY_1.abilityId -> {
                    if(player.location.block.getRelative(BlockFace.DOWN).type != Material.AIR) {
                        player.world.playSound(player.location, "block.gravel.break", SoundCategory.VOICE, 1f, 1f)
                        object : BukkitRunnable() {
                            var ticks = 0
                            var seconds = 0
                            val potatoMineEntity = player.world.spawn(player.location.clone().subtract(0.0, 0.75, 0.0).setRotation(Rotation.rotation(player.yaw, 0f)), BlockDisplay::class.java).apply {
                                block = Material.SPONGE.createBlockData()
                                brightness = Display.Brightness(15, 15)
                                transformation = Transformation(Vector3f(-0.5f, 0f, -0.5f), transformation.leftRotation, transformation.scale, transformation.rightRotation)
                                addScoreboardTag("${player.uniqueId}.potato_mine")
                            }
                            override fun run() {
                                if(!potatoMineEntity.isDead) {
                                    for(nearbyEnemy in potatoMineEntity.location.getNearbyPlayers(0.85).filter { p -> p.burbPlayer().playerTeam == Teams.ZOMBIES }) {
                                        potatoMineEntity.world.spawn(potatoMineEntity.location.clone().add(0.0, 0.5, 0.0), TNTPrimed::class.java).apply {
                                            source = player
                                            fuseTicks = 0
                                        }
                                        potatoMineEntity.world.playSound(potatoMineEntity.location, "block.gravel.break", 1f, 1f)
                                        potatoMineEntity.remove()
                                        cancel()
                                    }
                                    if(seconds >= 60) {
                                        potatoMineEntity.world.playSound(potatoMineEntity.location, "block.gravel.break", 1f, 1f)
                                        potatoMineEntity.remove()
                                        cancel()
                                    }
                                } else {
                                    potatoMineEntity.world.playSound(potatoMineEntity.location, "block.gravel.break", 1f, 1f)
                                    potatoMineEntity.remove()
                                    cancel()
                                }
                                ticks++
                                if(ticks >= 20) {
                                    ticks = 0
                                    seconds++
                                }
                            }
                        }.runTaskTimer(plugin, 0L, 1L)
                    } else {
                        player.setCooldown(usedItem, 0)
                        player.sendActionBar(Formatting.allTags.deserialize("<red>${BurbAbility.PLANTS_RANGED_ABILITY_1.abilityName} must be placed on solid ground."))
                    }
                }
                //TODO: GARLIC DRONE
                BurbAbility.PLANTS_RANGED_ABILITY_3.abilityId -> { // REDO WALL-NUT
                    val facingLocation = player.location.direction.toLocation(player.world)
                    if(facingLocation.block.getRelative(BlockFace.DOWN).isSolid) {
                        if(facingLocation.block.type == Material.AIR && facingLocation.block.getRelative(BlockFace.UP).type == Material.AIR) {
                            facingLocation.block.type = Material.GOLD_BLOCK
                            facingLocation.block.getRelative(BlockFace.UP).type = Material.GOLD_BLOCK
                            facingLocation.world.playSound(facingLocation, "block.anvil.place", SoundCategory.VOICE, 1f, 1f)
                            object : BukkitRunnable() {
                                var ticks = 0
                                var seconds = 0
                                override fun run() {
                                    if(seconds >= 30) {
                                        facingLocation.block.type = Material.AIR
                                        facingLocation.block.getRelative(BlockFace.UP).type = Material.AIR
                                        player.world.playSound(player.location, "block.anvil.break", SoundCategory.VOICE, 1f, 1f)
                                    }
                                    ticks++
                                    if(ticks >= 20) {
                                        ticks = 0
                                        seconds++
                                    }
                                }
                            }.runTaskTimer(plugin, 0L, 1L)
                        } else {
                            player.setCooldown(usedItem, 0)
                            player.sendActionBar(Formatting.allTags.deserialize("<red>${BurbAbility.PLANTS_RANGED_ABILITY_3.abilityName} requires a 2 block high space."))
                        }
                    } else {
                        player.setCooldown(usedItem, 0)
                        player.sendActionBar(Formatting.allTags.deserialize("<red>${BurbAbility.PLANTS_RANGED_ABILITY_3.abilityName} must be placed on solid ground."))
                    }
                }
                BurbAbility.ZOMBIES_SCOUT_ABILITY_1.abilityId -> {
                    val snowball = player.world.spawn(player.eyeLocation.clone(), Snowball::class.java)
                    val snowballVelocity = player.location.direction.multiply(0.75)
                    snowball.velocity = snowballVelocity
                    snowball.shooter = player
                    object : BukkitRunnable() {
                        override fun run() {
                            if(snowball.isDead || !player.isOnline) {
                                val smokeGrenadeLocation = snowball.location.clone().add(0.5, 1.0, 0.5)
                                smokeGrenadeLocation.world.playSound(smokeGrenadeLocation, "block.lava.extinguish", SoundCategory.VOICE, 1f, 1f)
                                object : BukkitRunnable() {
                                    var smokeTimer = 0
                                    override fun run() {
                                        if(smokeTimer >= 48 || GameManager.getGameState() !in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
                                            cancel()
                                        }
                                        smokeGrenadeLocation.clone().world.spawnParticle(
                                            Particle.DUST,
                                            smokeGrenadeLocation,
                                            150, 1.75, 1.5, 1.75, 0.0,
                                            Particle.DustOptions(Color.PURPLE, 3.5f)
                                        )
                                        smokeGrenadeLocation.world.playSound(smokeGrenadeLocation, "block.lava.extinguish", SoundCategory.VOICE, 1f, 1f)
                                        for(nearbyPlayer in smokeGrenadeLocation.getNearbyPlayers(3.0)) {
                                            if(nearbyPlayer.burbPlayer().playerTeam == Teams.PLANTS) {
                                                if(nearbyPlayer.vehicle == null || nearbyPlayer.vehicle?.scoreboardTags?.contains("${nearbyPlayer.uniqueId}-death-vehicle") == false) {
                                                    nearbyPlayer.damage(0.001, player)
                                                    if(nearbyPlayer.health >= 1.0) {
                                                        nearbyPlayer.health -= 1.0
                                                    } else {
                                                        nearbyPlayer.health = 0.0
                                                    }
                                                }
                                            }
                                        }
                                        smokeTimer++
                                    }
                                }.runTaskTimer(plugin, 0L, 5L)
                                cancel()
                            } else {
                                snowball.location.world.spawnParticle(
                                    Particle.DUST,
                                    snowball.location,
                                    1, 0.0, 0.0, 0.0, 0.0,
                                    Particle.DustOptions(Color.PURPLE, 1.25f),
                                    true
                                )
                            }
                        }
                    }.runTaskTimer(plugin, 0L, 2L)
                }
                BurbAbility.ZOMBIES_SCOUT_ABILITY_2.abilityId -> {
                    player.world.playSound(player.location, "burb.ability.foot_soldier.zpg.trigger", SoundCategory.VOICE, 0.5f, 1f)
                    object : BukkitRunnable() {
                        var timer = 0
                        override fun run() {
                            if(timer == 24) {
                                player.world.playSound(player.location, "burb.ability.foot_soldier.zpg.fire", SoundCategory.VOICE, 1f, 1f)
                                object : BukkitRunnable() {
                                    var ticksLived = 0
                                    val zpgEntity = player.world.spawn(player.eyeLocation, ItemDisplay::class.java).apply {
                                        setItemStack(ItemStack(Material.TNT))
                                        setGravity(false)
                                        displayWidth = 1.5f
                                        displayHeight = 1.5f
                                        teleportDuration = 1
                                    }
                                    val eyeLocation = player.eyeLocation
                                    val direction = eyeLocation.direction
                                    override fun run() {
                                        if(!zpgEntity.isValid) {
                                            zpgEntity.remove()
                                            cancel()
                                        }
                                        zpgEntity.teleport(zpgEntity.location.add(direction))
                                        val nearbyEntities = zpgEntity.location.getNearbyPlayers(0.1).filter { p -> p.burbPlayer().playerTeam == Teams.PLANTS }
                                        for(entity in nearbyEntities) {
                                            zpgEntity.world.spawn(zpgEntity.location, TNTPrimed::class.java).apply {
                                                source = player
                                                fuseTicks = 0
                                            }
                                            zpgEntity.remove()
                                            cancel()
                                        }
                                        if(ticksLived % 10 == 0) {
                                            zpgEntity.location.world.playSound(zpgEntity.location, "burb.ability.foot_soldier.zpg.whizz", SoundCategory.VOICE, 1f, 1f)
                                        }
                                        if(zpgEntity.location.block.getRelative(BlockFace.DOWN).type != Material.AIR || ticksLived >= 200) {
                                            val tnt = zpgEntity.world.spawn(zpgEntity.location, TNTPrimed::class.java)
                                            tnt.source = player
                                            tnt.fuseTicks = 0
                                            zpgEntity.remove()
                                            cancel()
                                        }
                                        ticksLived++
                                    }
                                }.runTaskTimer(plugin, 0L, 1L)
                                cancel()
                            } else {
                                if(player.vehicle == null) {
                                    player.addPotionEffect(PotionEffect(PotionEffectType.SLOWNESS, 10, 4, false,false))
                                } else {
                                    cancel()
                                }
                            }
                            timer++
                        }
                    }.runTaskTimer(plugin, 0L, 1L)
                }
                BurbAbility.ZOMBIES_SCOUT_ABILITY_3.abilityId -> {
                    player.world.playSound(player.location, "burb.ability.foot_soldier.rocket_jump", SoundCategory.VOICE, 1f, 1f)
                    player.velocity = player.velocity.setY(1.35)
                }
                //TODO: SUPER ULTRA BALL
                //TODO: TURBO TWISTER
                BurbAbility.ZOMBIES_HEAVY_ABILITY_3.abilityId -> {
                    player.world.playSound(player.location, "entity.breeze.shoot", SoundCategory.VOICE, 1f, 0.75f)
                    player.velocity = player.velocity.add(Vector(player.location.direction.x * 2.25, 0.25, player.location.direction.z * 2.25))
                }
                //TODO: HEAL BEAM
                BurbAbility.ZOMBIES_HEALER_ABILITY_2.abilityId -> {
                    val block = player.getTargetBlock(null, 16)
                    val location = block.location
                    val pitch = player.eyeLocation.pitch
                    val yaw = player.eyeLocation.yaw
                    location.add(0.5, 1.0, 0.5)
                    location.yaw = yaw
                    location.pitch = pitch
                    player.teleport(location)
                    player.world.playSound(player.location, "entity.enderman.teleport", SoundCategory.VOICE, 1f, 0.75f)
                }
                //TODO: STICKY GRENADES
                //TODO: BARREL BLAST
                //TODO: PARROT PAL
                //TODO: CANNON RODEO
            }
        }
    }
}
