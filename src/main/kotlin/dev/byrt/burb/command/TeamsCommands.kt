package dev.byrt.burb.command

import dev.byrt.burb.text.ChatUtility
import dev.byrt.burb.text.Formatting
import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.plugin
import dev.byrt.burb.team.BurbTeam
import dev.byrt.burb.team.TeamManager
import dev.byrt.burb.team.TeamManager.getPlayerNames
import dev.byrt.burb.team.Teams

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

import org.incendo.cloud.annotations.*
import org.incendo.cloud.annotations.processing.CommandContainer

@Suppress("unused", "unstableApiUsage")
@CommandContainer
class TeamsCommands {
    @Command("teams set <player> <team>")
    @CommandDescription("Puts the specified player on the specified team.")
    @Permission("burb.cmd.teams")
    fun setTeam(sender: CommandSender, @Argument("player") player : Player, @Argument("team") team : BurbTeam) {
        if(GameManager.getGameState() == GameState.IDLE) {
            GameManager.teams.setTeam(player, team)
        } else {
            sender.sendMessage(Formatting.allTags.deserialize("<red>Teams cannot be modified in this state."))
        }
    }

    @Command("teams shuffle")
    @CommandDescription("Automatically assigns everyone online to a team.")
    @Permission("burb.cmd.teams")
    fun autoTeam(sender: CommandSender, @Flag("ignoreAdmins") doesIgnoreAdmins: Boolean) {
        if(GameManager.getGameState() == GameState.IDLE) {
            if(!doesIgnoreAdmins) {
                ChatUtility.broadcastDev("<dark_gray>Teams shuffled by ${sender.name}.", false)
                TeamManager.shuffleTeams(sender, plugin.server.onlinePlayers.toSet(), false)
            } else {
                try {
                    val nonAdmins = mutableSetOf<Player>()
                    for(player in Bukkit.getOnlinePlayers()) {
                        if(!player.isOp) {
                            nonAdmins.add(player)
                        }
                    }
                    if(nonAdmins.isEmpty()) {
                        sender.sendMessage(Formatting.allTags.deserialize("<red>Teams cannot be shuffled in non-admin mode if only admins are online."))
                    } else {
                        TeamManager.shuffleTeams(sender, nonAdmins, true)
                    }
                } catch(e : Exception) {
                    sender.sendMessage(Formatting.allTags.deserialize("<red>An unknown error occurred when attempting to shuffle teams."))
                }
            }
        } else {
            sender.sendMessage(Formatting.allTags.deserialize("<red>Teams cannot be modified in this state."))
        }
    }

    @Command("teams list <option>")
    @CommandDescription("Allows the executing player to see the array of the specified team.")
    @Permission("burb.cmd.teams")
    fun teamList(sender: CommandSender, @Argument("option") option : TeamsListOptions) {
        when (option) {
            TeamsListOptions.PLANTS -> {
                sender.sendMessage(Formatting.allTags.deserialize("<plantscolour><bold>Plants Team<white>:<reset><newline><italic><gray>${TeamManager.getPlants().getPlayerNames()}"))
            }
            TeamsListOptions.ZOMBIES -> {
                sender.sendMessage(Formatting.allTags.deserialize("<zombiescolour><bold>Zombies Team<white>:<reset><newline><italic><gray>${TeamManager.getZombies().getPlayerNames()}"))
            }
            TeamsListOptions.SPECTATOR -> {
                sender.sendMessage(Formatting.allTags.deserialize("<speccolour><bold>Spectators<white>:<reset><newline><italic><gray>${TeamManager.getSpectators().getPlayerNames()}"))
            }
            TeamsListOptions.ALL -> {
                sender.sendMessage(Formatting.allTags.deserialize("<plantscolour><bold>Plants Team<white>:<reset><newline><italic><gray>${TeamManager.getPlants().getPlayerNames()}"))
                sender.sendMessage(Formatting.allTags.deserialize("<zombiescolour><bold>Zombies Team<white>:<reset><newline><italic><gray>${TeamManager.getZombies().getPlayerNames()}"))
                sender.sendMessage(Formatting.allTags.deserialize("<speccolour><bold>Spectators<white>:<reset><newline><italic><gray>${TeamManager.getSpectators().getPlayerNames()}"))
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