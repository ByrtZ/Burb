package dev.byrt.burb.cosmetics.shop

import dev.byrt.burb.cosmetics.BurbCosmetics
import dev.byrt.burb.item.rarity.ItemRarity
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.lobby.fishing.Fishing
import dev.byrt.burb.player.progression.BurbExperienceLevels
import dev.byrt.burb.plugin
import dev.byrt.burb.text.Formatting
import dev.byrt.burb.text.Formatting.sendTranslated
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.*
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Transformation

object BurbStickerShop {
    private val shopInteractions = mutableListOf<Interaction>()

    fun createStickerShop(location: Location) {
        val shopInteractionEntity = location.world.spawn(location, Interaction::class.java).apply {
            interactionWidth = 3f
            interactionHeight = 3f
        }
        shopInteractions.add(shopInteractionEntity)
    }

    fun destroyShops() {
        shopInteractions.forEach { shop -> shop.remove()}
        shopInteractions.clear()
    }

    fun getShopInteractions(): List<Interaction> = shopInteractions

    fun pullStickerPack(interaction: Interaction, player: Player) {
        player.sendMessage(Component.translatable("burb.shop.sticker_shop.pull"))
        player.playSound(Sounds.Shop.PURCHASE_PACK)
        player.playSound(Sounds.Shop.OPEN_PACK)
        val location = interaction.location.clone().add(interaction.location.direction.setY(0).multiply(3)).add(0.0, 1.25, 0.0)
        object : BukkitRunnable() {
            override fun run() {
                player.playSound(Sounds.Shop.PACK_JINGLE)
                player.playSound(Sounds.Shop.REVEAL_CARD)

                // Roll cosmetic; grant if not owned, award xp if owned
                val pulledCosmetic = BurbStickerShopOdds.rollRandomCosmetic()
                if(BurbCosmetics.isCosmeticUnlocked(player, pulledCosmetic)) {
                    BurbExperienceLevels.appendExperience(player, 25 * (pulledCosmetic.cosmeticRarity.ordinal + 1))
                } else {
                    BurbCosmetics.unlockCosmetic(player, pulledCosmetic)
                }

                // Visible item
                val cosmeticNamePassenger = player.world.spawn(location, TextDisplay::class.java).apply {
                    billboard = Display.Billboard.VERTICAL
                    transformation = Transformation(
                        transformation.translation.add(0f, 1f, 0f),
                        transformation.leftRotation,
                        transformation.scale,
                        transformation.rightRotation,
                    )
                    isShadowed = true
                    text(Formatting.allTags.deserialize("<!i><${pulledCosmetic.cosmeticRarity.rarityColour}>${pulledCosmetic.cosmeticName}").appendSpace().append(Formatting.allTags.deserialize("<burbcolour>[<gray>${player.name}</gray>]</burbcolour>")))
                }
                val droppedCosmeticItem = player.world.spawn(location, Item::class.java).apply {
                    itemStack = BurbCosmetics.getCosmeticItem(pulledCosmetic)
                    velocity = location.direction.multiply(0.1)
                    pickupDelay = Int.MAX_VALUE
                    isInvulnerable = true
                    addPassenger(cosmeticNamePassenger)
                }

                object : BukkitRunnable() {
                    override fun run() {
                        droppedCosmeticItem.world.spawnParticle(
                        Particle.DUST,
                        droppedCosmeticItem.location.clone().add(0.0, 0.5, 0.0),
                        25, 0.5, 0.5, 0.5, 0.0,
                        Particle.DustOptions(pulledCosmetic.cosmeticRarity.colour, 1f),
                        true
                        )
                        droppedCosmeticItem.world.playSound(Sounds.Shop.ITEM_DESPAWN)
                        droppedCosmeticItem.remove()
                        cosmeticNamePassenger.remove()
                    }
                }.runTaskLater(plugin, 150L)

                // Global message
                Audience.audience(Bukkit.getOnlinePlayers()).sendTranslated("burb.shop.sticker_shop.item_receive", text(player.name), text(pulledCosmetic.cosmeticName, TextColor.fromHexString(pulledCosmetic.cosmeticRarity.rarityColour)))

                // Effects
                when(pulledCosmetic.cosmeticRarity) {
                    ItemRarity.RARE -> Fishing.rareEffect(location)
                    ItemRarity.EPIC -> Fishing.epicEffect(player, location)
                    ItemRarity.LEGENDARY -> {
                        Fishing.legendaryEffect(location)
                        player.playSound(Sounds.Misc.ODE_TO_JOY)
                    }
                    ItemRarity.MYTHIC -> {
                        Fishing.mythicEffect(location)
                        player.playSound(Sounds.Misc.ODE_TO_JOY)
                    }
                    else -> {}
                }
                location.world.spawnParticle(
                    Particle.DUST,
                    location.clone(),
                    25, 0.5, 0.5, 0.5, 0.0,
                    Particle.DustOptions(pulledCosmetic.cosmeticRarity.colour, 1f),
                    true
                )
            }
        }.runTaskLater(plugin, 45L)
    }
}