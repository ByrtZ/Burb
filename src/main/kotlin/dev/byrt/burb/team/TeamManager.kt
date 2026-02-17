package dev.byrt.burb.team

import dev.byrt.burb.logger
import dev.byrt.burb.player.BurbPlayer
import dev.byrt.burb.player.PlayerGlowing
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.player.character.characterSelect
import dev.byrt.burb.text.InfoBoardManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scoreboard.Team
import java.time.Duration
import java.util.*
import kotlin.enums.EnumEntries
import kotlin.enums.enumEntries
import kotlin.reflect.KClass

/**
 * Manages the team a player is assigned to.
 */
class TeamManager<T> @PublishedApi internal constructor(
    private val teamClazz: KClass<T>,
    private val allTeams: EnumEntries<T>
) : Listener where T : GameTeam, T : Enum<T> {

    companion object {
        inline operator fun <reified T> invoke() where T : GameTeam, T : Enum<T> =
            TeamManager(T::class, enumEntries<T>())
    }

    private val playerTeams = mutableMapOf<UUID, T>()

    private val scoreboardTeams = allTeams.associateWith {
        InfoBoardManager.scoreboard.registerNewTeam(it.name).apply {
            displayName(Component.text(it.name))
            setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER)
            setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER)
            color(NamedTextColor.nearestTo(it.textColour))
        }
    }

    /**
     * Whether team glowing is enabled.
     */
    public var teamGlowingEnabled = false
        set(value) {
            if (field == value) return
            field = value

            allTeams.forEach { team ->
                if (value) {
                    teamMembers(team).forEach {
                        PlayerGlowing.addToGlowingGroup("team_${team.name}", it.bukkitPlayer())
                    }
                } else {
                    PlayerGlowing.removeGroup("team_${team.name}")
                }
            }
        }

    /**
     * Whether a player is participating in the game, i.e. they are on a team.
     */
    fun isParticipating(player: UUID): Boolean = player in playerTeams

    /**
     * A set of all participating players.
     */
    fun allParticipants(): Set<BurbPlayer> = playerTeams.keys.mapTo(mutableSetOf()) { it.burbPlayer() }

    /**
     * Gets all the players on a team.
     */
    fun teamMembers(team: T) : Set<BurbPlayer> = playerTeams.filterValues { it == team }.keys.mapTo(mutableSetOf()) { it.burbPlayer() }

    /**
     * Gets the team the player is on.
     */
    fun getTeam(player: UUID): T? = playerTeams[player]

    /**
     * Sets the team the player is on.
     */
    fun setTeam(player: Player, team: T?) {
        val previousTeam = if (team == null) {
            playerTeams.remove(player.uniqueId)
        } else {
            playerTeams.put(player.uniqueId, team)
        }

        if (previousTeam != null) {
            scoreboardTeams.getValue(previousTeam).removePlayer(player)
        }

        Bukkit.getPluginManager().callEvent(PlayerTeamChangedEvent(player, team))

        val teamChangeComponent = Component.translatable("burb.team.change", Component.text(team?.teamDisplayName ?: "<gray>Spectator"))
        player.sendMessage(teamChangeComponent)
        player.showTitle(Title.title(
            Component.empty(),
            teamChangeComponent,
            Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1)),
        ))

        logger.info("Teams: ${player.name} now has value ${team?.name}.")

        if (team != null) {
            scoreboardTeams.getValue(team).addPlayer(player)
            player.burbPlayer().characterSelect()
            if (teamGlowingEnabled) {
                PlayerGlowing.addToGlowingGroup("team_${team.name}", player)
            }
        }
    }

    /**
     * Remove players from their team on quit.
     */
    @EventHandler
    private fun onPlayerQuit(event: PlayerQuitEvent) {
        setTeam(event.player, null)
    }
}