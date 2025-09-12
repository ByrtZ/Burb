package dev.byrt.burb.command

import dev.byrt.burb.text.ChatUtility
import dev.byrt.burb.text.Formatting
import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.plugin
import dev.byrt.burb.team.TeamManager
import dev.byrt.burb.team.TeamManager.getPlayerNames
import dev.byrt.burb.team.Teams

import io.papermc.paper.command.brigadier.CommandSourceStack

import org.bukkit.Bukkit
import org.bukkit.entity.Player

import org.incendo.cloud.annotations.*
import org.incendo.cloud.annotations.processing.CommandContainer

@Suppress("unused", "unstableApiUsage")
@CommandContainer
class TeamsCommands {
    @Command("teams set <player> <team>")
    @CommandDescription("Puts the specified player on the specified team.")
    @Permission("burb.cmd.teams")
    fun setTeam(css: CommandSourceStack, @Argument("player") player : Player, @Argument("team") team : Teams) {
        if(GameManager.getGameState() == GameState.IDLE) {
            player.burbPlayer().setTeam(team)
        } else {
            css.sender.sendMessage(Formatting.allTags.deserialize("<red>Teams cannot be modified in this state."))
        }
    }

    @Command("teams shuffle")
    @CommandDescription("Automatically assigns everyone online to a team.")
    @Permission("burb.cmd.teams")
    fun autoTeam(css: CommandSourceStack, @Flag("ignoreAdmins") doesIgnoreAdmins: Boolean) {
        if(GameManager.getGameState() == GameState.IDLE) {
            if(!doesIgnoreAdmins) {
                ChatUtility.broadcastDev("<dark_gray>Teams shuffled by ${css.sender.name}.", false)
                TeamManager.shuffleTeams(css.sender, plugin.server.onlinePlayers.toSet(), false)
            } else {
                try {
                    val nonAdmins = mutableSetOf<Player>()
                    for(player in Bukkit.getOnlinePlayers()) {
                        if(!player.isOp) {
                            nonAdmins.add(player)
                        }
                    }
                    if(nonAdmins.isEmpty()) {
                        css.sender.sendMessage(Formatting.allTags.deserialize("<red>Teams cannot be shuffled in non-admin mode if only admins are online."))
                    } else {
                        TeamManager.shuffleTeams(css.sender, nonAdmins, true)
                    }
                } catch(e : Exception) {
                    css.sender.sendMessage(Formatting.allTags.deserialize("<red>An unknown error occurred when attempting to shuffle teams."))
                }
            }
        } else {
            css.sender.sendMessage(Formatting.allTags.deserialize("<red>Teams cannot be modified in this state."))
        }
    }

    @Command("teams list <option>")
    @CommandDescription("Allows the executing player to see the array of the specified team.")
    @Permission("burb.cmd.teams")
    fun teamList(css: CommandSourceStack, @Argument("option") option : TeamsListOptions) {
        when (option) {
            TeamsListOptions.PLANTS -> {
                css.sender.sendMessage(Formatting.allTags.deserialize("<plantscolour><bold>Plants Team<white>:<reset><newline><italic><gray>${TeamManager.getPlants().getPlayerNames()}"))
            }
            TeamsListOptions.ZOMBIES -> {
                css.sender.sendMessage(Formatting.allTags.deserialize("<zombiescolour><bold>Zombies Team<white>:<reset><newline><italic><gray>${TeamManager.getZombies().getPlayerNames()}"))
            }
            TeamsListOptions.SPECTATOR -> {
                css.sender.sendMessage(Formatting.allTags.deserialize("<speccolour><bold>Spectators<white>:<reset><newline><italic><gray>${TeamManager.getSpectators().getPlayerNames()}"))
            }
            TeamsListOptions.ALL -> {
                css.sender.sendMessage(Formatting.allTags.deserialize("<plantscolour><bold>Plants Team<white>:<reset><newline><italic><gray>${TeamManager.getPlants().getPlayerNames()}"))
                css.sender.sendMessage(Formatting.allTags.deserialize("<zombiescolour><bold>Zombies Team<white>:<reset><newline><italic><gray>${TeamManager.getZombies().getPlayerNames()}"))
                css.sender.sendMessage(Formatting.allTags.deserialize("<speccolour><bold>Spectators<white>:<reset><newline><italic><gray>${TeamManager.getSpectators().getPlayerNames()}"))
            }
        }
    }
}

enum class TeamsListOptions {
    SPECTATOR,
    PLANTS,
    ZOMBIES,
    ALL
}