package dev.byrt.burb.player.displayname

import dev.byrt.burb.game.GameManager
import dev.byrt.burb.team.PlayerTeamChangedEvent
import dev.byrt.burb.text.Formatting
import me.lucyydotp.tinsel.font.Spacing
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

/**
 * Sets player display names in response to game events.
 */
class PlayerNameFormatter : Listener {
    private fun setDisplayName(player: Player) {
        val team = GameManager.teams.getTeam(player.uniqueId)

        var prefix: Component?
        var color: TextColor = NamedTextColor.GRAY

        when {
            team != null -> {
                prefix = team.playerNamePrefix
                color = team.textColour
            }
            player.isOp -> {
                prefix = Formatting.glyph("\uD002")
                color = TextColor.color(0xc11414)
            }
            else -> {
                prefix = Formatting.glyph("\uD003")
            }
        }

        player.displayName(
            Component.empty()
                .append(prefix)
                .append(Spacing.spacing(2))
                .append(Component.text(player.name).color(color))
        )
        player.playerListName(player.displayName())

        Bukkit.getPluginManager().callEvent(DisplayNameChangeEvent(player))
    }

    @EventHandler(priority = EventPriority.HIGH)
    private fun onJoin(e: PlayerJoinEvent) {
        setDisplayName(e.player)
    }

    @EventHandler
    private fun onTeamChange(e: PlayerTeamChangedEvent) {
        setDisplayName(e.player)
    }
}