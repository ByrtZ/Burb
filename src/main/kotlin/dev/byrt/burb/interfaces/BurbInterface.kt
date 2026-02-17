package dev.byrt.burb.interfaces

import dev.byrt.burb.text.Formatting
import dev.byrt.burb.player.character.BurbCharacter
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.player.character.getCharacter
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.lobby.BurbLobby
import dev.byrt.burb.item.ServerItem
import dev.byrt.burb.player.cosmetics.BurbCosmetic
import dev.byrt.burb.player.cosmetics.BurbCosmetics
import dev.byrt.burb.player.data.BurbPlayerData
import dev.byrt.burb.plugin

import com.noxcrew.interfaces.drawable.Drawable.Companion.drawable
import com.noxcrew.interfaces.element.Element
import com.noxcrew.interfaces.element.StaticElement
import com.noxcrew.interfaces.grid.GridPoint
import com.noxcrew.interfaces.grid.GridPositionGenerator
import com.noxcrew.interfaces.interfaces.buildChestInterface
import com.noxcrew.interfaces.pane.Pane
import com.noxcrew.interfaces.transform.builtin.PaginationButton
import com.noxcrew.interfaces.transform.builtin.PaginationTransformation
import dev.byrt.burb.game.GameManager
import dev.byrt.burb.item.type.ItemType
import dev.byrt.burb.lobby.npc.BurbNPC
import dev.byrt.burb.lobby.fishing.FishRarity
import dev.byrt.burb.team.BurbTeam

import kotlinx.coroutines.runBlocking

import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.title.Title
import net.kyori.adventure.translation.GlobalTranslator

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

import java.time.Duration
import java.util.Locale

class BurbInterface(player: Player, interfaceType: BurbInterfaceType) {
    init {
        when (interfaceType) {
            BurbInterfaceType.TEAM_SELECT -> {
                runBlocking {
                    BurbInterfaces.createTeamInterface(player, interfaceType)
                }
            }
            BurbInterfaceType.CHARACTER_SELECT -> {
                runBlocking {
                    BurbInterfaces.createCharacterInterface(player, interfaceType)
                }
            }
            BurbInterfaceType.ALL_COSMETICS -> {
                runBlocking {
                    BurbInterfaces.createAllCosmeticsInterface(player, interfaceType)
                }
            }
            BurbInterfaceType.WARDROBE -> {
                runBlocking {
                    BurbInterfaces.createWardrobeInterface(player, interfaceType)
                }
            }
            BurbInterfaceType.MY_PROFILE -> {
                runBlocking {
                    BurbInterfaces.createProfileInterface(player, interfaceType)
                }
            }
            BurbInterfaceType.FISHING_SELECT -> {
                runBlocking {
                    BurbInterfaces.createFishingInterface(player, interfaceType)
                }
            }
            BurbInterfaceType.FISHING_CATALOGUE -> {
                runBlocking {
                    BurbInterfaces.createFishingCatalogueInterface(player, interfaceType)
                }
            }
            BurbInterfaceType.FISHING_CHANCES -> {
                runBlocking {
                    BurbInterfaces.createFishingChancesInterface(player, interfaceType)
                }
            }
            BurbInterfaceType.FISHING_ROD_GIVER -> {
                runBlocking {
                    BurbInterfaces.createFishingRodGiverInterface(player, interfaceType)
                }
            }
        }
    }
}

object BurbInterfaces {
    suspend fun createTeamInterface(player: Player, interfaceType: BurbInterfaceType) = buildChestInterface {
        titleSupplier = { Formatting.allTags.deserialize("<!i><b><burbcolour><shadow:#0:0.75>${interfaceType.interfaceName}") }
        rows = 3
        withTransform { pane, _ ->
            if(GameManager.teams.getTeam(player.uniqueId) == BurbTeam.PLANTS) {
                val plantsTeamItem = ItemStack(Material.BARRIER)
                val plantsTeamItemMeta = plantsTeamItem.itemMeta
                plantsTeamItemMeta.displayName(Formatting.allTags.deserialize("<red>Close Menu").decoration(TextDecoration.ITALIC, false))
                plantsTeamItem.itemMeta = plantsTeamItemMeta
                pane[1, 3] = StaticElement(drawable(plantsTeamItem)) {
                    player.playSound(Sounds.Misc.INTERFACE_BACK)
                    player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)

                    if(player.vehicle != null) {
                        if(player.vehicle is ItemDisplay) {
                            val veh = player.vehicle as ItemDisplay
                            if(veh.scoreboardTags.contains("${player.uniqueId}-title-vehicle")) {
                                BurbLobby.playerJoinHub(player)
                            }
                        }
                    }
                }
            } else {
                val plantsTeamItem = ItemStack(Material.LIME_DYE)
                val plantsTeamItemMeta = plantsTeamItem.itemMeta
                plantsTeamItemMeta.displayName(Formatting.allTags.deserialize("<plantscolour>Plants").decoration(TextDecoration.ITALIC, false))
                plantsTeamItem.itemMeta = plantsTeamItemMeta
                pane[1, 3] = StaticElement(drawable(plantsTeamItem)) {
                    GameManager.teams.setTeam(player, BurbTeam.PLANTS)
                    player.playSound(Sounds.Misc.INTERFACE_ENTER_SUB_MENU)
                    player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)

                    if(player.vehicle != null) {
                        if(player.vehicle is ItemDisplay) {
                            val veh = player.vehicle as ItemDisplay
                            veh.teleportDuration = 40
                            if(veh.scoreboardTags.contains("${player.uniqueId}-title-vehicle")) {
                                veh.teleport(Location(veh.world, 987.5, 5.5, 998.5, -25f, 20f))
                                player.setRotation(-25f, 20f)
                            }
                        }
                    }
                }
            }

            if(GameManager.teams.getTeam(player.uniqueId) == BurbTeam.ZOMBIES) {
                val zombiesTeamItem = ItemStack(Material.BARRIER)
                val zombiesTeamItemMeta = zombiesTeamItem.itemMeta
                zombiesTeamItemMeta.displayName(Formatting.allTags.deserialize("<red>Close Menu").decoration(TextDecoration.ITALIC, false))
                zombiesTeamItem.itemMeta = zombiesTeamItemMeta
                pane[1, 5] = StaticElement(drawable(zombiesTeamItem)) {
                    player.playSound(Sounds.Misc.INTERFACE_BACK)
                    player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)

                    if(player.vehicle != null) {
                        if(player.vehicle is ItemDisplay) {
                            val veh = player.vehicle as ItemDisplay
                            if(veh.scoreboardTags.contains("${player.uniqueId}-title-vehicle")) {
                                BurbLobby.playerJoinHub(player)
                            }
                        }
                    }
                }
            } else {
                val zombiesTeamItem = ItemStack(Material.PURPLE_DYE)
                val zombiesTeamItemMeta = zombiesTeamItem.itemMeta
                zombiesTeamItemMeta.displayName(Formatting.allTags.deserialize("<zombiescolour>Zombies").decoration(TextDecoration.ITALIC, false))
                zombiesTeamItem.itemMeta = zombiesTeamItemMeta
                pane[1, 5] = StaticElement(drawable(zombiesTeamItem)) {
                    GameManager.teams.setTeam(player, BurbTeam.ZOMBIES)
                    player.playSound(Sounds.Misc.INTERFACE_ENTER_SUB_MENU)
                    player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)

                    if(player.vehicle != null) {
                        if(player.vehicle is ItemDisplay) {
                            val veh = player.vehicle as ItemDisplay
                            veh.teleportDuration = 45
                            if(veh.scoreboardTags.contains("${player.uniqueId}-title-vehicle")) {
                                veh.teleport(Location(veh.world, 1004.5, 5.5, 997.5, -90f, 10f))
                                player.setRotation(-90f, 10f)
                            }
                        }
                    }
                }
            }

            if(GameManager.teams.getTeam(player.uniqueId) == null) {
                val closeMenuItem = ItemStack(Material.BARRIER)
                val closeMenuItemMeta = closeMenuItem.itemMeta
                closeMenuItemMeta.displayName(Formatting.allTags.deserialize("<red>Close Menu").decoration(TextDecoration.ITALIC, false))
                closeMenuItem.itemMeta = closeMenuItemMeta
                pane[2, 4] = StaticElement(drawable(closeMenuItem)) {
                    player.playSound(Sounds.Misc.INTERFACE_BACK)
                    player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)

                    if(player.vehicle != null) {
                        if(player.vehicle is ItemDisplay) {
                            val veh = player.vehicle as ItemDisplay
                            veh.teleportDuration = 55
                            if(veh.scoreboardTags.contains("${player.uniqueId}-title-vehicle")) {
                                veh.teleport(Location(veh.world, 1014.75, 18.5, 997.5, -55f, 15f))
                                player.setRotation(-55f, 15f)
                                BurbLobby.playerJoinHub(player)
                            }
                        }
                    }
                }
            } else {
                val spectatorsTeamItem = ItemStack(Material.LIGHT_GRAY_DYE)
                val spectatorsTeamItemMeta = spectatorsTeamItem.itemMeta
                spectatorsTeamItemMeta.displayName(Formatting.allTags.deserialize("<speccolour>Spectators").decoration(TextDecoration.ITALIC, false))
                spectatorsTeamItem.itemMeta = spectatorsTeamItemMeta
                pane[2, 4] = StaticElement(drawable(spectatorsTeamItem)) {
                    GameManager.teams.setTeam(player, null)
                    player.playSound(Sounds.Misc.INTERFACE_ENTER_SUB_MENU)
                    player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)

                    if(player.vehicle != null) {
                        if(player.vehicle is ItemDisplay) {
                            val veh = player.vehicle as ItemDisplay
                            veh.teleportDuration = 55
                            if(veh.scoreboardTags.contains("${player.uniqueId}-title-vehicle")) {
                                veh.teleport(Location(veh.world, 1014.75, 18.5, 997.5, -55f, 15f))
                                player.setRotation(-55f, 15f)
                                BurbLobby.playerJoinHub(player)
                            }
                        }
                    }
                }
            }
        }
        /** Add back button **/
        withTransform { pane, view ->
            if(view.parent() != null) {
                val backItem = ItemStack(Material.SPECTRAL_ARROW)
                val backItemMeta = backItem.itemMeta
                backItemMeta.displayName(Formatting.allTags.deserialize("<!i><burbcolour>Back"))
                backItem.itemMeta = backItemMeta
                pane[2,0] = StaticElement(drawable(backItem)) {
                    player.playSound(Sounds.Misc.INTERFACE_BACK)
                    runBlocking {
                        view.parent()?.open()
                    }
                }
            }
        }
    }.open(player)

    suspend fun createCharacterInterface(player: Player, interfaceType: BurbInterfaceType) = buildChestInterface {
        titleSupplier = { Formatting.allTags.deserialize("<!i><b><burbcolour><shadow:#0:0.75>${interfaceType.interfaceName}") }
        rows = 3
        withTransform { pane, _ ->
            var i = 0
            for(character in BurbCharacter.entries) {
                if(character != BurbCharacter.NULL) {
                    if(GameManager.teams.getTeam(player.uniqueId) == BurbTeam.PLANTS && character.name.startsWith("PLANTS") || GameManager.teams.getTeam(player.uniqueId) == BurbTeam.ZOMBIES && character.name.startsWith("ZOMBIES")) {
                        val characterItem = ServerItem.getCharacterBreakdownItem(character)

                        if(i == 0) i++
                        if(i == 1) i++
                        if(i == 4) i++

                        pane[1, i] = StaticElement(drawable(characterItem)) {
                            if(GameManager.teams.getTeam(player.uniqueId) == BurbTeam.PLANTS && character.name.startsWith("PLANTS") || GameManager.teams.getTeam(player.uniqueId) == BurbTeam.ZOMBIES && character.name.startsWith("ZOMBIES")) {
                                player.burbPlayer().setCharacter(character.characterName.getCharacter())
                                player.playSound(Sounds.Misc.INTERFACE_ENTER_SUB_MENU)
                                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
                                player.showTitle(
                                    Title.title(
                                        Formatting.allTags.deserialize(""),
                                        Formatting.allTags.deserialize("You are now a ${if(character.name.startsWith("PLANTS")) "<plantscolour>" else if(character.name.startsWith("ZOMBIES")) "<zombiescolour>" else "<#000000>"}${character.characterName}<reset>."),
                                        Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(3), Duration.ofMillis(250))
                                    )
                                )

                                if(player.vehicle != null) {
                                    if(player.vehicle is ItemDisplay) {
                                        val veh = player.vehicle as ItemDisplay
                                        if(veh.scoreboardTags.contains("${player.uniqueId}-title-vehicle")) {
                                            BurbLobby.playerJoinHub(player)
                                        }
                                    }
                                }
                            }
                        }
                        i++
                    }
                }
            }
        }
        /** Add back button **/
        withTransform { pane, view ->
            if(view.parent() != null) {
                val backItem = ItemStack(Material.SPECTRAL_ARROW)
                val backItemMeta = backItem.itemMeta
                backItemMeta.displayName(Formatting.allTags.deserialize("<!i><burbcolour>Back"))
                backItem.itemMeta = backItemMeta
                pane[2,0] = StaticElement(drawable(backItem)) {
                    player.playSound(Sounds.Misc.INTERFACE_BACK)
                    runBlocking {
                        view.parent()?.open()
                    }
                }
            }
        }
    }.open(player)

    suspend fun createAllCosmeticsInterface(player: Player, interfaceType: BurbInterfaceType) = buildChestInterface {
        val cosmeticItems = mutableListOf<ItemStack>()
        for(entry in BurbCosmetic.entries) {
            if (entry != BurbCosmetic.INVALID_COSMETIC) {
                cosmeticItems.add(BurbCosmetics.getCosmeticItem(entry))
            }
        }
        titleSupplier = { Formatting.allTags.deserialize("<!i><b><burbcolour><shadow:#0:0.75>${interfaceType.interfaceName}") }
        rows = 6
        /** Apply pagination transform **/
        addTransform(GenericPaginationTransformation(cosmeticItems))
        /** Add overview item **/
        withTransform { pane, _ ->
            val infoMenuItem = ItemStack(Material.NETHER_STAR)
            val infoMenuItemMeta = infoMenuItem.itemMeta
            infoMenuItemMeta.displayName(Formatting.allTags.deserialize("<!i><burbcolour>${interfaceType.interfaceName}"))
            infoMenuItemMeta.lore(listOf(
                Formatting.allTags.deserialize("<!i><white>Debug interface to view"),
                Formatting.allTags.deserialize("<!i><white>all registered cosmetics.")
            ))
            infoMenuItem.itemMeta = infoMenuItemMeta
            pane[0,4] = StaticElement(drawable(infoMenuItem))
        }
        /** Add close menu button **/
        withTransform { pane, _ ->
            val closeMenuItem = ItemStack(Material.BARRIER)
            val closeMenuItemMeta = closeMenuItem.itemMeta
            closeMenuItemMeta.displayName(Formatting.allTags.deserialize("<!i><red>Close Menu"))
            closeMenuItem.itemMeta = closeMenuItemMeta
            pane[5,4] = StaticElement(drawable(closeMenuItem)) {
                player.playSound(Sounds.Misc.INTERFACE_BACK)
                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
            }
        }
        /** Draw central information item if the interface cannot be populated **/
        if(cosmeticItems.isEmpty()) {
            withTransform { pane, _ ->
                val noCosmeticsMenuItem = ItemStack(Material.BARRIER)
                val noCosmeticsMenuItemMeta = noCosmeticsMenuItem.itemMeta
                noCosmeticsMenuItemMeta.displayName(Formatting.allTags.deserialize("<!i><red>No cosmetics found."))
                noCosmeticsMenuItemMeta.lore(listOf(
                    Formatting.allTags.deserialize("<i><dark_gray>Where are the artists?!")
                ))
                noCosmeticsMenuItem.itemMeta = noCosmeticsMenuItemMeta
                pane[2,4] = StaticElement(drawable(noCosmeticsMenuItem))
            }
        }
        /** Fill border with blank items **/
        withTransform { pane, _ ->
            val borderItem = ItemStack(Material.GRAY_STAINED_GLASS_PANE).apply {
                itemMeta = itemMeta.apply {
                    isHideTooltip = true
                }
            }
            val borderElement = StaticElement(drawable(borderItem))
            for(column in 0..8) {
                for(row in 0..5) {
                    if(column in listOf(0, 8) || row in listOf(0, 5)) {
                        if(pane[row, column] == null) {
                            pane[row, column] = borderElement
                        }
                    }
                }
            }
        }
    }.open(player)

    suspend fun createProfileInterface(player: Player, interfaceType: BurbInterfaceType) = buildChestInterface {
        titleSupplier = { Formatting.allTags.deserialize("<!i><b><burbcolour><shadow:#0:0.75>${interfaceType.interfaceName}") }
        rows = 3

        withTransform { pane, _ ->
            pane[1,3] = StaticElement(drawable(ServerItem.getTeamSwitcherItem())) {
                player.playSound(Sounds.Misc.INTERFACE_ENTER_SUB_MENU)
                runBlocking { createTeamInterface(player, BurbInterfaceType.TEAM_SELECT) }
            }
        }
        withTransform { pane, _ ->
            pane[1,5] = StaticElement(drawable(ServerItem.getCosmeticsItem())) {
                player.playSound(Sounds.Misc.INTERFACE_ENTER_SUB_MENU)
                runBlocking { createWardrobeInterface(player, BurbInterfaceType.WARDROBE) }
            }
        }
        if(player.isOp) {
            withTransform { pane, _ ->
                pane[2,8] = StaticElement(drawable(ServerItem.getAdminPanelItem())) {
                    player.playSound(Sounds.Misc.INTERFACE_ERROR)
                    player.sendMessage(Formatting.allTags.deserialize("<red>This interface is not implemented yet."))
                }
            }
        }
        withTransform { pane, _ ->
            val closeMenuItem = ItemStack(Material.BARRIER)
            val closeMenuItemMeta = closeMenuItem.itemMeta
            closeMenuItemMeta.displayName(Formatting.allTags.deserialize("<!i><red>Close Menu"))
            closeMenuItem.itemMeta = closeMenuItemMeta
            pane[2,4] = StaticElement(drawable(closeMenuItem)) {
                player.playSound(Sounds.Misc.INTERFACE_BACK)
                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
            }
        }
    }.open(player)

    suspend fun createWardrobeInterface(player: Player, interfaceType: BurbInterfaceType) = buildChestInterface {
        val playerConfig = BurbPlayerData.getPlayerConfiguration(player)
        val rawUnlockedCosmetics = playerConfig.getList("${player.uniqueId}.cosmetics") ?: emptyList()
        val unlockedCosmetics = rawUnlockedCosmetics.mapNotNull { it as? String }.toMutableList()
        val equippedHat = BurbCosmetics.getEquippedCosmetic(player, ItemType.HAT)
        val equippedAccessory = BurbCosmetics.getEquippedCosmetic(player, ItemType.ACCESSORY)

        // Check unlocked cosmetics and replace item type with a locked material if the cosmetic is not unlocked
        val cosmeticItems = mutableListOf<ItemStack>()
        for(cosmetic in BurbCosmetic.entries) {
            if(cosmetic != BurbCosmetic.INVALID_COSMETIC) {
                val cosmeticItem = BurbCosmetics.getCosmeticItem(cosmetic)
                if(!unlockedCosmetics.contains(cosmetic.cosmeticId)) {
                    // Remove model if not unlocked
                    cosmeticItem.apply { itemMeta = itemMeta.apply { itemModel = null } }
                    cosmeticItem.type = Material.GRAY_DYE
                    // Do not show details if hidden
                    if(cosmetic.isHidden) {
                        cosmeticItem.apply { itemMeta = itemMeta.apply { displayName(Formatting.allTags.deserialize("<!i><red>???")) } }
                        cosmeticItem.lore(BurbCosmetics.buildCosmeticLore(Formatting.allTags.deserialize("<!i>"), GlobalTranslator.renderer().render(cosmetic.cosmeticObtainment, Locale.ENGLISH), Formatting.allTags.deserialize("<!i>"), Formatting.allTags.deserialize("<!i><red><unicodeprefix:locked> Locked")))
                    } else {
                        cosmeticItem.lore(BurbCosmetics.buildCosmeticLore(Formatting.allTags.deserialize("<!i><white>${cosmetic.cosmeticRarity.asMiniMessage()}${cosmetic.cosmeticType.asMiniMessage()}"), Formatting.allTags.deserialize("<!i>"), GlobalTranslator.renderer().render(cosmetic.cosmeticObtainment, Locale.ENGLISH), Formatting.allTags.deserialize("<!i>"), Formatting.allTags.deserialize("<!i><red><unicodeprefix:locked> Locked")))
                    }
                } else {
                    if(cosmetic == equippedHat || cosmetic == equippedAccessory) {
                        cosmeticItem.apply { itemMeta = itemMeta.apply { itemModel = null } }
                        cosmeticItem.type = Material.LIME_DYE
                        cosmeticItem.lore(cosmeticItem.lore()?.plus(BurbCosmetics.buildCosmeticLore(Formatting.allTags.deserialize("<!i>"), Formatting.allTags.deserialize("<!i><burbcolour><unicodeprefix:unlocked> Click to unequip"))))
                    } else {
                        cosmeticItem.lore(cosmeticItem.lore()?.plus(BurbCosmetics.buildCosmeticLore(Formatting.allTags.deserialize("<!i>"), Formatting.allTags.deserialize("<!i><burbcolour><unicodeprefix:unlocked> Click to equip"))))
                    }
                }
                cosmeticItems.add(cosmeticItem)
            }
        }
        titleSupplier = { Formatting.allTags.deserialize("<!i><b><burbcolour><shadow:#0:0.75>${interfaceType.interfaceName}") }
        rows = 6
        /** Apply pagination transform **/
        addTransform(WardrobePaginationTransformation(cosmeticItems))
        /** Add overview item **/
        withTransform { pane, _ ->
            val infoMenuItem = ItemStack(Material.NETHER_STAR)
            val infoMenuItemMeta = infoMenuItem.itemMeta
            infoMenuItemMeta.displayName(Formatting.allTags.deserialize("<!i><burbcolour>${interfaceType.interfaceName}"))
            infoMenuItemMeta.lore(listOf(
                Formatting.allTags.deserialize("<!i>"),
                Formatting.allTags.deserialize("<!i><white>Browse and equip various cosmetics"),
                Formatting.allTags.deserialize("<!i><white>to be displayed on your character.")
            ))
            infoMenuItem.itemMeta = infoMenuItemMeta
            pane[0,4] = StaticElement(drawable(infoMenuItem))
        }
        /** Add close menu button **/
        withTransform { pane, _ ->
            val closeMenuItem = ItemStack(Material.BARRIER)
            val closeMenuItemMeta = closeMenuItem.itemMeta
            closeMenuItemMeta.displayName(Formatting.allTags.deserialize("<!i><red>Close Menu"))
            closeMenuItem.itemMeta = closeMenuItemMeta
            pane[5,4] = StaticElement(drawable(closeMenuItem)) {
                player.playSound(Sounds.Misc.INTERFACE_BACK)
                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
            }
        }
        /** Draw central information item if the interface cannot be populated **/
        if(cosmeticItems.isEmpty()) {
            withTransform { pane, _ ->
                val noCosmeticsMenuItem = ItemStack(Material.BARRIER)
                val noCosmeticsMenuItemMeta = noCosmeticsMenuItem.itemMeta
                noCosmeticsMenuItemMeta.displayName(Formatting.allTags.deserialize("<!i><red>No cosmetics found."))
                noCosmeticsMenuItemMeta.lore(listOf(
                    Formatting.allTags.deserialize("<i><dark_gray>I have no idea what went wrong here.")
                ))
                noCosmeticsMenuItem.itemMeta = noCosmeticsMenuItemMeta
                pane[2,4] = StaticElement(drawable(noCosmeticsMenuItem))
            }
        }
        /** Add back button **/
        withTransform { pane, view ->
            if(view.parent() != null) {
                val backItem = ItemStack(Material.SPECTRAL_ARROW)
                val backItemMeta = backItem.itemMeta
                backItemMeta.displayName(Formatting.allTags.deserialize("<!i><burbcolour>Back"))
                backItem.itemMeta = backItemMeta
                pane[5,0] = StaticElement(drawable(backItem)) {
                    player.playSound(Sounds.Misc.INTERFACE_BACK)
                    runBlocking {
                        view.parent()?.open()
                    }
                }
            }
        }
        /** Fill border with blank items **/
        withTransform { pane, _ ->
            val borderItem = ItemStack(Material.GRAY_STAINED_GLASS_PANE).apply {
                itemMeta = itemMeta.apply {
                    isHideTooltip = true
                }
            }
            val borderElement = StaticElement(drawable(borderItem))
            for(column in 0..8) {
                for(row in 0..5) {
                    if(column in listOf(0, 8) || row in listOf(0, 5)) {
                        if(pane[row, column] == null) {
                            pane[row, column] = borderElement
                        }
                    }
                }
            }
        }
    }.open(player)

    suspend fun createFishingInterface(player: Player, interfaceType: BurbInterfaceType) = buildChestInterface {
        titleSupplier = { Formatting.allTags.deserialize("<!i><b><burbcolour><shadow:#0:0.75>${interfaceType.interfaceName}") }
        rows = 3

        withTransform { pane, _ ->
            pane[1,3] = StaticElement(drawable(ServerItem.getFishingCatalogueItem())) {
                player.playSound(Sounds.Misc.INTERFACE_ENTER_SUB_MENU)
                runBlocking { createFishingCatalogueInterface(player, BurbInterfaceType.FISHING_CATALOGUE) }
            }
        }
        withTransform { pane, _ ->
            pane[1,5] = StaticElement(drawable(ServerItem.getFishingChancesItem())) {
                player.playSound(Sounds.Misc.INTERFACE_ENTER_SUB_MENU)
                runBlocking { createFishingChancesInterface(player, BurbInterfaceType.FISHING_CHANCES) }
            }
        }
        withTransform { pane, _ ->
            val closeMenuItem = ItemStack(Material.BARRIER)
            val closeMenuItemMeta = closeMenuItem.itemMeta
            closeMenuItemMeta.displayName(Formatting.allTags.deserialize("<!i><red>Close Menu"))
            closeMenuItem.itemMeta = closeMenuItemMeta
            pane[2,4] = StaticElement(drawable(closeMenuItem)) {
                player.playSound(Sounds.Misc.INTERFACE_BACK)
                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
            }
        }
        /** Add back button **/
        withTransform { pane, view ->
            if(view.parent() != null) {
                val backItem = ItemStack(Material.SPECTRAL_ARROW)
                val backItemMeta = backItem.itemMeta
                backItemMeta.displayName(Formatting.allTags.deserialize("<!i><burbcolour>Back"))
                backItem.itemMeta = backItemMeta
                pane[2,0] = StaticElement(drawable(backItem)) {
                    player.playSound(Sounds.Misc.INTERFACE_BACK)
                    runBlocking {
                        view.parent()?.open()
                    }
                }
            }
        }
    }.open(player)

    suspend fun createFishingCatalogueInterface(player: Player, interfaceType: BurbInterfaceType) = buildChestInterface {
        titleSupplier = { Formatting.allTags.deserialize("<!i><b><burbcolour><shadow:#0:0.75>${interfaceType.interfaceName}") }
        rows = 3

        withTransform { pane, _ ->
            pane[1,4] = StaticElement(drawable(ServerItem.getUnconfiguredItem())) {
                player.playSound(Sounds.Misc.INTERFACE_ERROR)
            }
        }

        withTransform { pane, _ ->
            val closeMenuItem = ItemStack(Material.BARRIER)
            val closeMenuItemMeta = closeMenuItem.itemMeta
            closeMenuItemMeta.displayName(Formatting.allTags.deserialize("<!i><red>Close Menu"))
            closeMenuItem.itemMeta = closeMenuItemMeta
            pane[2,4] = StaticElement(drawable(closeMenuItem)) {
                player.playSound(Sounds.Misc.INTERFACE_BACK)
                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
            }
        }

        /** Add back button **/
        withTransform { pane, view ->
            if(view.parent() != null) {
                val backItem = ItemStack(Material.SPECTRAL_ARROW)
                val backItemMeta = backItem.itemMeta
                backItemMeta.displayName(Formatting.allTags.deserialize("<!i><burbcolour>Back"))
                backItem.itemMeta = backItemMeta
                pane[2,0] = StaticElement(drawable(backItem)) {
                    player.playSound(Sounds.Misc.INTERFACE_BACK)
                    runBlocking {
                        view.parent()?.open()
                    }
                }
            }
        }
    }.open(player)

    suspend fun createFishingChancesInterface(player: Player, interfaceType: BurbInterfaceType) = buildChestInterface {
        titleSupplier = { Formatting.allTags.deserialize("<!i><b><burbcolour><shadow:#0:0.75>${interfaceType.interfaceName}") }
        rows = 3

        for((col, rarity) in FishRarity.entries.filter { it != FishRarity.SPECIAL }.withIndex()) {
            withTransform { pane, _ ->
                pane[1,col] = StaticElement(drawable(ServerItem.getFishingChanceItem(rarity))) {
                    player.playSound(Sounds.Fishing.FISH_FLOP)
                }
            }
        }

        withTransform { pane, _ ->
            val closeMenuItem = ItemStack(Material.BARRIER)
            val closeMenuItemMeta = closeMenuItem.itemMeta
            closeMenuItemMeta.displayName(Formatting.allTags.deserialize("<!i><red>Close Menu"))
            closeMenuItem.itemMeta = closeMenuItemMeta
            pane[2,4] = StaticElement(drawable(closeMenuItem)) {
                player.playSound(Sounds.Misc.INTERFACE_BACK)
                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
            }
        }

        /** Add back button **/
        withTransform { pane, view ->
            if(view.parent() != null) {
                val backItem = ItemStack(Material.SPECTRAL_ARROW)
                val backItemMeta = backItem.itemMeta
                backItemMeta.displayName(Formatting.allTags.deserialize("<!i><burbcolour>Back"))
                backItem.itemMeta = backItemMeta
                pane[2,0] = StaticElement(drawable(backItem)) {
                    player.playSound(Sounds.Misc.INTERFACE_BACK)
                    runBlocking {
                        view.parent()?.open()
                    }
                }
            }
        }
    }.open(player)

    suspend fun createFishingRodGiverInterface(player: Player, interfaceType: BurbInterfaceType) = buildChestInterface {
        titleSupplier = { Formatting.allTags.deserialize("<!i><b><burbcolour><shadow:#0:0.75>${interfaceType.interfaceName}") }
        rows = 3

        withTransform { pane, view ->
            pane[1,4] = StaticElement(drawable(ServerItem.getFishingRodItem())) {
                if(player.inventory.contains(Material.FISHING_ROD)) {
                    player.sendMessage(Formatting.allTags.deserialize("<b>${BurbNPC.LOBBY_FISHING_ROD_GIVER.npcNameColour}${BurbNPC.LOBBY_FISHING_ROD_GIVER.npcName}</b><white>: You've already got a rod, don't be greedy now!"))
                    player.playSound(Sounds.Misc.INTERFACE_ERROR)
                    player.playSound(Sounds.Misc.NPC_INTERACT)
                    runBlocking { view.close(InventoryCloseEvent.Reason.PLUGIN, false) }
                } else {
                    player.sendMessage(Formatting.allTags.deserialize("<b>${BurbNPC.LOBBY_FISHING_ROD_GIVER.npcNameColour}${BurbNPC.LOBBY_FISHING_ROD_GIVER.npcName}</b><white>: Best of luck out there in the waters..."))
                    player.playSound(Sounds.Misc.COMPLETE)
                    player.playSound(Sounds.Misc.NPC_INTERACT)
                    player.inventory.setItem(1, ServerItem.getUsableFishingRodItem())
                    runBlocking { view.close(InventoryCloseEvent.Reason.PLUGIN, false) }
                }

            }
        }

        withTransform { pane, _ ->
            val closeMenuItem = ItemStack(Material.BARRIER)
            val closeMenuItemMeta = closeMenuItem.itemMeta
            closeMenuItemMeta.displayName(Formatting.allTags.deserialize("<!i><red>Close Menu"))
            closeMenuItem.itemMeta = closeMenuItemMeta
            pane[2,4] = StaticElement(drawable(closeMenuItem)) {
                player.playSound(Sounds.Misc.INTERFACE_BACK)
                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
            }
        }
    }.open(player)
}

class WardrobePaginationTransformation(items: List<ItemStack>): PaginationTransformation<Pane, ItemStack>(
    positionGenerator = GridPositionGenerator { buildList {
        for(row in 1..4) {
            for(col in 1..7) {
                add(GridPoint(row, col))
            }
        }
    }},
    items,
    back = PaginationButton(
        position = GridPoint(5, 2),
        drawable = drawable(ItemStack(Material.ARROW).apply { itemMeta = itemMeta.apply {
            displayName(Formatting.allTags.deserialize("<!i><burbcolour>Previous Page"))
        } }),
        increments = mapOf(Pair(ClickType.LEFT, -1)),
        clickHandler = { player -> player.playSound(Sounds.Misc.INTERFACE_INTERACT) }
    ),
    forward = PaginationButton(
        position = GridPoint(5, 6),
        drawable = drawable(ItemStack(Material.ARROW).apply { itemMeta = itemMeta.apply {
            displayName(Formatting.allTags.deserialize("<!i><burbcolour>Next Page"))
        } }),
        increments = mapOf(Pair(ClickType.LEFT, 1)),
        clickHandler = { player -> player.playSound(Sounds.Misc.INTERFACE_INTERACT) }
    )) {
    override suspend fun drawElement(index: Int, element: ItemStack): Element {
        return StaticElement(drawable(if(element.type == Material.AIR)
            ItemStack(Material.BARRIER).apply {
                itemMeta = itemMeta.apply {
                    displayName(Formatting.allTags.deserialize("<!i><red>An error occurred when loading this item."))
                }
            } else element)
        ) { click ->
            val player = click.player
            when(click.type) {
                ClickType.LEFT -> {
                    when(element.type) {
                        Material.LIME_DYE -> {
                            element.persistentDataContainer.get(NamespacedKey(plugin, "cosmetic"), PersistentDataType.STRING)
                                ?.let { BurbCosmetics.getCosmeticById(it) }?.let {
                                    BurbCosmetics.unequipCosmetic(player, it.cosmeticType)
                                    player.playSound(Sounds.Misc.INTERFACE_BACK)
                                    runBlocking {
                                        BurbInterfaces.createWardrobeInterface(player, BurbInterfaceType.WARDROBE)
                                    }
                                }
                        }
                        Material.GRAY_DYE -> player.playSound(Sounds.Misc.INTERFACE_ERROR)
                        else -> {
                            element.persistentDataContainer.get(NamespacedKey(plugin, "cosmetic"), PersistentDataType.STRING)
                                ?.let { BurbCosmetics.getCosmeticById(it) }?.let {
                                    BurbCosmetics.equipCosmetic(player, it, false)
                                    player.playSound(Sounds.Misc.INTERFACE_ENTER_SUB_MENU)
                                    runBlocking {
                                        BurbInterfaces.createWardrobeInterface(player, BurbInterfaceType.WARDROBE)
                                    }
                                }
                        }
                    }
                } else -> {
                    player.playSound(Sounds.Misc.INTERFACE_ERROR)
                }
            }
        }
    }
}

class GenericPaginationTransformation(items: List<ItemStack>): PaginationTransformation<Pane, ItemStack>(
    positionGenerator = GridPositionGenerator { buildList {
        for(row in 1..4) {
            for(col in 1..7) {
                add(GridPoint(row, col))
            }
        }
    }},
    items,
    back = PaginationButton(
        position = GridPoint(5, 2),
        drawable = drawable(ItemStack(Material.ARROW).apply { itemMeta = itemMeta.apply {
            displayName(Formatting.allTags.deserialize("<!i><burbcolour>Previous Page"))
        } }),
        increments = mapOf(Pair(ClickType.LEFT, -1)),
        clickHandler = { player -> player.playSound(Sounds.Misc.INTERFACE_INTERACT) }
    ),
    forward = PaginationButton(
        position = GridPoint(5, 6),
        drawable = drawable(ItemStack(Material.ARROW).apply { itemMeta = itemMeta.apply {
            displayName(Formatting.allTags.deserialize("<!i><burbcolour>Next Page"))
        } }),
        increments = mapOf(Pair(ClickType.LEFT, 1)),
        clickHandler = { player -> player.playSound(Sounds.Misc.INTERFACE_INTERACT) }
    )) {
    override suspend fun drawElement(index: Int, element: ItemStack): Element {
        return StaticElement(drawable(if(element.type == Material.AIR)
            ItemStack(Material.BARRIER).apply {
                itemMeta = itemMeta.apply {
                    displayName(Formatting.allTags.deserialize("<!i><red>An error occurred when loading this item."))
                }
            } else element)
        )
    }
}