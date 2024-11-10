package dev.byrt.burb.team

import dev.byrt.burb.chat.ChatUtility
import dev.byrt.burb.chat.Formatting
import dev.byrt.burb.item.ItemManager
import dev.byrt.burb.library.Translation
import dev.byrt.burb.player.BurbPlayer
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.player.PlayerType

import net.kyori.adventure.text.format.TextColor
import org.bukkit.Color
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object TeamManager {
    private val spectators = mutableSetOf<BurbPlayer>()
    private val plants = mutableSetOf<BurbPlayer>()
    private val zombies = mutableSetOf<BurbPlayer>()
    fun setTeam(player: BurbPlayer, team: Teams) {
        if(spectators.contains(player)) spectators.remove(player)
        if(plants.contains(player)) plants.remove(player)
        if(zombies.contains(player)) zombies.remove(player)
        when(team) {
            Teams.SPECTATOR -> {
                spectators.add(player)
                player.setType(PlayerType.SPECTATOR)
            }
            Teams.PLANTS -> {
                plants.add(player)
                player.setType(PlayerType.PARTICIPANT)
            }
            Teams.ZOMBIES -> {
                zombies.add(player)
                player.setType(PlayerType.PARTICIPANT)
            }
            Teams.NULL -> {
                player.setType(PlayerType.INVALID)
            }
        }
        ItemManager.givePlayerTeamBoots(player.getBukkitPlayer(), team)
        player.getBukkitPlayer().sendMessage(Formatting.allTags.deserialize(Translation.Teams.JOIN_TEAM.replace("%d", team.teamColourTag).replace("%s", team.teamName)))
    }

    fun shuffleTeams(sender: CommandSender?, players: Set<Player>, ignoreAdmins: Boolean) {
        for((i, player) in players.withIndex()) {
            if(i % 2 == 0) {
                player.burbPlayer().setTeam(Teams.ZOMBIES)
            } else {
                player.burbPlayer().setTeam(Teams.PLANTS)
            }
        }
        ChatUtility.broadcastDev("<dark_gray>Teams shuffled by ${sender?.name ?: "the game"} ${if(ignoreAdmins) "<italic>[Non-Admins]</italic>." else "."}", false)
    }

    fun getParticipants(): Set<BurbPlayer> {
        return this.plants + this.zombies
    }

    fun getSpectators(): Set<BurbPlayer> {
        return this.spectators
    }

    fun getPlants(): Set<BurbPlayer> {
        return this.plants
    }

    fun getZombies(): Set<BurbPlayer> {
        return this.zombies
    }

    fun Set<BurbPlayer>.getPlayerNames(): ArrayList<String> {
        val playerNames = ArrayList<String>()
        for(player in this) {
            playerNames.add(player.playerName)
        }
        return playerNames
    }
}

enum class Teams(val teamName: String, val teamHexColour: TextColor, val teamColour: Color, val teamColourTag: String) {
    SPECTATOR("Spectator", TextColor.fromHexString("#aaaaaa")!!, Color.GRAY, "<speccolour>"),
    PLANTS("Plants", TextColor.color(21, 237, 50), Color.LIME, "<plantscolour>"),
    ZOMBIES("Zombies", TextColor.color(136, 21, 237), Color.PURPLE, "<zombiescolour>"),
    NULL("null", TextColor.color(0, 0, 0), Color.BLACK,"")
}