package dev.byrt.burb.command

import dev.byrt.burb.chat.Formatting
import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.interfaces.BurbInterface
import dev.byrt.burb.interfaces.BurbInterfaceType
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.team.Teams

import io.papermc.paper.command.brigadier.CommandSourceStack

import org.bukkit.entity.Player

import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.annotations.processing.CommandContainer

@Suppress("unused", "unstableApiUsage")
@CommandContainer
class PlayerCommands {
    @Command("player get <player>")
    @CommandDescription("Gets all information about a player that is stored.")
    @Permission("burb.cmd.player")
    fun getPlayer(css: CommandSourceStack, @Argument("player") player : Player) {
        val abilityNames = mutableSetOf<String>()
        player.burbPlayer().playerCharacter.characterAbilities.abilitySet.forEach { ability -> abilityNames.add(ability.abilityName) }
        css.sender.sendMessage(Formatting.allTags.deserialize("<burbcolour><bold>Player ${player.name}'s Info:<reset>\n<speccolour>Name: <yellow>${player.burbPlayer().playerName}<speccolour>\nUUID: <yellow>${player.burbPlayer().uuid}<speccolour>\nType: <yellow>${player.burbPlayer().playerType}<speccolour>\nTeam: <yellow>${player.burbPlayer().playerTeam}<speccolour>\nCharacter: <yellow>${player.burbPlayer().playerCharacter.characterName}<speccolour>\nCharacter Weapon: <yellow>${player.burbPlayer().playerCharacter.characterMainWeapon.weaponName}<speccolour>\nCharacter Abilites: <yellow>${abilityNames}"))
    }

    @Command("player get <player> type")
    @CommandDescription("Gets the specified player's type.")
    @Permission("burb.cmd.player")
    fun getType(css: CommandSourceStack, @Argument("player") player : Player) {
        css.sender.sendMessage(Formatting.allTags.deserialize("<yellow>Player <gold>${player.name}<yellow>'s type is <gold>${player.burbPlayer().playerType}<yellow>."))
    }

    @Command("player get <player> team")
    @CommandDescription("Gets the specified player's team.")
    @Permission("burb.cmd.player")
    fun getTeam(css: CommandSourceStack, @Argument("player") player : Player) {
        css.sender.sendMessage(Formatting.allTags.deserialize("<yellow>Player <gold>${player.name}<yellow>'s team is <gold>${player.burbPlayer().playerTeam}<yellow>."))
    }

    @Command("character")
    @CommandDescription("Opens character selection screen.")
    fun setCharacter(css: CommandSourceStack) {
        if(css.sender is Player && GameManager.getGameState() == GameState.IDLE || css.sender is Player && css.sender.isOp) {
            val player = css.sender as Player
            if(player.burbPlayer().playerTeam in listOf(Teams.PLANTS, Teams.ZOMBIES)) {
                BurbInterface(player, BurbInterfaceType.CHARACTER_SELECT)
            }
        }
    }

    @Command("teams")
    @CommandDescription("Opens team selection screen.")
    fun setTeam(css: CommandSourceStack) {
        if(css.sender is Player && GameManager.getGameState() == GameState.IDLE || css.sender is Player && css.sender.isOp) {
            val player = css.sender as Player
            BurbInterface(player, BurbInterfaceType.TEAM_SELECT)
        }
    }
}