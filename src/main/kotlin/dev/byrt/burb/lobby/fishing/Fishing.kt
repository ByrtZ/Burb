package dev.byrt.burb.lobby.fishing

import dev.byrt.burb.item.rarity.ItemRarity
import dev.byrt.burb.item.type.ItemType
import dev.byrt.burb.item.rarity.SubRarity
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.logger
import dev.byrt.burb.player.PlayerVisuals
import dev.byrt.burb.plugin
import dev.byrt.burb.text.Formatting.allTags
import dev.byrt.burb.util.Keys.FISH_IS_OBFUSCATED
import dev.byrt.burb.util.Keys.FISH_IS_SHADOW
import dev.byrt.burb.util.Keys.FISH_IS_SHINY
import dev.byrt.burb.util.Keys.FISH_RARITY
import dev.byrt.burb.util.extension.startsWithVowel
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.title.Title
import org.bukkit.*
import org.bukkit.entity.*
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import java.time.Duration
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

object Fishing {
    fun catchFish(
        player: Player,
        item: Item,
        location: Location,
        forcedFishRarity: FishRarity?,
        forcedFishSubRarity: SubRarity?
    ) {
        val fishRarity = forcedFishRarity ?: FishRarity.getRandomRarity()
        val subRarity = forcedFishSubRarity ?: SubRarity.getRandomSubRarity()

        val fishMeta = item.itemStack.itemMeta
        val lore = mutableListOf<String>()

        // The number of fish at this point is always one lower than the one shown in the statistics screen.
        val fishNumber = player.getStatistic(Statistic.FISH_CAUGHT) + 1

        lore += "<reset><!i><white>${fishRarity.itemRarity.asMiniMesssage()}${if (subRarity != SubRarity.NONE) subRarity.asMiniMesssage() else ""}${ItemType.FISH.asMiniMesssage()}"

        if (subRarity != SubRarity.NONE) {
            when (subRarity) {
                SubRarity.SHINY -> {
                    lore += "<!i><white>Caught by <yellow>${player.name}<white>."
                    lore += "<!i><white>Fish #${fishNumber}"

                    fishMeta.setEnchantmentGlintOverride(true)
                    fishMeta.persistentDataContainer.set(FISH_IS_SHINY, PersistentDataType.BOOLEAN, true)
                    shinyEffect(item)
                }
                SubRarity.SHADOW -> {
                    lore += "<!i><#0><shadow:white>Caught by <shadow:yellow>${player.name}<shadow:white>."
                    lore += "<!i><#0><shadow:white>Fish #${fishNumber}"

                    fishMeta.persistentDataContainer.set(FISH_IS_SHADOW, PersistentDataType.BOOLEAN, true)
                    shadowEffect(item)
                }
                SubRarity.OBFUSCATED -> {
                    lore += "<!i><white><font:alt>Caught by <yellow>${player.name}</font:alt>."
                    lore += "<!i><white><font:alt>Fish</font:alt> #${fishNumber}"

                    fishMeta.persistentDataContainer.set(FISH_IS_OBFUSCATED, PersistentDataType.BOOLEAN, true)
                    obfuscatedEffect(item)
                }
            }
        } else {
            if (fishRarity.props.showCatcher) {
                lore += "<!i><white>Caught by <yellow>${player.name}<white>."
            }
            if (fishRarity.props.showFishNumber) {
                lore += "<!i><white>Fish #${fishNumber}"
            }
        }
        fishMeta.displayName(
            allTags.deserialize("${if (subRarity == SubRarity.SHADOW) "<#0><shadow:${fishRarity.itemRarity.rarityColour}>" else "<${fishRarity.itemRarity.rarityColour}>"}${if (subRarity == SubRarity.OBFUSCATED) "<font:alt>" else ""}${item.name}")
                .decoration(TextDecoration.ITALIC, false)
        )
        fishMeta.lore(
            lore.map { allTags.deserialize(it) }
        )
        fishMeta.persistentDataContainer.set(FISH_RARITY, PersistentDataType.STRING, fishRarity.name)
        item.itemStack.setItemMeta(fishMeta)

        player.sendActionBar(
            allTags.deserialize("Caught <${fishRarity.itemRarity.rarityColour}><b>${fishRarity.itemRarity.name.uppercase()}</b> ")
                .append(item.itemStack.effectiveName()).append(allTags.deserialize("<reset>."))
        )

        if (fishRarity.props.sendGlobalMsg || subRarity != SubRarity.NONE) catchText(player, item, fishRarity)
        if (fishRarity.props.sendGlobalTitle) catchTitle(player, item, fishRarity)
        if (fishRarity.props.isAnimated) catchAnimation(player, item, location.add(0.0, 1.75, 0.0), fishRarity)
        if (fishRarity in listOf(
                FishRarity.LEGENDARY,
                FishRarity.MYTHIC,
                FishRarity.UNREAL,
                FishRarity.TRANSCENDENT,
                FishRarity.CELESTIAL
            )
        ) logger.info("(FISHING) ${player.name} caught $fishRarity ${item.name}.")
        if (subRarity != SubRarity.NONE) logger.info("(FISHING) ${player.name} caught $subRarity ${item.name}.")
    }

    private fun catchText(catcher: Player, item: Item, fishRarity: FishRarity) {
        Bukkit.getServer().sendMessage(
            playerCaughtFishComponent(fishRarity, catcher, item)
        )
    }

    private fun catchTitle(catcher: Player, item: Item, fishRarity: FishRarity) {
        Bukkit.getServer().showTitle(
            Title.title(
                allTags.deserialize("<${fishRarity.itemRarity.rarityColour}><b>${fishRarity.itemRarity.rarityName.uppercase()}<reset>"),
                playerCaughtFishComponent(fishRarity, catcher, item),
                Title.Times.times(Duration.ofMillis(250L), Duration.ofSeconds(3L), Duration.ofMillis(250L))
            )
        )
    }

    private fun playerCaughtFishComponent(
        fishRarity: FishRarity,
        catcher: Player,
        item: Item
    ) = allTags.deserialize(
        "<burbcolour>${catcher.name}<reset> caught a${
            if (fishRarity.itemRarity.rarityName.startsWithVowel()) "n " else " "
        }<${fishRarity.itemRarity.rarityColour}><b>${fishRarity.name}</b> "
    ).append(item.itemStack.effectiveName().hoverEvent(item.itemStack)).append(Component.text("."))

    private fun catchAnimation(catcher: Player, item: Item, location: Location, fishRarity: FishRarity) {
        when (fishRarity) {
            FishRarity.RARE -> {
                PlayerVisuals.firework(
                    location,
                    flicker = false,
                    trail = false,
                    fishRarity.itemRarity.colour,
                    FireworkEffect.Type.BURST,
                    false
                )
            }

            FishRarity.EPIC -> {
                catcher.playSound(Sounds.Fishing.EPIC_CATCH)
                PlayerVisuals.firework(
                    location,
                    flicker = false,
                    trail = false,
                    fishRarity.itemRarity.colour,
                    FireworkEffect.Type.BALL,
                    false
                )
                epicEffect(location)
            }

            FishRarity.LEGENDARY -> {
                Bukkit.getServer().playSound(Sounds.Fishing.LEGENDARY_CATCH)
                for (i in 0..2) {
                    object : BukkitRunnable() {
                        override fun run() {
                            PlayerVisuals.firework(
                                location,
                                flicker = true,
                                trail = true,
                                fishRarity.itemRarity.colour,
                                FireworkEffect.Type.BALL_LARGE,
                                false
                            )
                        }
                    }.runTaskLater(plugin, i * 2L)
                }
                legendaryEffect(location)
            }

            FishRarity.MYTHIC -> {
                Bukkit.getServer().playSound(Sounds.Fishing.MYTHIC_CATCH)
                mythicEffect(location)
            }

            FishRarity.UNREAL -> {
                val server = Bukkit.getServer()
                server.playSound(Sounds.Fishing.UNREAL_CATCH)
                server.playSound(Sounds.Fishing.UNREAL_CATCH_SPAWN)
                val previousDayTime = catcher.world.time
                val previousFullTime = catcher.world.fullTime
                if (catcher.world.time < 6000) catcher.world.time += 6000 - catcher.world.time
                if (catcher.world.time > 6000) catcher.world.time -= 6000 + catcher.world.time
                for (i in 0..15) {
                    object : BukkitRunnable() {
                        val startLoc = item.location.clone()
                        override fun run() {
                            startLoc.world.spawnParticle(
                                Particle.SONIC_BOOM,
                                startLoc.add(0.0, i.toDouble(), 0.0),
                                1,
                                0.0,
                                0.0,
                                0.0,
                                0.0
                            )
                            if (i == 15) {
                                unrealEffect(startLoc)
                                startLoc.world.spawnParticle(
                                    Particle.SOUL_FIRE_FLAME,
                                    startLoc,
                                    100,
                                    0.0,
                                    0.0,
                                    0.0,
                                    0.35
                                )
                                startLoc.world.spawnParticle(Particle.SCULK_SOUL, startLoc, 100, 0.0, 0.0, 0.0, 0.40)
                                for (player in Bukkit.getOnlinePlayers()) player.playSound(Sounds.Fishing.UNREAL_CATCH_SPAWN_BATS)
                            }
                        }
                    }.runTaskLater(plugin, i * 2L)
                }
                for (i in 0..19) {
                    object : BukkitRunnable() {
                        override fun run() {
                            catcher.world.strikeLightningEffect(
                                item.location.set(
                                    item.location.x,
                                    -64.0,
                                    item.location.z
                                )
                            )
                            if (i % 2 == 0) {
                                catcher.world.time += 12000
                                item.isGlowing = true
                            } else {
                                catcher.world.time -= 12000
                                item.isGlowing = false
                            }
                            if (i == 19) {
                                catcher.world.fullTime = previousFullTime
                                catcher.world.time = previousDayTime
                            }
                        }
                    }.runTaskLater(plugin, i * 15L)
                }
            }

            FishRarity.TRANSCENDENT -> {
                Bukkit.getServer().playSound(Sounds.Fishing.TRANSCENDENT_CATCH)
                Bukkit.getServer().playSound(Sounds.Fishing.TRANSCENDENT_CATCH_SPAWN)
                object : BukkitRunnable() {
                    val radius = 5.0
                    val vertices = transcendentGenerateVertices(radius)
                    val edges = transcendentGetEdges()
                    var angle = 0.0
                    var time = 0
                    override fun run() {
                        if (time++ >= 300) cancel()
                        val rotated = vertices.map { transcendentRotateY(it, angle) }
                        rotated.forEach { point ->
                            location.world.spawnParticle(
                                Particle.DUST,
                                location.clone().add(point),
                                1, 0.0, 0.0, 0.0, 0.0,
                                Particle.DustOptions(Color.RED, 1.5f),
                                true
                            )
                        }
                        edges.forEach { (i, j) ->
                            transcendentDrawEdge(location, rotated[i], rotated[j], 8, Color.RED)
                        }
                        if (time % 5 == 0) {
                            location.world.playSound(location, "entity.blaze.ambient", 2f, 0.75f)
                            val origin = rotated.random().clone()
                            val direction =
                                Vector(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5).normalize()
                                    .multiply(12.0)
                            val startLoc = location.clone().add(origin)
                            val steps = 16
                            val delta = direction.clone().multiply(1.0 / steps)

                            for (i in 0..steps) {
                                val point = startLoc.clone().add(delta.clone().multiply(i))
                                location.world.spawnParticle(
                                    Particle.DUST,
                                    point,
                                    1, 0.0, 0.0, 0.0, 0.0,
                                    Particle.DustOptions(Color.RED, 1.2f),
                                    true
                                )
                            }
                            PlayerVisuals.firework(
                                startLoc.clone().add(direction),
                                flicker = false,
                                trail = false,
                                fishRarity.itemRarity.colour,
                                FireworkEffect.Type.BURST,
                                variedVelocity = false
                            )
                        }
                        angle += Math.toRadians(4.0)
                    }


                }.runTaskTimer(plugin, 0L, 2L)
            }

            FishRarity.CELESTIAL -> {
                Bukkit.getServer().playSound(Sounds.Fishing.CELESTIAL_CATCH)
                Bukkit.getServer().playSound(Sounds.Fishing.CELESTIAL_CATCH_SPAWN)
                PlayerVisuals.firework(
                    location,
                    flicker = true,
                    trail = true,
                    fishRarity.itemRarity.colour,
                    FireworkEffect.Type.BALL_LARGE,
                    variedVelocity = false
                )
                var riseHeight = location.blockY
                object : BukkitRunnable() {
                    override fun run() {
                        if (riseHeight < location.blockY + 100) {
                            PlayerVisuals.firework(
                                Location(location.world, location.x, riseHeight.toDouble(), location.z),
                                flicker = false,
                                trail = false,
                                Color.GRAY,
                                FireworkEffect.Type.BALL,
                                variedVelocity = false
                            )
                            riseHeight += 2
                        } else {
                            cancel()
                            for (i in 0..3) {
                                location.world.spawnParticle(
                                    Particle.CLOUD,
                                    location.clone().add(0.0, 100.0, 0.0),
                                    2500, 0.0, 0.0, 0.0, 0.5, null, true
                                )
                            }

                            val radius = 12.0
                            val step = Math.PI / 16
                            for (angle in 0 until 12) {
                                val x = radius * cos(angle * step)
                                val z = radius * sin(angle * step)
                                location.world.strikeLightningEffect(location.clone().add(x, 100.0, z))
                                location.world.spawnParticle(
                                    Particle.FLASH,
                                    location.clone().add(x, 100.0, z),
                                    1, 0.0, 0.0, 0.0, 0.0, Color.WHITE, true
                                )
                            }
                            for (i in 0..39) {
                                object : BukkitRunnable() {
                                    override fun run() {
                                        val randomX = Random.nextDouble(location.x - 20.0, location.x + 20.0)
                                        val randomZ = Random.nextDouble(location.z - 20.0, location.z + 20.0)
                                        var descendHeight = location.blockY + 100
                                        object : BukkitRunnable() {
                                            override fun run() {
                                                if (descendHeight > location.blockY) {
                                                    PlayerVisuals.firework(
                                                        Location(
                                                            location.world,
                                                            randomX,
                                                            descendHeight.toDouble(),
                                                            randomZ
                                                        ),
                                                        flicker = false,
                                                        trail = false,
                                                        fishRarity.itemRarity.colour,
                                                        FireworkEffect.Type.BALL,
                                                        variedVelocity = false
                                                    )
                                                    descendHeight -= 2
                                                } else {
                                                    location.world.playSound(
                                                        Location(
                                                            location.world,
                                                            randomX,
                                                            descendHeight.toDouble(),
                                                            randomZ
                                                        ), "item.totem.use", 1f, 0.75f
                                                    )
                                                    location.world.spawnParticle(
                                                        Particle.TOTEM_OF_UNDYING,
                                                        Location(
                                                            location.world,
                                                            randomX,
                                                            descendHeight.toDouble(),
                                                            randomZ
                                                        ),
                                                        250, 0.0, 0.0, 0.0, 0.75, null, true
                                                    )
                                                    cancel()
                                                }
                                            }
                                        }.runTaskTimer(plugin, 0L, 4L)
                                    }
                                }.runTaskLater(plugin, 0L + (i.toLong() * 15))
                            }
                        }
                    }
                }.runTaskTimer(plugin, 0L, 1L)
            }

            else -> { /* do nothing */
            }
        }
    }

    fun transcendentGenerateVertices(r: Double): List<Vector> {
        val phi = (1 + sqrt(5.0)) / 2
        val a = 1.0 / sqrt(3.0)
        val b = a / phi
        val c = a * phi
        return listOf(
            Vector(a, a, a), Vector(a, a, -a), Vector(a, -a, a), Vector(a, -a, -a),
            Vector(-a, a, a), Vector(-a, a, -a), Vector(-a, -a, a), Vector(-a, -a, -a),
            Vector(0.0, b, c), Vector(0.0, b, -c), Vector(0.0, -b, c), Vector(0.0, -b, -c),
            Vector(b, c, 0.0), Vector(b, -c, 0.0), Vector(-b, c, 0.0), Vector(-b, -c, 0.0),
            Vector(c, 0.0, b), Vector(c, 0.0, -b), Vector(-c, 0.0, b), Vector(-c, 0.0, -b)
        ).map { it.multiply(r) }
    }

    fun transcendentGetEdges(): List<Pair<Int, Int>> = listOf(
        0 to 8, 0 to 12, 0 to 16, 1 to 9, 1 to 12, 1 to 17, 2 to 10, 2 to 13, 2 to 16,
        3 to 11, 3 to 13, 3 to 17, 4 to 8, 4 to 14, 4 to 18, 5 to 9, 5 to 14, 5 to 19,
        6 to 10, 6 to 15, 6 to 18, 7 to 11, 7 to 15, 7 to 19, 8 to 10, 9 to 11,
        12 to 14, 13 to 15, 16 to 17, 18 to 19
    )

    fun transcendentRotateY(v: Vector, angle: Double): Vector {
        val cos = cos(angle)
        val sin = sin(angle)
        return Vector(v.x * cos - v.z * sin, v.y, v.x * sin + v.z * cos)
    }

    fun transcendentDrawEdge(origin: Location, start: Vector, end: Vector, steps: Int, color: Color) {
        val delta = end.clone().subtract(start).multiply(1.0 / steps)
        val world = origin.world
        for (i in 0..steps) {
            val point = start.clone().add(delta.clone().multiply(i))
            world.spawnParticle(
                Particle.DUST,
                origin.clone().add(point),
                1, 0.0, 0.0, 0.0, 0.0,
                Particle.DustOptions(color, 1.0f),
                true
            )
        }
    }

    private fun epicEffect(location: Location) {
        object : BukkitRunnable() {
            var radius = 0.0
            override fun run() {
                if (radius > 4.0) {
                    cancel()
                    return
                }
                val step = Math.PI / 16
                for (angle in 0 until 32) {
                    val x = radius * cos(angle * step)
                    val z = radius * sin(angle * step)
                    val particleLocation = location.clone().add(x, -0.75, z)
                    location.world.spawnParticle(Particle.WITCH, particleLocation, 1, 0.0, 0.0, 0.0, 0.0)
                }
                radius += 0.2
            }
        }.runTaskTimer(plugin, 0L, 2L)
    }

    fun legendaryEffect(location: Location) {
        val effectLoc = location.clone()
        for (i in 0..3) {
            object : BukkitRunnable() {
                override fun run() {
                    effectLoc.world.playSound(Sounds.Fishing.LEGENDARY_CATCH_EXPLODE)
                    effectLoc.world.spawnParticle(
                        Particle.EXPLOSION,
                        effectLoc.add(
                            Random.nextDouble(-0.25, 0.25),
                            Random.nextDouble(-0.25, 0.25),
                            Random.nextDouble(-0.25, 0.25)
                        ),
                        1,
                        0.0,
                        0.0,
                        0.0,
                        0.0
                    )
                }
            }.runTaskLater(plugin, (i * 4L) + 35L)
        }

        for (i in 0..20) {
            object : BukkitRunnable() {
                override fun run() {
                    for (r in 0..3) {
                        for (j in 0 until 32) {
                            val angle = 2 * Math.PI * j / 32
                            val x = r * cos(angle)
                            val z = r * sin(angle)
                            val particleLocation = location.clone().add(x, -1.5, z)
                            location.world?.spawnParticle(Particle.FLAME, particleLocation, 1, 0.0, 0.0, 0.0, 0.0)
                        }
                    }
                }
            }.runTaskLater(plugin, i * 2L)
        }
    }

    fun mythicEffect(location: Location) {
        for (i in 0..15) {
            object : BukkitRunnable() {
                override fun run() {
                    PlayerVisuals.firework(
                        location,
                        flicker = true,
                        trail = false,
                        ItemRarity.MYTHIC.colour,
                        if (i <= 11) FireworkEffect.Type.BALL_LARGE else FireworkEffect.Type.BALL,
                        false
                    )
                }
            }.runTaskLater(plugin, i * 2L)
        }
        for (i in 0..60) {
            object : BukkitRunnable() {
                override fun run() {
                    PlayerVisuals.firework(
                        location,
                        i % 2 == 0,
                        i % 3 == 0,
                        ItemRarity.MYTHIC.colour,
                        if (i % 2 == 0) FireworkEffect.Type.BALL_LARGE else FireworkEffect.Type.BALL,
                        true
                    )
                }
            }.runTaskLater(plugin, (i * 3L) + 30L)
        }
    }

    private fun unrealEffect(location: Location) {
        fun getSoul(location: Location): Bat {
            val bat = location.world.spawnEntity(location, EntityType.BAT) as Bat
            bat.isAwake = true
            bat.isSilent = true
            bat.isInvisible = true
            bat.isInvulnerable = true
            bat.addScoreboardTag("soul.bat.${bat.uniqueId}")
            return bat
        }
        object : BukkitRunnable() {
            val soulAmount = 20
            var timer = 0
            val souls = ArrayList<Bat>()
            override fun run() {
                if (timer <= soulAmount) {
                    val soul = getSoul(location)
                    souls.add(soul)
                    soul.velocity = Vector(0.0, 0.15, 0.0)
                }
                for (soul in souls) soul.world.spawnParticle(Particle.SCULK_SOUL, soul.location, 2, 0.0, 0.0, 0.0, 0.0)
                if (timer >= 14 * 20) {
                    for (soul in souls) soul.remove()
                    souls.clear()
                    this.cancel()
                } else {
                    timer++
                }
            }
        }.runTaskTimer(plugin, 0L, 1L)

        object : BukkitRunnable() {
            var i = 0
            var loc = location.clone()
            var radius = 0.0
            var y = 0.0
            override fun run() {
                val x = radius * cos(y)
                val z = radius * sin(y)
                if (i % 2 == 0) PlayerVisuals.firework(
                    Location(location.world, loc.x + x, loc.y + y, loc.z + z),
                    flicker = false,
                    trail = false,
                    ItemRarity.UNREAL.colour,
                    FireworkEffect.Type.BALL,
                    false
                )

                y += if (y >= 2.0) 0.1 else 0.05
                radius += if (radius >= 1.5) 0.08 else 0.15

                if (y >= 25) cancel()
                i++
            }
        }.runTaskTimer(plugin, 0L, 1L)
    }

    fun shinyEffect(item: Item) {
        Bukkit.getServer().playSound(Sounds.Fishing.SHINY_CATCH)
        object : BukkitRunnable() {
            var i = 0
            override fun run() {
                if (i % 2 == 0) {
                    item.location.world.spawnParticle(
                        Particle.ELECTRIC_SPARK,
                        item.location.clone().add(0.0, 0.5, 0.0),
                        10, 0.25, 0.25, 0.25, 0.0
                    )
                }
                if (i >= 100 || item.isDead) {
                    cancel()
                }
                i++
            }
        }.runTaskTimer(plugin, 0L, 5L)
    }

    fun shadowEffect(item: Item) {
        Bukkit.getServer().playSound(Sounds.Fishing.SHADOW_CATCH)
        object : BukkitRunnable() {
            var i = 0
            override fun run() {
                if (i % 2 == 0) {
                    item.location.world.spawnParticle(
                        Particle.SMOKE,
                        item.location.clone().add(0.0, 0.5, 0.0),
                        20, 0.0, 0.0, 0.0, 0.05
                    )
                }
                if (i >= 100 || item.isDead) {
                    cancel()
                }
                i++
            }
        }.runTaskTimer(plugin, 0L, 5L)
    }

    fun obfuscatedEffect(item: Item) {
        Bukkit.getServer().playSound(Sounds.Fishing.OBFUSCATED_CATCH)
        object : BukkitRunnable() {
            var i = 0
            override fun run() {
                if (i % 2 == 0) {
                    item.location.world.spawnParticle(
                        Particle.ENCHANT,
                        item.location.clone().add(0.0, 0.5, 0.0),
                        5, 0.25, 0.25, 0.25, 0.1
                    )
                }
                if (i >= 500 || item.isDead) {
                    cancel()
                }
                i++
            }
        }.runTaskTimer(plugin, 0L, 1L)
    }

}

/**
 * Special fishing related properties for the rarities, all default to false
 *
 * @property showCatcher If the name of the catcher should be put on the fish
 * @property isAnimated If the rarity has an animation
 * @property sendGlobalMsg If a catch of this rarity should send a global message
 * @property sendGlobalTitle If a catch of this rarity should send a global title
 * @property retainData If ItemMeta should be retained on cooking for this rarity
 * @property showFishNumber If the number of fish caught should be shown in the lore
 */
data class RarityProperties(
    val showCatcher: Boolean = false,
    val isAnimated: Boolean = false,
    val sendGlobalMsg: Boolean = false,
    val sendGlobalTitle: Boolean = false,
    val retainData: Boolean = false,
    val showFishNumber: Boolean = false,
)

/**
 * @param weight Weight in % out of 100.0
 * @param itemRarity Item rarity used for display purposes
 * @param props Special display properties of the rarity
 */
enum class FishRarity(val weight: Double, val itemRarity: ItemRarity, val props: RarityProperties) {
    COMMON(47.7125, ItemRarity.COMMON, RarityProperties()),
    UNCOMMON(34.0, ItemRarity.UNCOMMON, RarityProperties()),
    RARE(12.0, ItemRarity.RARE, RarityProperties(isAnimated = true)),
    EPIC(5.0, ItemRarity.EPIC, RarityProperties(isAnimated = true, sendGlobalMsg = true)),
    LEGENDARY(
        1.0, ItemRarity.LEGENDARY, RarityProperties(
            isAnimated = true,
            sendGlobalMsg = true,
            showCatcher = true,
            retainData = true
        )
    ),
    MYTHIC(
        0.2, ItemRarity.MYTHIC, RarityProperties(
            isAnimated = true,
            sendGlobalMsg = true,
            sendGlobalTitle = true,
            showCatcher = true,
            retainData = true
        )
    ),
    UNREAL(
        0.05, ItemRarity.UNREAL, RarityProperties(
            isAnimated = true,
            sendGlobalMsg = true,
            sendGlobalTitle = true,
            showCatcher = true,
            retainData = true,
            showFishNumber = true
        )
    ),
    SPECIAL(
        0.0, ItemRarity.SPECIAL, RarityProperties(
            sendGlobalMsg = true,
            sendGlobalTitle = true,
            showCatcher = true,
            retainData = true,
            showFishNumber = true
        )
    ),
    TRANSCENDENT(
        0.025, ItemRarity.TRANSCENDENT, RarityProperties(
            isAnimated = true,
            sendGlobalMsg = true,
            sendGlobalTitle = true,
            showCatcher = true,
            retainData = true,
            showFishNumber = true
        )
    ),
    CELESTIAL(
        0.0125, ItemRarity.CELESTIAL, RarityProperties(
            isAnimated = true,
            sendGlobalMsg = true,
            sendGlobalTitle = true,
            showCatcher = true,
            retainData = true,
            showFishNumber = true
        )
    );

    companion object {
        fun getRandomRarity(): FishRarity {
            val totalWeight = entries.sumOf { it.weight }
            val randomValue = Random.nextDouble(totalWeight)

            var cumulativeWeight = 0.0
            for (rarity in entries) {
                cumulativeWeight += rarity.weight
                if (randomValue < cumulativeWeight) {
                    return rarity
                }
            }

            logger.warning("Unreachable code hit! No rarity selected")
            return SPECIAL // Should be unreachable but default to special in case of issue
        }
    }
}