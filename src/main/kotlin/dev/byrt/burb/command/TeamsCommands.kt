package dev.byrt.burb.command

import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.team.BurbTeam
import dev.byrt.burb.text.ChatUtility
import dev.byrt.burb.text.Formatting
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
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
    fun setTeam(sender: CommandSender, @Argument("player") player: Player, @Argument("team") team: BurbTeam) {
        if (GameManager.getGameState() == GameState.IDLE) {
            GameManager.teams.setTeam(player, team)
        } else {
            sender.sendMessage(Formatting.allTags.deserialize("<red>Teams cannot be modified in this state."))
        }
    }

    @Command("teams remove <player>")
    @CommandDescription("Removes a player from their team.")
    @Permission("burb.cmd.teams")
    fun removeFromTeam(sender: CommandSender, @Argument("player") player: Player) {
        if (GameManager.getGameState() == GameState.IDLE) {
            GameManager.teams.setTeam(player, null)
        } else {
            sender.sendMessage(Formatting.allTags.deserialize("<red>Teams cannot be modified in this state."))
        }
    }

    @Command("teams shuffle")
    @CommandDescription("Automatically assigns everyone online to a team.")
    @Permission("burb.cmd.teams")
    fun autoTeam(sender: CommandSender, @Flag("ignoreAdmins") doesIgnoreAdmins: Boolean) {
        if (GameManager.getGameState() != GameState.IDLE) {
            sender.sendMessage(Formatting.allTags.deserialize("<red>Teams cannot be modified in this state."))
            return
        }

        val players = Bukkit.getOnlinePlayers()
            .let { if (doesIgnoreAdmins) it.filter(Player::isOp) else it }
            .shuffled()
            .takeIf { it.isNotEmpty() }
            ?: return sender.sendMessage(
                Formatting.allTags.deserialize("<red>Teams cannot be shuffled in non-admin mode if only admins are online.")
            )

        players.forEachIndexed { index, player ->
            GameManager.teams.setTeam(player, if (index % 2 == 0) BurbTeam.PLANTS else BurbTeam.ZOMBIES)
        }
        ChatUtility.broadcastDev("<dark_gray>Teams shuffled by ${sender.name}.", false)
        teamList(sender, TeamsListOptions.ALL)
    }

    @Command("teams list <option>")
    @CommandDescription("Allows the executing player to see the array of the specified team.")
    @Permission("burb.cmd.teams")
    fun teamList(sender: CommandSender, @Argument("option") option: TeamsListOptions) {
        // TODO(lucy): reimplement options
        GameManager.teams.allParticipants()
            .groupBy { it.playerTeam }
            .forEach { (team, players) ->
                Component.text()
                    .append(team ?: return@forEach)
                    .append(
                        Component.join(JoinConfiguration.spaces(), players.map { it.bukkitPlayer().displayName() })
                    )
            }
    }
}

enum class TeamsListOptions {
    SPECTATOR,
    PLANTS,
    ZOMBIES,
    ALL
}