package dev.byrt.burb.player.nametag

import dev.byrt.burb.game.GameManager
import dev.byrt.burb.logger
import dev.byrt.burb.team.BurbTeam
import dev.byrt.burb.team.PlayerTeamChangedEvent
import dev.byrt.burb.text.Formatting
import net.kyori.adventure.text.Component
import org.bukkit.Color
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler

// TODO(lucy): finish this
class LobbyNameTagProvider : NameTagProvider() {

    fun getRank(player: Player) = if (player.isOp) Formatting.glyph("\uD002") else Component.empty()

    override val lines: Int = 2

    override fun setUpForPlayer(player: Player, nametag: NameTag) {
        nametag[0].backgroundColor = Color.fromARGB(0)
        nametag[0] = getRank(player)

        nametag[1] = teamName(player, GameManager.teams.getTeam(player.uniqueId))
    }

    private fun teamName(player: Player, team: BurbTeam?) = team?.let {
        it.playerNamePrefix.append(Component.text(player.name, it.textColour))
    } ?: Component.text(player.name)


    @EventHandler
    public fun onTeamChange(e: PlayerTeamChangedEvent) {
        val tag = nametags[e.player.uniqueId] ?: return
        tag[1] = teamName(e.player, e.newTeam as BurbTeam?)
    }
}