package dev.byrt.burb.interfaces

import dev.byrt.burb.chat.Formatting
import dev.byrt.burb.player.BurbCharacter
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.player.getCharacter
import dev.byrt.burb.team.Teams

import com.noxcrew.interfaces.drawable.Drawable.Companion.drawable
import com.noxcrew.interfaces.element.StaticElement
import com.noxcrew.interfaces.interfaces.buildChestInterface

import kotlinx.coroutines.runBlocking
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.title.Title

import org.bukkit.Material
import org.bukkit.entity.Player
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
            val plantsTeamItem = ItemStack(Material.LIME_DYE)
            pane[1, 3] = StaticElement(drawable(plantsTeamItem)) {
                player.burbPlayer().setTeam(Teams.PLANTS)
                player.closeInventory()
            }

            val zombiesTeamItem = ItemStack(Material.PURPLE_DYE)
            pane[1, 5] = StaticElement(drawable(zombiesTeamItem)) {
                player.burbPlayer().setTeam(Teams.ZOMBIES)
                player.closeInventory()
            }

            val spectatorsTeamItem = ItemStack(Material.GRAY_DYE)
            pane[2, 4] = StaticElement(drawable(spectatorsTeamItem)) {
                player.burbPlayer().setTeam(Teams.SPECTATOR)
                player.closeInventory()
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
                        val characterItem = ItemStack(if(character.name.startsWith("PLANTS")) Material.LIME_DYE else if(character.name.startsWith("ZOMBIES")) Material.PURPLE_DYE else Material.POTATO)
                        val characterItemMeta = characterItem.itemMeta
                        characterItemMeta.displayName(Formatting.allTags.deserialize("${if(character.name.startsWith("PLANTS")) "<plantscolour>" else if(character.name.startsWith("ZOMBIES")) "<zombiescolour>" else "<#000000>"}${character.characterName}").decoration(TextDecoration.ITALIC, false))
                        characterItem.itemMeta = characterItemMeta

                        if(i == 0) i++
                        if(i == 1) i++
                        if(i == 4) i++

                        pane[1, i] = StaticElement(drawable(characterItem)) {
                            if(player.burbPlayer().playerTeam == Teams.PLANTS && character.name.startsWith("PLANTS") || player.burbPlayer().playerTeam == Teams.ZOMBIES && character.name.startsWith("ZOMBIES")) {
                                player.burbPlayer().setCharacter(character.characterName.getCharacter())
                                player.closeInventory()
                                player.showTitle(
                                    Title.title(
                                        Formatting.allTags.deserialize(""),
                                        Formatting.allTags.deserialize("You are now a ${if(character.name.startsWith("PLANTS")) "<plantscolour>" else if(character.name.startsWith("ZOMBIES")) "<zombiescolour>" else "<#000000>"}${character.characterName}<reset>."),
                                        Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(3), Duration.ofMillis(250))
                                    )
                                )
                            }
                        }
                        i++
                    }
                }
            }
        }
    }
}