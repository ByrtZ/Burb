package dev.byrt.burb.team

import dev.byrt.burb.player.BurbPlayer
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.player.characterSelect
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Team
import java.util.*
import kotlin.enums.EnumEntries
import kotlin.enums.enumEntries
import kotlin.reflect.KClass

/**
 * Manages the team a player is assigned to.
 */
class TeamManagerV2<T> @PublishedApi internal constructor(
    private val teamClazz: KClass<T>,
    private val allTeams: EnumEntries<T>
) where T : GameTeam, T : Enum<T> {

    companion object {
        inline operator fun <reified T> invoke() where T : GameTeam, T : Enum<T> =
            TeamManagerV2(T::class, enumEntries<T>())
    }

    private val playerTeams = mutableMapOf<UUID, T>()

    private val scoreboard = Bukkit.getScoreboardManager().newScoreboard
    private val scoreboardTeams = allTeams.associateWith {
        scoreboard.registerNewTeam(it.name).apply {
            displayName(Component.text(it.name))
            prefix(Component.text(it.name + " ")) // temp, to be replaced by another system
            setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER)
            color(NamedTextColor.nearestTo(it.textColour))
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

        if (team != null) {
            scoreboardTeams.getValue(team).addPlayer(player)
            player.burbPlayer().characterSelect()
        }
    }
}