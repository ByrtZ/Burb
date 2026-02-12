package dev.byrt.burb.command

import dev.byrt.burb.text.Formatting
import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.interfaces.BurbInterface
import dev.byrt.burb.interfaces.BurbInterfaceType
import dev.byrt.burb.player.PlayerManager.burbPlayer

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.annotations.processing.CommandContainer

@Suppress("unused", "unstableApiUsage")
@CommandContainer
class PlayerCommands {
    @Command("player <player>")
    @CommandDescription("Gets all information about a player that is stored.")
    @Permission("burb.cmd.player")
    fun getPlayer(sender: CommandSender, @Argument("player") player: Player) {
        val abilityNames = mutableSetOf<String>()
        player.burbPlayer().playerCharacter.characterAbilities.abilitySet.forEach { ability -> abilityNames.add(ability.abilityName) }
        sender.sendMessage(Formatting.allTags.deserialize("<burbcolour><bold>Player ${player.name}'s Info:<reset>\n<speccolour>Name: <yellow>${player.burbPlayer().playerName}<speccolour>\nUUID: <yellow>${player.burbPlayer().uuid}<speccolour>\nType: <yellow>${player.burbPlayer().playerType}<speccolour>\nTeam: <yellow>${player.burbPlayer().playerTeam}<speccolour>\nCharacter: <yellow>${player.burbPlayer().playerCharacter.characterName}<speccolour>\nCharacter Weapon: <yellow>${player.burbPlayer().playerCharacter.characterMainWeapon.weaponName}<speccolour>\nCharacter Abilites: <yellow>${abilityNames}<speccolour>\nDead: <yellow>${player.burbPlayer().isDead}"))
    }

    @Command("player <player> type")
    @CommandDescription("Gets the specified player's type.")
    @Permission("burb.cmd.player")
    fun getType(sender: CommandSender, @Argument("player") player: Player) {
        sender.sendMessage(Formatting.allTags.deserialize("<yellow>Player <gold>${player.name}<yellow>'s type is <gold>${player.burbPlayer().playerType}<yellow>."))
    }

    @Command("player <player> team")
    @CommandDescription("Gets the specified player's team.")
    @Permission("burb.cmd.player")
    fun getTeam(sender: CommandSender, @Argument("player") player: Player) {
        sender.sendMessage(Formatting.allTags.deserialize("<yellow>Player <gold>${player.name}<yellow>'s team is <gold>${player.burbPlayer().playerTeam}<yellow>."))
    }

    @Command("character")
    @CommandDescription("Opens character selection screen.")
    @Permission("burb.cmd.player")
    fun setCharacter(player: Player) {
        if (GameManager.getGameState() == GameState.IDLE || player.isOp) {
            if (GameManager.teams.isParticipating(player.uniqueId)) {
                BurbInterface(player, BurbInterfaceType.CHARACTER_SELECT)
            }
        }
    }

    @Command("teams")
    @CommandDescription("Opens team selection screen.")
    @Permission("burb.cmd.player")
    fun setTeam(player: Player) {
        if (GameManager.getGameState() == GameState.IDLE || player.isOp) {
            BurbInterface(player, BurbInterfaceType.TEAM_SELECT)
        }
    }

    @Command("wardrobe")
    @CommandDescription("Opens the wardrobe.")
    @Permission("burb.cmd.wardrobe")
    fun openWardrobe(player: Player) {
        if (GameManager.getGameState() == GameState.IDLE || player.isOp) {
            BurbInterface(player, BurbInterfaceType.WARDROBE)
        }
    }
}