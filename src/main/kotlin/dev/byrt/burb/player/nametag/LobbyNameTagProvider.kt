package dev.byrt.burb.player.nametag

import dev.byrt.burb.game.GameManager
import dev.byrt.burb.player.progression.BurbExperienceLevels
import dev.byrt.burb.player.progression.BurbLevel
import dev.byrt.burb.player.progression.PlayerLevelUpEvent
import dev.byrt.burb.team.BurbTeam
import dev.byrt.burb.team.PlayerTeamChangedEvent
import dev.byrt.burb.text.Formatting
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler

class LobbyNameTagProvider : NameTagProvider() {
    override val lines: Int = 2

    override fun setUpForPlayer(player: Player, nametag: NameTag) {
        nametag[0] = level(player)
        nametag[1] = teamName(player, GameManager.teams.getTeam(player.uniqueId))
    }

    private fun level(player: Player, level: BurbLevel = BurbExperienceLevels.getLevel(player)): Component {
        val text = Component.text()
        if (player.isOp) text.append(Formatting.glyph("\uD002")).append(Component.text("  "))
        text.append(Component.text("LVL ${level.levelOrdinal}", level.textColour).font(Formatting.BURB_FONT))
        return text.build()
    }

    private fun teamName(player: Player, team: BurbTeam?) = team?.let {
        Component.text()
            .append(it.playerNamePrefix)
            .append(Component.text(player.name, it.textColour))
            .build()
    } ?: Component.text(player.name)


    @EventHandler
    public fun onTeamChange(e: PlayerTeamChangedEvent) {
        val tag = nametags[e.player.uniqueId] ?: return
        tag[1] = teamName(e.player, e.newTeam as BurbTeam?)
    }

    @EventHandler
    fun onLevelUp(e: PlayerLevelUpEvent) {
        val tag = nametags[e.player.uniqueId] ?: return
        tag[0] = level(e.player, e.newLevel)
    }
}