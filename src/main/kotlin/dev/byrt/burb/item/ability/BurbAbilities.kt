package dev.byrt.burb.item.ability

import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.item.ItemManager
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.library.Translation
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.player.cosmetics.BurbCosmetics
import dev.byrt.burb.plugin
import dev.byrt.burb.team.Teams
import dev.byrt.burb.text.ChatUtility
import dev.byrt.burb.text.Formatting

import io.papermc.paper.math.Rotation

import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.SoundCategory
import org.bukkit.attribute.Attribute
import org.bukkit.block.BlockFace
import org.bukkit.entity.BlockDisplay
import org.bukkit.entity.Display
import org.bukkit.entity.Fireball
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.entity.Snowball
import org.bukkit.entity.TNTPrimed
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Transformation
import org.bukkit.util.Vector

import org.joml.Vector3f

import kotlin.math.sin
import kotlin.random.Random

object BurbAbilities {
    fun useAbility(player: Player, ability: BurbAbility, usedItem: ItemStack) {
        // Check item used is actually an ability item
        if(ItemManager.verifyAbility(usedItem)) {
            // Return if cooldown exists
            if(player.hasCooldown(usedItem)) {
                player.sendActionBar(Formatting.allTags.deserialize("<red>Ability is on cooldown!"))
                player.playSound(Sounds.Misc.INTERFACE_ERROR)
                return
            } else {
                player.sendActionBar(Formatting.allTags.deserialize("<white>You used ${player.burbPlayer().playerTeam.teamColourTag}${ability.abilityName}<white>!"))
                player.playSound(Sounds.Weapon.ABILITY_COMBO_CAST)
            }
            // Set cooldown on use
            player.setCooldown(usedItem, usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.ability.cooldown"), PersistentDataType.INTEGER)!!)
            // Run ability
            when(ability) {
                // Peashooter
                BurbAbility.PLANTS_SCOUT_ABILITY_1 -> {
                    player.world.playSound(player.location, "burb.ability.peashooter.explosive.fire", SoundCategory.VOICE, 1f, 1f)
                    val tnt = player.world.spawn(player.eyeLocation, TNTPrimed::class.java)
                    tnt.source = player
                    val tntVelocity = player.location.direction.multiply(1.15)
                    tnt.velocity = tntVelocity
                    tnt.fuseTicks = Int.MAX_VALUE
                    object : BukkitRunnable() {
                        override fun run() {
                            if(tnt.isOnGround && tnt.velocity.x <= 0.015 && tnt.velocity.y <= 0.015 && tnt.velocity.z <= 0.015) {
                                player.world.playSound(tnt.location, "burb.ability.peashooter.explosive.voice", SoundCategory.VOICE, 2.5f, 1f)
                                object : BukkitRunnable() {
                                    override fun run() {
                                        tnt.world.createExplosion(player,tnt.location, 3f, false, false)
                                        tnt.remove()
                                        player.world.playSound(tnt.location, "burb.ability.peashooter.explosive.explode", SoundCategory.VOICE, 3f, 1f)
                                    }
                                }.runTaskLater(plugin, 90L)
                                cancel()
                            }
                        }
                    }.runTaskTimer(plugin, 0L, 1L)
                }
                BurbAbility.PLANTS_SCOUT_ABILITY_2 -> {
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
                                if(player.inventory.itemInMainHand.type != Material.BREEZE_ROD || player.burbPlayer().isDead || bulletsRemaining <= 0 || GameManager.getGameState() !in listOf(
                                        GameState.IN_GAME, GameState.OVERTIME)) {
                                    player.sendActionBar(Formatting.allTags.deserialize(""))
                                    gatlingVehicle.eject()
                                    gatlingVehicle.remove()
                                    player.inventory.remove(Material.BREEZE_ROD)
                                    player.velocity = player.velocity.add(Vector(0.0, 0.5, 0.0))
                                    ItemManager.giveCharacterItems(player)
                                    player.setCooldown(usedItem.type, usedItem.persistentDataContainer.get(
                                        NamespacedKey(
                                            plugin,
                                            "burb.ability.cooldown"
                                        ), PersistentDataType.INTEGER)!!)
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
                BurbAbility.PLANTS_SCOUT_ABILITY_3 -> {
                    player.world.playSound(player.location, "block.beacon.power_select", SoundCategory.VOICE, 0.5f, 2f)
                    player.addPotionEffects(listOf(
                        PotionEffect(PotionEffectType.JUMP_BOOST, 20 * 12, 6, false, true),
                        PotionEffect(PotionEffectType.SPEED, 20 * 12, 6, false, true)
                    ))
                }
                // Chomper
                BurbAbility.PLANTS_HEAVY_ABILITY_1 -> {
                    player.world.playSound(player.location, "block.slime_block.place", SoundCategory.VOICE, 1f, 1f)
                    for(bullets in 0..7) {
                        val snowball = player.world.spawn(player.eyeLocation.clone(), Snowball::class.java)
                        snowball.shooter = player
                        snowball.location.direction = player.location.direction
                        // Projectile velocity
                        val snowballVelocity = player.location.direction.multiply(1.75)
                        snowball.velocity = snowballVelocity
                        // Projectile bloom
                        snowball.velocity = snowball.velocity.add(
                            Vector(
                                Random.nextDouble(-0.095, 0.095),
                                Random.nextDouble(-0.075, 0.075),
                                Random.nextDouble(-0.095, 0.095)
                            )
                        )
                        // Projectile damage
                        snowball.persistentDataContainer.set(
                            NamespacedKey(
                                plugin,
                                "burb.weapon.damage"
                            ), PersistentDataType.DOUBLE, 1.0)
                        // Projectile trail
                        object : BukkitRunnable() {
                            override fun run() {
                                if(snowball.isDead || !player.isOnline) {
                                    this.cancel()
                                } else {
                                    for(nearbyPlayer in snowball.location.getNearbyPlayers(0.75)) {
                                        if(nearbyPlayer.burbPlayer().playerTeam == Teams.ZOMBIES) {
                                            nearbyPlayer.addPotionEffect(
                                                PotionEffect(
                                                    PotionEffectType.SLOWNESS,
                                                    20 * 6,
                                                    3,
                                                    false,
                                                    false
                                                )
                                            )
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
                BurbAbility.PLANTS_HEAVY_ABILITY_2 -> {
                if(player.location.block.getRelative(BlockFace.DOWN).type != Material.AIR) {
                    player.world.playSound(player.location, "entity.player.burp", SoundCategory.VOICE, 1f, 1f)
                    object : BukkitRunnable() {
                        var ticks = 0
                        var seconds = 0
                        val spikeweedEntity = player.world.spawn(player.location.setRotation(Rotation.rotation(player.yaw, 0f)), BlockDisplay::class.java).apply {
                            block = Material.FIREFLY_BUSH.createBlockData()
                            brightness = Display.Brightness(15, 15)
                            transformation = Transformation(
                                Vector3f(-0.5f, 0f, -0.5f),
                                transformation.leftRotation,
                                transformation.scale,
                                transformation.rightRotation
                            )
                            addScoreboardTag("${player.uniqueId}.spikeweed")
                        }
                        override fun run() {
                            if(!spikeweedEntity.isDead) {
                                if(spikeweedEntity.passengers.isEmpty()) {
                                    for(nearbyEnemy in spikeweedEntity.location.getNearbyPlayers(0.9).filter { p -> p.burbPlayer().playerTeam == Teams.ZOMBIES && !p.burbPlayer().isDead}) {
                                        spikeweedEntity.addPassenger(nearbyEnemy)
                                        object : BukkitRunnable() {
                                            var damageTimer = 0
                                            override fun run() {
                                                spikeweedEntity.passengers.filterIsInstance<Player>().forEach{ passenger -> passenger.damage(0.75, player) }
                                                damageTimer++
                                                if(damageTimer >= 4 || nearbyEnemy.burbPlayer().isDead) {
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
                BurbAbility.PLANTS_HEAVY_ABILITY_3 -> {
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
                            if(player.inventory.itemInMainHand.type != Material.BREEZE_ROD || ticks >= 120 || player.burbPlayer().isDead || GameManager.getGameState() !in listOf(
                                    GameState.IN_GAME, GameState.OVERTIME)) {
                                player.sendActionBar(Formatting.allTags.deserialize(""))
                                player.inventory.remove(Material.BREEZE_ROD)
                                player.velocity = player.velocity.add(Vector(0.0, 0.75, 0.0))
                                player.removePotionEffect(PotionEffectType.SPEED)
                                player.removePotionEffect(PotionEffectType.INVISIBILITY)
                                player.getAttribute(Attribute.SCALE)?.baseValue = 1.0
                                ItemManager.giveCharacterItems(player)
                                ItemManager.givePlayerTeamBoots(player, player.burbPlayer().playerTeam)
                                player.setCooldown(usedItem.type, usedItem.persistentDataContainer.get(
                                    NamespacedKey(
                                        plugin,
                                        "burb.ability.cooldown"
                                    ), PersistentDataType.INTEGER)!!)
                                player.world.playSound(player.location, "block.grass.place", SoundCategory.VOICE, 1f, 1f)
                                cancel()
                            }
                            ticks++
                        }
                    }.runTaskTimer(plugin, 0L, 1L)
                }
                // Sunflower
                BurbAbility.PLANTS_HEALER_ABILITY_1 -> {
                    val nearbyTeammates = player.getNearbyEntities(4.0, 4.0, 4.0).filterIsInstance<Player>().filter { p -> p.burbPlayer().playerTeam == Teams.PLANTS }.sortedBy { p -> player.location.distanceSquared(p.location) }
                    if(nearbyTeammates.isNotEmpty()) {
                        val healingTeammate = nearbyTeammates[0]
                        player.sendActionBar(Formatting.allTags.deserialize("<yellow>Healing attached to ${healingTeammate.name}"))
                        player.setCooldown(usedItem, 20 * 3600)
                        object : BukkitRunnable() {
                            var timer = 0
                            var ticks = 0
                            override fun run() {
                                if(!player.burbPlayer().isDead && !healingTeammate.burbPlayer().isDead) {
                                    if(player.location.distanceSquared(healingTeammate.location) <= 64.0 || timer <= 5 || GameManager.getGameState() !in listOf(
                                            GameState.IN_GAME, GameState.OVERTIME)) {
                                        if(ticks % 5 == 0) {
                                            if(player.location.distanceSquared(healingTeammate.location) > 64.0) {
                                                player.sendActionBar(Formatting.allTags.deserialize("<red>Healing requirements failed or timer exceeded"))
                                                player.setCooldown(usedItem, usedItem.persistentDataContainer.get(
                                                    NamespacedKey(plugin, "burb.ability.cooldown"), PersistentDataType.INTEGER)!!)
                                                cancel()
                                            }
                                            val startLoc = player.location.add(0.0, 0.25, 0.0)
                                            val endLoc = healingTeammate.location.add(0.0, 0.25, 0.0)
                                            val steps = 10
                                            val xIncrement = (endLoc.x - startLoc.x) / steps
                                            val yIncrement = (endLoc.y - startLoc.y) / steps
                                            val zIncrement = (endLoc.z - startLoc.z) / steps

                                            for (i in 0..steps) {
                                                val x = startLoc.x + xIncrement * i
                                                var y = startLoc.y + yIncrement * i
                                                val z = startLoc.z + zIncrement * i

                                                y += sin(Math.PI * i / steps) * 0.05
                                                val particleLoc = Location(startLoc.world, x, y, z)
                                                player.world.spawnParticle(
                                                    Particle.DUST,
                                                    particleLoc,
                                                    0,
                                                    0.0,
                                                    0.0,
                                                    0.0,
                                                    Particle.DustOptions(Color.YELLOW, 1.5f)
                                                )
                                            }
                                            if(healingTeammate.health >= 19.75) {
                                                healingTeammate.health = 20.0
                                                player.sendActionBar(Formatting.allTags.deserialize("<green>${healingTeammate.name} fully healed"))
                                                player.setCooldown(usedItem.type, usedItem.persistentDataContainer.get(
                                                    NamespacedKey(plugin, "burb.ability.cooldown"), PersistentDataType.INTEGER)!!)
                                                cancel()
                                            } else {
                                                healingTeammate.health += 0.25
                                                player.sendActionBar(Formatting.allTags.deserialize("<green>Healed ${healingTeammate.name} for 0.5<red>${ChatUtility.HEART_UNICODE}"))
                                            }
                                        }
                                    } else {
                                        player.sendActionBar(Formatting.allTags.deserialize("<red>Healing requirements failed or timer exceeded"))
                                        player.setCooldown(usedItem, usedItem.persistentDataContainer.get(
                                            NamespacedKey(
                                                plugin,
                                                "burb.ability.cooldown"
                                            ), PersistentDataType.INTEGER)!!)
                                        cancel()
                                    }
                                } else {
                                    player.sendActionBar(Formatting.allTags.deserialize("<red>Healing requirements failed or timer exceeded"))
                                    player.setCooldown(usedItem, usedItem.persistentDataContainer.get(
                                        NamespacedKey(
                                            plugin,
                                            "burb.ability.cooldown"
                                        ), PersistentDataType.INTEGER)!!)
                                    cancel()
                                }
                                ticks++
                                if(ticks >= 20) {
                                    ticks = 0
                                    timer++
                                }
                            }
                        }.runTaskTimer(plugin, 0L, 1L)
                    } else {
                        player.setCooldown(usedItem, 0)
                        player.sendActionBar(Formatting.allTags.deserialize("<red>No team mate in range."))
                    }
                }
                BurbAbility.PLANTS_HEALER_ABILITY_2 -> {
                    if(player.location.block.getRelative(BlockFace.DOWN).type != Material.AIR && player.location.block.getRelative(BlockFace.DOWN).isSolid) {
                        player.world.playSound(player.location, "block.beacon.activate", SoundCategory.VOICE, 1f, 1f)
                        ItemManager.clearItems(player)
                        player.inventory.setItemInMainHand(ItemStack(Material.BREEZE_ROD, 1))
                        player.inventory.setItem(player.inventory.heldItemSlot + 1, ItemStack(Material.BREEZE_ROD, 1))
                        player.inventory.setItem(player.inventory.heldItemSlot - 1, ItemStack(Material.BREEZE_ROD, 1))
                        object : BukkitRunnable() {
                            var bulletsRemaining = 50
                            val sunbeamVehicle = player.location.world.spawn(player.location.clone(), ItemDisplay::class.java).apply {
                                addScoreboardTag("${player.uniqueId}-sunbeam-vehicle")
                                player.teleport(this)
                                addPassenger(player)
                            }
                            override fun run() {
                                if(!player.isOnline) {
                                    sunbeamVehicle.remove()
                                    cancel()
                                }
                                if(player.vehicle == sunbeamVehicle) {
                                    player.sendActionBar(Formatting.allTags.deserialize(Translation.Weapon.SUNBEAM_CONTROLS.replace("%s", bulletsRemaining.toString())))
                                    sunbeamVehicle.setRotation(player.yaw, player.pitch)
                                }
                                if(player.isSneaking && player.inventory.itemInMainHand.type == Material.BREEZE_ROD) {
                                    val snowball = player.world.spawn(player.eyeLocation.clone(), Snowball::class.java)
                                    snowball.shooter = player
                                    snowball.location.direction = player.location.direction
                                    val snowballVelocity = player.location.direction.multiply(7.5)
                                    snowball.velocity = snowballVelocity
                                    snowball.persistentDataContainer.set(NamespacedKey(plugin, "burb.weapon.damage"), PersistentDataType.DOUBLE, 0.8)
                                    object : BukkitRunnable() {
                                        override fun run() {
                                            if(snowball.isDead || !player.isOnline) {
                                                this.cancel()
                                            } else {
                                                snowball.location.world.spawnParticle(
                                                    Particle.DUST,
                                                    snowball.location,
                                                    1, 0.0, 0.0, 0.0,
                                                    Particle.DustOptions(Color.YELLOW, 2f)
                                                )
                                            }
                                        }
                                    }.runTaskTimer(plugin, 1L, 2L)
                                    player.world.playSound(player.location, "burb.weapon.sunflower.fire", SoundCategory.VOICE, 1f, 1f)
                                    bulletsRemaining--
                                }
                                if(player.inventory.itemInMainHand.type != Material.BREEZE_ROD || player.burbPlayer().isDead || bulletsRemaining <= 0 || GameManager.getGameState() !in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
                                    player.sendActionBar(Formatting.allTags.deserialize(""))
                                    sunbeamVehicle.eject()
                                    sunbeamVehicle.remove()
                                    player.inventory.remove(Material.BREEZE_ROD)
                                    player.velocity = player.velocity.add(Vector(0.0, 0.5, 0.0))
                                    ItemManager.giveCharacterItems(player)
                                    player.setCooldown(usedItem.type, usedItem.persistentDataContainer.get(NamespacedKey(plugin, "burb.ability.cooldown"), PersistentDataType.INTEGER)!!)
                                    if(bulletsRemaining == 50) player.setCooldown(BurbAbility.PLANTS_HEALER_ABILITY_2.abilityMaterial, 0)
                                    player.world.playSound(player.location, "block.beacon.deactivate", SoundCategory.VOICE, 1f, 1f)
                                    cancel()
                                }
                            }
                        }.runTaskTimer(plugin, 0L, 1L)
                    } else {
                        player.setCooldown(usedItem, 0)
                        player.sendActionBar(Formatting.allTags.deserialize("<red>You cannot use this ability while in the air."))
                    }
                }
                BurbAbility.PLANTS_HEALER_ABILITY_3 -> {
                    player.world.playSound(player.location, "trial_spawner.about_to_spawn_item", SoundCategory.VOICE, 2f, 1.5f)
                    player.velocity = player.velocity.setY(1.5)
                    object : BukkitRunnable() {
                        override fun run() {
                            if(!player.burbPlayer().isDead || player.vehicle != null) {
                                player.velocity = player.velocity.add(
                                    Vector(
                                        player.location.direction.x * 1.5,
                                        0.25,
                                        player.location.direction.z * 1.5
                                    )
                                )
                                player.world.playSound(player.location, "block.beacon.power_select", SoundCategory.VOICE, 1f, 1f)
                            }
                        }
                    }.runTaskLater(plugin, 20L)
                }
                // Cactus
                BurbAbility.PLANTS_RANGED_ABILITY_1 -> {
                    if(player.location.block.getRelative(BlockFace.DOWN).type != Material.AIR) {
                        player.world.playSound(player.location, "block.gravel.break", SoundCategory.VOICE, 1f, 1f)
                        object : BukkitRunnable() {
                            var primeTime = 3
                            var timer = 0
                            val potatoMineEntity = player.world.spawn(player.location.clone().setRotation(
                                Rotation.rotation(player.yaw + 180, 0f)), ItemDisplay::class.java).apply {
                                val potatoMine = ItemStack(Material.ECHO_SHARD)
                                val potatoMineMeta = potatoMine.itemMeta
                                potatoMineMeta.itemModel = NamespacedKey("minecraft", "potato_mine")
                                potatoMine.itemMeta = potatoMineMeta
                                setItemStack(potatoMine)
                                brightness = Display.Brightness(15, 15)
                                teleportDuration = 5
                                transformation = Transformation(
                                    transformation.translation,
                                    transformation.leftRotation,
                                    transformation.scale,
                                    transformation.rightRotation
                                )
                                addScoreboardTag("${player.uniqueId}.potato_mine")
                            }
                            override fun run() {
                                if(timer < primeTime) {
                                    timer++
                                } else {
                                    object : BukkitRunnable() {
                                        var ticks = 0
                                        var seconds = 0
                                        override fun run() {
                                            if(ticks == 0 && seconds == 0) {
                                                potatoMineEntity.teleport(potatoMineEntity.location.clone().add(Vector(0.0, 0.5, 0.0)))
                                                potatoMineEntity.world.playSound(potatoMineEntity.location, "block.gravel.break", SoundCategory.VOICE, 1f, 1f)
                                            }
                                            if(!potatoMineEntity.isDead) {
                                                val nearbyEnemies = potatoMineEntity.location.getNearbyPlayers(0.9).filter { p -> p.burbPlayer().playerTeam == Teams.ZOMBIES && !p.burbPlayer().isDead }
                                                if(nearbyEnemies.isNotEmpty()) {
                                                    potatoMineEntity.world.createExplosion(player, potatoMineEntity.location, 2.5f, false, false)
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
                                    cancel()
                                }
                            }
                        }.runTaskTimer(plugin, 0L, 20L)

                    } else {
                        player.setCooldown(usedItem, 0)
                        player.sendActionBar(Formatting.allTags.deserialize("<red>${BurbAbility.PLANTS_RANGED_ABILITY_1.abilityName} must be placed on solid ground."))
                    }
                }
                BurbAbility.PLANTS_RANGED_ABILITY_2 -> {
                val facingLocation = player.location.add(player.location.setRotation(player.yaw, 0f).direction.multiply(5).normalize()).block.location.toBlockLocation()
                if(facingLocation.block.getRelative(BlockFace.DOWN).isSolid) {
                    if(facingLocation.block.type == Material.AIR && facingLocation.block.getRelative(BlockFace.UP).type == Material.AIR) {
                        facingLocation.block.type = Material.MANGROVE_WOOD
                        facingLocation.block.getRelative(BlockFace.UP).type = Material.MANGROVE_WOOD
                        facingLocation.world.playSound(facingLocation, "block.anvil.place", SoundCategory.VOICE, 1f, 1f)
                        object : BukkitRunnable() {
                            var ticks = 0
                            var seconds = 0
                            override fun run() {
                                if(seconds >= 20 || GameManager.getGameState() !in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
                                    facingLocation.block.type = Material.AIR
                                    facingLocation.block.getRelative(BlockFace.UP).type = Material.AIR
                                    facingLocation.world.playSound(facingLocation, "block.anvil.break", SoundCategory.VOICE, 1f, 1f)
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
                        player.sendActionBar(Formatting.allTags.deserialize("<red>${BurbAbility.PLANTS_RANGED_ABILITY_3.abilityName} requires a 2 block high space."))
                    }
                } else {
                    player.setCooldown(usedItem, 0)
                    player.sendActionBar(Formatting.allTags.deserialize("<red>${BurbAbility.PLANTS_RANGED_ABILITY_3.abilityName} must be placed on solid ground."))
                }
            }
                BurbAbility.PLANTS_RANGED_ABILITY_3 -> {
                    player.world.playSound(player.location, "entity.wither.shoot", SoundCategory.VOICE, 1f, 1.25f)
                    player.location.clone().world.spawnParticle(
                        Particle.DUST,
                        player.location,
                        400, 2.5, 2.0, 2.5, 0.0,
                        Particle.DustOptions(Color.GRAY, 8f)
                    )
                    player.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, 20 * 5, 0, false, false))
                    player.velocity = player.velocity.add(
                        Vector(
                            player.location.direction.x * -1.25,
                            1.5,
                            player.location.direction.z * -1.25
                        )
                    )
                    player.inventory.helmet = null
                    player.inventory.boots = null
                    player.inventory.setItemInOffHand(null)
                    player.burbPlayer().playerCharacter.characterAbilities.abilitySet.forEach { ability -> player.setCooldown(ability.abilityMaterial, 20 * 5) }
                    player.setCooldown(player.burbPlayer().playerCharacter.characterMainWeapon.material, 20 * 5)
                    object: BukkitRunnable() {
                        override fun run() {
                            if(!player.burbPlayer().isDead) {
                                ItemManager.givePlayerTeamBoots(player, player.burbPlayer().playerTeam)
                                BurbCosmetics.equipCosmetics(player)
                            }
                        }
                    }.runTaskLater(plugin, 20L * 5L)
                }
                // Foot Soldier
                BurbAbility.ZOMBIES_SCOUT_ABILITY_1 -> {
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
                                                if(!nearbyPlayer.burbPlayer().isDead) {
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
                BurbAbility.ZOMBIES_SCOUT_ABILITY_2 -> {
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
                                        if(nearbyEntities.isNotEmpty()) {
                                            zpgEntity.world.createExplosion(player,zpgEntity.location, 2.5f, false, false)
                                            zpgEntity.remove()
                                            cancel()
                                        }
                                        if(ticksLived % 10 == 0) {
                                            zpgEntity.location.world.playSound(zpgEntity.location, "burb.ability.foot_soldier.zpg.whizz", SoundCategory.VOICE, 1f, 1f)
                                        }
                                        if(zpgEntity.location.block.getRelative(BlockFace.DOWN).type != Material.AIR || ticksLived >= 200) {
                                            zpgEntity.world.createExplosion(player,zpgEntity.location, 2.5f, false, false)
                                            zpgEntity.remove()
                                            cancel()
                                        }
                                        ticksLived++
                                    }
                                }.runTaskTimer(plugin, 0L, 1L)
                                cancel()
                            } else {
                                if(player.vehicle == null) {
                                    player.addPotionEffect(PotionEffect(PotionEffectType.SLOWNESS, 10, 4, false, false))
                                } else {
                                    cancel()
                                }
                            }
                            timer++
                        }
                    }.runTaskTimer(plugin, 0L, 1L)
                }
                BurbAbility.ZOMBIES_SCOUT_ABILITY_3 -> {
                    player.world.playSound(player.location, "burb.ability.foot_soldier.rocket_jump", SoundCategory.VOICE, 1f, 1f)
                    player.velocity = player.velocity.setY(1.35)
                }
                // Super Brainz
                BurbAbility.ZOMBIES_HEAVY_ABILITY_1 -> {
                    player.location.world.playSound(player.location, "entity.illusioner.cast_spell", 1f, 1f)
                    object : BukkitRunnable() {
                        var timer = 0
                        var ticks = 0
                        val spawnLocation = player.eyeLocation.set(player.eyeLocation.x, -60.0, player.eyeLocation.z)
                        val ultraBall = player.eyeLocation.world.spawn(spawnLocation, Fireball::class.java).apply {
                            direction = Vector(0, 0, 0)
                            acceleration = Vector(0, 0, 0)
                        }
                        val ultraBallVehicle = player.location.world.spawn(player.location, ItemDisplay::class.java).apply {
                            addPassenger(player)
                        }
                        override fun run() {
                            if(timer == 1 && ticks == 10) {
                                player.location.world.playSound(player.location, "entity.wither.shoot", 1f, 0.75f)
                                ultraBall.teleport(player.eyeLocation)
                                ultraBall.shooter = player
                                ultraBall.setHasBeenShot(true)
                                ultraBall.direction = player.eyeLocation.direction
                                ultraBall.acceleration = player.eyeLocation.direction.multiply(1.5)
                            }
                            if(timer == 2 && ticks == 0) {
                                ultraBallVehicle.removePassenger(player)
                                ultraBallVehicle.remove()
                            }
                            if(ultraBall.isDead) {
                                ultraBall.world.createExplosion(player,ultraBall.location.add(0.0, 1.0, 0.0), 2f, false, false)
                                ultraBall.remove()
                                ultraBallVehicle.remove()
                                cancel()
                            }
                            ticks++
                            if(ticks >= 20) {
                                ticks = 0
                                timer++
                            }
                            if(player.burbPlayer().isDead || timer >= 8) {
                                ultraBall.remove()
                                cancel()
                            }
                        }
                    }.runTaskTimer(plugin, 0L, 1L)
                }
                BurbAbility.ZOMBIES_HEAVY_ABILITY_2 -> {
                    player.world.playSound(player.location, "block.beacon.activate", SoundCategory.VOICE, 2f, 1.25f)
                    player.world.playSound(player.location, "block.respawn_anchor.set_spawn", SoundCategory.VOICE, 0.75f, 1.5f)
                    player.addPotionEffect(PotionEffect(PotionEffectType.RESISTANCE, 7 * 20, 0, false, false))
                    player.addPotionEffect(PotionEffect(PotionEffectType.SLOWNESS, 7 * 20, 3, false, false))
                    object: BukkitRunnable() {
                        override fun run() {
                            if(player.isDead || !(player.hasPotionEffect(PotionEffectType.SLOWNESS) && player.hasPotionEffect(PotionEffectType.RESISTANCE))) {
                                player.world.playSound(player.location, "block.beacon.deactivate", SoundCategory.VOICE, 2f, 1.25f)
                                player.world.playSound(player.location, "block.respawn_anchor.deplete", SoundCategory.VOICE, 0.75f, 0.75f)
                                cancel()
                            } else {
                                player.world.spawnParticle(
                                    Particle.ENCHANTED_HIT,
                                    player.location,
                                    5,
                                    0.75,
                                    0.75,
                                    0.75,
                                    0.0
                                )
                            }
                        }
                    }.runTaskTimer(plugin, 0L, 1L)
                }
                BurbAbility.ZOMBIES_HEAVY_ABILITY_3 -> {
                    player.world.playSound(player.location, "entity.breeze.shoot", SoundCategory.VOICE, 1f, 0.75f)
                    player.world.playSound(player.location, "item.spear.lunge_3", SoundCategory.VOICE, 1f, 1.25f)
                    player.velocity = player.velocity.add(
                        Vector(
                            player.location.direction.x * 2.3,
                            0.5,
                            player.location.direction.z * 2.3
                        )
                    )
                    object : BukkitRunnable() {
                        var timer = 0
                        override fun run() {
                            if(player.isDead || timer >= 5 * 20 || player.vehicle != null || (player.velocity.y <= 0.0 && player.location.block.getRelative(BlockFace.DOWN).type != Material.AIR)) {
                                cancel()
                            } else {
                                val nearbyEnemies = player.getNearbyEntities(1.25, 1.25, 1.25).filterIsInstance<Player>().filter { p -> p.burbPlayer().playerTeam == Teams.PLANTS && !p.burbPlayer().isDead }.sortedBy { p -> player.location.distanceSquared(p.location) }
                                if(nearbyEnemies.isNotEmpty()) {
                                    val enemyToHit = nearbyEnemies[0]
                                    val direction = player.location.direction.subtract(enemyToHit.location.direction)
                                    enemyToHit.velocity = direction.setY(0.5)
                                    enemyToHit.health -= 5.0
                                    enemyToHit.damage(0.0001)
                                    enemyToHit.world.playSound(player.location, "entity.zombie.attack_iron_door", SoundCategory.VOICE, 1f, 1.25f)
                                    cancel()
                                }
                                player.world.spawnParticle(
                                    Particle.CLOUD,
                                    player.location,
                                    3,
                                    0.75,
                                    0.75,
                                    0.75,
                                    0.0
                                )
                                player.world.spawnParticle(
                                    Particle.ELECTRIC_SPARK,
                                    player.location,
                                    5,
                                    0.75,
                                    0.75,
                                    0.75,
                                    0.0
                                )
                                timer++
                            }
                        }
                    }.runTaskTimer(plugin, 0L, 1L)
                }
                // Scientist
                BurbAbility.ZOMBIES_HEALER_ABILITY_1 -> {
                    val nearbyTeammates = player.getNearbyEntities(4.0, 4.0, 4.0).filterIsInstance<Player>().filter { p -> p.burbPlayer().playerTeam == Teams.ZOMBIES }.sortedBy { p -> player.location.distanceSquared(p.location) }
                    if(nearbyTeammates.isNotEmpty()) {
                        val healingTeammate = nearbyTeammates[0]
                        player.sendActionBar(Formatting.allTags.deserialize("<yellow>Healing attached to ${healingTeammate.name}"))
                        player.setCooldown(usedItem, 20 * 3600)
                        object : BukkitRunnable() {
                            var timer = 0
                            var ticks = 0
                            override fun run() {
                                if(!player.burbPlayer().isDead && !healingTeammate.burbPlayer().isDead) {
                                    if(player.location.distanceSquared(healingTeammate.location) <= 64.0 || timer <= 5 || GameManager.getGameState() !in listOf(
                                            GameState.IN_GAME, GameState.OVERTIME)) {
                                        if(ticks % 5 == 0) {
                                            if(player.location.distanceSquared(healingTeammate.location) > 64.0) {
                                                player.sendActionBar(Formatting.allTags.deserialize("<red>Healing requirements failed or timer exceeded"))
                                                player.setCooldown(usedItem, usedItem.persistentDataContainer.get(
                                                    NamespacedKey(plugin, "burb.ability.cooldown"), PersistentDataType.INTEGER)!!)
                                                cancel()
                                            }
                                            val startLoc = player.location.add(0.0, 0.25, 0.0)
                                            val endLoc = healingTeammate.location.add(0.0, 0.25, 0.0)
                                            val steps = 10
                                            val xIncrement = (endLoc.x - startLoc.x) / steps
                                            val yIncrement = (endLoc.y - startLoc.y) / steps
                                            val zIncrement = (endLoc.z - startLoc.z) / steps

                                            for (i in 0..steps) {
                                                val x = startLoc.x + xIncrement * i
                                                var y = startLoc.y + yIncrement * i
                                                val z = startLoc.z + zIncrement * i

                                                y += sin(Math.PI * i / steps) * 0.05
                                                val particleLoc = Location(startLoc.world, x, y, z)
                                                player.world.spawnParticle(
                                                    Particle.DUST,
                                                    particleLoc,
                                                    0,
                                                    0.0,
                                                    0.0,
                                                    0.0,
                                                    Particle.DustOptions(Color.PURPLE, 1.5f)
                                                )
                                            }
                                            if(healingTeammate.health >= 19.5) {
                                                healingTeammate.health = 20.0
                                                player.sendActionBar(Formatting.allTags.deserialize("<green>${healingTeammate.name} fully healed"))
                                                player.setCooldown(usedItem.type, usedItem.persistentDataContainer.get(
                                                    NamespacedKey(plugin, "burb.ability.cooldown"), PersistentDataType.INTEGER)!!)
                                                cancel()
                                            } else {
                                                healingTeammate.health += 0.5
                                                player.sendActionBar(Formatting.allTags.deserialize("<green>Healed ${healingTeammate.name} for 0.75<red>${ChatUtility.HEART_UNICODE}"))
                                            }
                                        }
                                    } else {
                                        player.sendActionBar(Formatting.allTags.deserialize("<red>Healing requirements failed or timer exceeded"))
                                        player.setCooldown(usedItem, usedItem.persistentDataContainer.get(
                                            NamespacedKey(
                                                plugin,
                                                "burb.ability.cooldown"
                                            ), PersistentDataType.INTEGER)!!)
                                        cancel()
                                    }
                                } else {
                                    player.sendActionBar(Formatting.allTags.deserialize("<red>Healing requirements failed or timer exceeded"))
                                    player.setCooldown(usedItem, usedItem.persistentDataContainer.get(
                                        NamespacedKey(
                                            plugin,
                                            "burb.ability.cooldown"
                                        ), PersistentDataType.INTEGER)!!)
                                    cancel()
                                }
                                ticks++
                                if(ticks >= 20) {
                                    ticks = 0
                                    timer++
                                }
                            }
                        }.runTaskTimer(plugin, 0L, 1L)
                    } else {
                        player.setCooldown(usedItem, 0)
                        player.sendActionBar(Formatting.allTags.deserialize("<red>No team mate in range"))
                    }
                }
                BurbAbility.ZOMBIES_HEALER_ABILITY_2 -> {
                    if(player.location.block.getRelative(BlockFace.DOWN).type != Material.AIR) {
                        player.world.playSound(player.location, "entity.enderman.teleport", SoundCategory.VOICE, 1f, 0.75f)
                        object : BukkitRunnable() {
                            var primeTime = 5
                            var timer = 0
                            val scienceMineEntity = player.world.spawn(player.location.clone().setRotation(
                                Rotation.rotation(player.yaw + 180, 0f)), ItemDisplay::class.java).apply {
                                val scienceMine = ItemStack(Material.PURPLE_GLAZED_TERRACOTTA)
                                val scienceMineMeta = scienceMine.itemMeta
                                scienceMine.itemMeta = scienceMineMeta
                                setItemStack(scienceMine)
                                teleportDuration = 5
                                brightness = Display.Brightness(15, 15)
                                transformation = Transformation(
                                    transformation.translation,
                                    transformation.leftRotation,
                                    transformation.scale,
                                    transformation.rightRotation
                                )
                                addScoreboardTag("${player.uniqueId}.science_mine")
                            }
                            override fun run() {
                                if(timer < primeTime) {
                                    timer++
                                } else {
                                    object : BukkitRunnable() {
                                        var ticks = 0
                                        var seconds = 0
                                        override fun run() {
                                            if(ticks == 0 && seconds == 0) {
                                                scienceMineEntity.teleport(scienceMineEntity.location.clone().add(Vector(0.0, 0.5, 0.0)))
                                                scienceMineEntity.world.playSound(scienceMineEntity.location, "entity.enderman.teleport", SoundCategory.VOICE, 1f, 0.5f)
                                            }
                                            if(!scienceMineEntity.isDead) {
                                                val nearbyEnemies = scienceMineEntity.location.getNearbyPlayers(0.9).filter { p -> p.burbPlayer().playerTeam == Teams.PLANTS && !p.burbPlayer().isDead }
                                                if(nearbyEnemies.isNotEmpty()) {
                                                    scienceMineEntity.world.createExplosion(player, scienceMineEntity.location, 2.75f, false, false)
                                                    scienceMineEntity.world.playSound(scienceMineEntity.location, "entity.enderman.teleport", SoundCategory.VOICE, 1f, 0.5f)
                                                    scienceMineEntity.remove()
                                                    cancel()
                                                }
                                                if(seconds >= 60) {
                                                    scienceMineEntity.world.playSound(scienceMineEntity.location, "entity.enderman.teleport", SoundCategory.VOICE, 1f, 0.5f)
                                                    scienceMineEntity.remove()
                                                    cancel()
                                                }
                                            } else {
                                                scienceMineEntity.world.playSound(scienceMineEntity.location, "entity.enderman.teleport", SoundCategory.VOICE, 1f, 0.5f)
                                                scienceMineEntity.remove()
                                                cancel()
                                            }
                                            ticks++
                                            if(ticks >= 20) {
                                                ticks = 0
                                                seconds++
                                            }
                                        }
                                    }.runTaskTimer(plugin, 0L, 1L)
                                    cancel()
                                }
                            }
                        }.runTaskTimer(plugin, 0L, 20L)
                    } else {
                        player.setCooldown(usedItem, 0)
                        player.sendActionBar(Formatting.allTags.deserialize("<red>${BurbAbility.ZOMBIES_HEALER_ABILITY_3.abilityName} must be placed on solid ground."))
                    }
            }
                BurbAbility.ZOMBIES_HEALER_ABILITY_3 -> {
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
                // Deadbeard
                BurbAbility.ZOMBIES_RANGED_ABILITY_1 -> {
                    player.world.playSound(player.location, "block.barrel.open", SoundCategory.VOICE, 1f, 0.8f)
                    player.addPotionEffect(PotionEffect(PotionEffectType.SLOWNESS, 5 * 20, 1, false, false))
                    object : BukkitRunnable() {
                        var timer = 0
                        val barrelEntity = player.world.spawn(player.location.clone(), ItemDisplay::class.java).apply {
                            setItemStack(ItemStack(Material.BARREL))
                            teleportDuration = 2
                            brightness = Display.Brightness(15, 15)
                            transformation = Transformation(
                                Vector3f(0f, 0.5f, 0f),
                                transformation.leftRotation,
                                Vector3f(1.5f, 1.5f, 1.5f),
                                transformation.rightRotation
                            )
                            addScoreboardTag("${player.uniqueId}.barrel_blast")
                        }
                        override fun run() {
                            player.addPassenger(barrelEntity)
                            if(player.burbPlayer().isDead || player.vehicle != null || !player.isOnline) {
                                player.world.playSound(player.location, "block.barrel.close", SoundCategory.VOICE, 1f, 0.8f)
                                player.removePotionEffect(PotionEffectType.SLOWNESS)
                                barrelEntity.eject()
                                barrelEntity.remove()
                                cancel()
                            }
                            if(timer >= 5 * 20) {
                                player.world.playSound(player.location, "block.barrel.close", SoundCategory.VOICE, 1f, 0.8f)
                                player.removePotionEffect(PotionEffectType.SLOWNESS)
                                player.world.createExplosion(player, player.location, 2.5f, false, false)
                                player.velocity = player.velocity.setY(1.5)
                                barrelEntity.eject()
                                barrelEntity.remove()
                                cancel()
                            }
                            if(timer in (0 * 20)..(2 * 20)) {
                                if(timer % 10 == 0) {
                                    player.world.playSound(player.location, "block.note_block.snare", SoundCategory.VOICE, 1f, 1f)
                                }
                            }
                            if(timer in (2 * 20)..(3 * 20)) {
                                if(timer % 5 == 0) {
                                    player.world.playSound(player.location, "block.note_block.snare", SoundCategory.VOICE, 1f, 1.25f)
                                }
                            }
                            if(timer in (4 * 20)..(5 * 20)) {
                                if(timer % 2 == 0) {
                                    player.world.playSound(player.location, "block.note_block.snare", SoundCategory.VOICE, 1f, 1.5f)
                                }
                            }
                            timer++
                        }
                    }.runTaskTimer(plugin, 0L, 1L)
                    // spawn barrel entity on player, maybe even make it a passenger with offset, timer and explode and shoot player upwards on explosion
                }
                BurbAbility.ZOMBIES_RANGED_ABILITY_2 -> {
                    if(player.location.block.getRelative(BlockFace.DOWN).type != Material.AIR && player.location.block.getRelative(
                            BlockFace.DOWN).isSolid) {
                        player.world.playSound(player.location, "entity.firework_rocket.twinkle_far", SoundCategory.VOICE, 2f, 1.75f)
                        ItemManager.clearItems(player)
                        player.inventory.setItemInMainHand(ItemStack(Material.BREEZE_ROD, 1))
                        player.inventory.setItem(player.inventory.heldItemSlot + 1, ItemStack(Material.BREEZE_ROD, 1))
                        player.inventory.setItem(player.inventory.heldItemSlot - 1, ItemStack(Material.BREEZE_ROD, 1))
                        object : BukkitRunnable() {
                            var bulletsRemaining = 12
                            val cannonRodeoVehicle = player.location.world.spawn(player.location.clone(), ItemDisplay::class.java).apply {
                                addScoreboardTag("${player.uniqueId}-cannon-rodeo-vehicle")
                                player.teleport(this)
                                addPassenger(player)
                            }
                            override fun run() {
                                if(!player.isOnline) {
                                    cannonRodeoVehicle.remove()
                                    cancel()
                                }
                                if(player.vehicle == cannonRodeoVehicle) {
                                    player.sendActionBar(Formatting.allTags.deserialize(Translation.Weapon.CANNON_RODEO_CONTROLS.replace("%s", bulletsRemaining.toString())))
                                    cannonRodeoVehicle.setRotation(player.yaw, player.pitch)
                                }
                                if(player.isSneaking && player.inventory.itemInMainHand.type == Material.BREEZE_ROD) {
                                    val snowball = player.world.spawn(player.eyeLocation.clone(), Snowball::class.java)
                                    snowball.shooter = player
                                    snowball.location.direction = player.location.direction
                                    val snowballVelocity = player.location.direction.multiply(2)
                                    snowball.velocity = snowballVelocity
                                    snowball.persistentDataContainer.set(NamespacedKey(plugin, "burb.weapon.damage"), PersistentDataType.DOUBLE, 0.00001)
                                    object : BukkitRunnable() {
                                        override fun run() {
                                            if(snowball.isDead || !player.isOnline) {
                                                snowball.location.world.createExplosion(player, snowball.location.clone(), 1.5f, false, false)
                                                this.cancel()
                                            } else {
                                                snowball.location.world.spawnParticle(
                                                    Particle.DUST,
                                                    snowball.location,
                                                    1, 0.0, 0.0, 0.0,
                                                    Particle.DustOptions(Color.PURPLE, 0.75f)
                                                )
                                            }
                                        }
                                    }.runTaskTimer(plugin, 1L, 1L)
                                    player.world.playSound(player.location, "entity.firework_rocket.launch", SoundCategory.VOICE, 2f, 1.75f)
                                    bulletsRemaining--
                                }
                                if(player.inventory.itemInMainHand.type != Material.BREEZE_ROD || player.burbPlayer().isDead || bulletsRemaining <= 0 || GameManager.getGameState() !in listOf(
                                        GameState.IN_GAME, GameState.OVERTIME)) {
                                    player.sendActionBar(Formatting.allTags.deserialize(""))
                                    cannonRodeoVehicle.eject()
                                    cannonRodeoVehicle.remove()
                                    player.inventory.remove(Material.BREEZE_ROD)
                                    player.velocity = player.velocity.add(Vector(0.0, 0.5, 0.0))
                                    ItemManager.giveCharacterItems(player)
                                    player.setCooldown(usedItem.type, usedItem.persistentDataContainer.get(
                                        NamespacedKey(
                                            plugin,
                                            "burb.ability.cooldown"
                                        ), PersistentDataType.INTEGER)!!)
                                    if(bulletsRemaining == 8) player.setCooldown(BurbAbility.ZOMBIES_RANGED_ABILITY_3.abilityMaterial, 0)
                                    player.world.playSound(player.location, "entity.firework_rocket.shoot", SoundCategory.VOICE, 2f, 1.5f)
                                    cancel()
                                }
                            }
                        }.runTaskTimer(plugin, 0L, 15L)
                    } else {
                        player.setCooldown(usedItem, 0)
                        player.sendActionBar(Formatting.allTags.deserialize("<red>You cannot use this ability while in the air."))
                    }
                }
                BurbAbility.ZOMBIES_RANGED_ABILITY_3 -> {
                    player.world.playSound(player.location, "entity.wither.shoot", SoundCategory.VOICE, 1f, 1.25f)
                    player.location.clone().world.spawnParticle(
                        Particle.DUST,
                        player.location,
                        400, 2.5, 2.0, 2.5, 0.0,
                        Particle.DustOptions(Color.GRAY, 8f)
                    )
                    player.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, 20 * 5, 0, false, false))
                    player.velocity = player.velocity.add(
                        Vector(
                            player.location.direction.x * -1.25,
                            1.5,
                            player.location.direction.z * -1.25
                        )
                    )
                    player.inventory.helmet = null
                    player.inventory.boots = null
                    player.inventory.setItemInOffHand(null)
                    player.burbPlayer().playerCharacter.characterAbilities.abilitySet.forEach { ability -> player.setCooldown(ability.abilityMaterial, 20 * 5) }
                    player.setCooldown(player.burbPlayer().playerCharacter.characterMainWeapon.material, 20 * 5)
                    object: BukkitRunnable() {
                        override fun run() {
                            if(!player.burbPlayer().isDead) {
                                ItemManager.givePlayerTeamBoots(player, player.burbPlayer().playerTeam)
                                BurbCosmetics.equipCosmetics(player)
                            }
                        }
                    }.runTaskLater(plugin, 20L * 5L)
                }
                else -> {}
            }
        }
    }
}