package dev.byrt.burb.interfaces

import dev.byrt.burb.text.Formatting
import dev.byrt.burb.player.BurbCharacter
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.player.getCharacter
import dev.byrt.burb.team.Teams
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.lobby.LobbyManager
import dev.byrt.burb.item.ServerItem

import com.noxcrew.interfaces.drawable.Drawable.Companion.drawable
import com.noxcrew.interfaces.element.StaticElement
import com.noxcrew.interfaces.interfaces.buildChestInterface

import io.papermc.paper.entity.TeleportFlag

import kotlinx.coroutines.runBlocking

import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.title.Title

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack

import java.time.Duration

class BurbInterface(player: Player, interfaceType: BurbInterfaceType) {
    init {
        when(interfaceType) {
            BurbInterfaceType.TEAM_SELECT -> {
                runBlocking {
                    val teamInterface = createTeamInterface(player, interfaceType)
                    teamInterface.open(player)
                }
            }
            BurbInterfaceType.CHARACTER_SELECT -> {
                runBlocking {
                    val characterInterface = createCharacterInterface(player, interfaceType)
                    characterInterface.open(player)
                }
            }
        }
    }

    private fun createTeamInterface(player: Player, interfaceType: BurbInterfaceType) = buildChestInterface {
        initialTitle = Formatting.allTags.deserialize(interfaceType.interfaceName)
        rows = 3
        withTransform { pane, _ ->
            if(player.burbPlayer().playerTeam == Teams.PLANTS) {
                val plantsTeamItem = ItemStack(Material.BARRIER)
                val plantsTeamItemMeta = plantsTeamItem.itemMeta
                plantsTeamItemMeta.displayName(Formatting.allTags.deserialize("<red>Close Menu").decoration(TextDecoration.ITALIC, false))
                plantsTeamItem.itemMeta = plantsTeamItemMeta
                pane[1, 3] = StaticElement(drawable(plantsTeamItem)) {
                    player.playSound(Sounds.Misc.INTERFACE_INTERACT)
                    player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)

                    if(player.vehicle != null) {
                        if(player.vehicle is ItemDisplay) {
                            val veh = player.vehicle as ItemDisplay
                            if(veh.scoreboardTags.contains("${player.uniqueId}-title-vehicle")) {
                                LobbyManager.playerJoinHub(player)
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
                    player.burbPlayer().setTeam(Teams.PLANTS)
                    player.playSound(Sounds.Misc.INTERFACE_INTERACT)
                    player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)

                    if(player.vehicle != null) {
                        if(player.vehicle is ItemDisplay) {
                            val veh = player.vehicle as ItemDisplay
                            veh.teleportDuration = 40
                            if(veh.scoreboardTags.contains("${player.uniqueId}-title-vehicle")) {
                                veh.teleport(Location(veh.world, 987.5, 5.5, 998.5, -25f, 20f), TeleportFlag.EntityState.RETAIN_PASSENGERS)
                                player.setRotation(-25f, 20f)
                            }
                        }
                    }
                }
            }

            if(player.burbPlayer().playerTeam == Teams.ZOMBIES) {
                val zombiesTeamItem = ItemStack(Material.BARRIER)
                val zombiesTeamItemMeta = zombiesTeamItem.itemMeta
                zombiesTeamItemMeta.displayName(Formatting.allTags.deserialize("<red>Close Menu").decoration(TextDecoration.ITALIC, false))
                zombiesTeamItem.itemMeta = zombiesTeamItemMeta
                pane[1, 5] = StaticElement(drawable(zombiesTeamItem)) {
                    player.playSound(Sounds.Misc.INTERFACE_INTERACT)
                    player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)

                    if(player.vehicle != null) {
                        if(player.vehicle is ItemDisplay) {
                            val veh = player.vehicle as ItemDisplay
                            if(veh.scoreboardTags.contains("${player.uniqueId}-title-vehicle")) {
                                LobbyManager.playerJoinHub(player)
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
                    player.burbPlayer().setTeam(Teams.ZOMBIES)
                    player.playSound(Sounds.Misc.INTERFACE_INTERACT)
                    player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)

                    if(player.vehicle != null) {
                        if(player.vehicle is ItemDisplay) {
                            val veh = player.vehicle as ItemDisplay
                            veh.teleportDuration = 45
                            if(veh.scoreboardTags.contains("${player.uniqueId}-title-vehicle")) {
                                veh.teleport(Location(veh.world, 1004.5, 5.5, 997.5, -90f, 10f), TeleportFlag.EntityState.RETAIN_PASSENGERS)
                                player.setRotation(-90f, 10f)
                            }
                        }
                    }
                }
            }

            if(player.burbPlayer().playerTeam == Teams.SPECTATOR) {
                val closeMenuItem = ItemStack(Material.BARRIER)
                val closeMenuItemMeta = closeMenuItem.itemMeta
                closeMenuItemMeta.displayName(Formatting.allTags.deserialize("<red>Close Menu").decoration(TextDecoration.ITALIC, false))
                closeMenuItem.itemMeta = closeMenuItemMeta
                pane[2, 4] = StaticElement(drawable(closeMenuItem)) {
                    player.playSound(Sounds.Misc.INTERFACE_INTERACT)
                    player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)

                    if(player.vehicle != null) {
                        if(player.vehicle is ItemDisplay) {
                            val veh = player.vehicle as ItemDisplay
                            veh.teleportDuration = 55
                            if(veh.scoreboardTags.contains("${player.uniqueId}-title-vehicle")) {
                                veh.teleport(Location(veh.world, 1014.75, 18.5, 997.5, -55f, 15f), TeleportFlag.EntityState.RETAIN_PASSENGERS)
                                player.setRotation(-55f, 15f)
                                LobbyManager.playerJoinHub(player)
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
                    player.burbPlayer().setTeam(Teams.SPECTATOR)
                    player.playSound(Sounds.Misc.INTERFACE_INTERACT)
                    player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)

                    if(player.vehicle != null) {
                        if(player.vehicle is ItemDisplay) {
                            val veh = player.vehicle as ItemDisplay
                            veh.teleportDuration = 80
                            if(veh.scoreboardTags.contains("${player.uniqueId}-title-vehicle")) {
                                veh.teleport(Location(veh.world, 1014.75, 18.5, 997.5, -55f, 15f), TeleportFlag.EntityState.RETAIN_PASSENGERS)
                                player.setRotation(-55f, 15f)
                                LobbyManager.playerJoinHub(player)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun createCharacterInterface(player: Player, interfaceType: BurbInterfaceType) = buildChestInterface {
        initialTitle = Formatting.allTags.deserialize(interfaceType.interfaceName)
        rows = 3
        withTransform { pane, _ ->
            var i = 0
            for(character in BurbCharacter.entries) {
                if(character != BurbCharacter.NULL) {
                    if(player.burbPlayer().playerTeam == Teams.PLANTS && character.name.startsWith("PLANTS") || player.burbPlayer().playerTeam == Teams.ZOMBIES && character.name.startsWith("ZOMBIES")) {
                        val characterItem = ServerItem.getCharacterBreakdownItem(character)

                        if(i == 0) i++
                        if(i == 1) i++
                        if(i == 4) i++

                        pane[1, i] = StaticElement(drawable(characterItem)) {
                            if(player.burbPlayer().playerTeam == Teams.PLANTS && character.name.startsWith("PLANTS") || player.burbPlayer().playerTeam == Teams.ZOMBIES && character.name.startsWith("ZOMBIES")) {
                                player.burbPlayer().setCharacter(character.characterName.getCharacter())
                                player.playSound(Sounds.Misc.INTERFACE_INTERACT)
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
                                            LobbyManager.playerJoinHub(player)
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
    }
}