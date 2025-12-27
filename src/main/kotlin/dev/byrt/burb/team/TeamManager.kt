package dev.byrt.burb.team

import dev.byrt.burb.text.ChatUtility
import dev.byrt.burb.text.Formatting
import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.item.ItemManager
import dev.byrt.burb.library.Translation
import dev.byrt.burb.player.BurbPlayer
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.player.PlayerType
import dev.byrt.burb.player.characterSelect
import dev.byrt.burb.plugin

import fr.skytasul.glowingentities.GlowingEntities

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.title.Title

import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Team

import java.time.Duration

object TeamManager {
    private val spectators = mutableSetOf<BurbPlayer>()
    private val plants = mutableSetOf<BurbPlayer>()
    private val zombies = mutableSetOf<BurbPlayer>()

    private var plantsDisplayTeam = Bukkit.getScoreboardManager().mainScoreboard.registerNewTeam("b_plants")
    private var zombiesDisplayTeam = Bukkit.getScoreboardManager().mainScoreboard.registerNewTeam("c_zombies")
    private var spectatorDisplayTeam = Bukkit.getScoreboardManager().mainScoreboard.registerNewTeam("z_spectator")
    private var adminDisplayTeam = Bukkit.getScoreboardManager().mainScoreboard.registerNewTeam("a_admin")

    private val GlowingEntities = GlowingEntities(plugin)

    fun setTeam(player: BurbPlayer, team: Teams) {
        cancelAllGlowing(player.getBukkitPlayer())
        if(spectators.contains(player)) {
            spectators.remove(player)
            spectatorDisplayTeam.removePlayer(Bukkit.getOfflinePlayer(player.uuid))
            adminDisplayTeam.removePlayer(Bukkit.getOfflinePlayer(player.uuid))
        }
        if(plants.contains(player)) {
            plants.remove(player)
            plantsDisplayTeam.removePlayer(Bukkit.getOfflinePlayer(player.uuid))
        }
        if(zombies.contains(player)) {
            zombies.remove(player)
            zombiesDisplayTeam.removePlayer(Bukkit.getOfflinePlayer(player.uuid))
        }
        when(team) {
            Teams.SPECTATOR -> {
                spectators.add(player)
                player.setType(PlayerType.SPECTATOR)
                if(player.getBukkitPlayer().isOp) {
                    adminDisplayTeam.addPlayer(Bukkit.getOfflinePlayer(player.uuid))
                } else {
                    spectatorDisplayTeam.addPlayer(Bukkit.getOfflinePlayer(player.uuid))
                }
            }
            Teams.PLANTS -> {
                plants.add(player)
                player.setType(PlayerType.PARTICIPANT)
                plantsDisplayTeam.addPlayer(Bukkit.getOfflinePlayer(player.uuid))
            }
            Teams.ZOMBIES -> {
                zombies.add(player)
                player.setType(PlayerType.PARTICIPANT)
                zombiesDisplayTeam.addPlayer(Bukkit.getOfflinePlayer(player.uuid))
            }
            Teams.NULL -> {
                player.setType(PlayerType.INVALID)
            }
        }
        ItemManager.givePlayerTeamBoots(player.getBukkitPlayer(), team)
        player.getBukkitPlayer().sendMessage(Formatting.allTags.deserialize(Translation.Teams.JOIN_TEAM.replace("%d", team.teamColourTag).replace("%s", team.teamName)))
        player.getBukkitPlayer().showTitle(
            Title.title(
                Formatting.allTags.deserialize(""),
                Formatting.allTags.deserialize(Translation.Teams.JOIN_TEAM.replace("%d", team.teamColourTag).replace("%s", team.teamName)),
                Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(3), Duration.ofMillis(250))
            )
        )
        if(GameManager.getGameState() in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
            if(player.playerTeam in listOf(Teams.PLANTS, Teams.ZOMBIES)) {
                refreshGlowing()
            }
        }
        player.characterSelect()
    }

    fun shuffleTeams(sender: CommandSender?, players: Set<Player>, ignoreAdmins: Boolean) {
        for((i, player) in players.withIndex()) {
            if(i % 2 == 0) {
                player.burbPlayer().setTeam(Teams.ZOMBIES)
            } else {
                player.burbPlayer().setTeam(Teams.PLANTS)
            }
        }
        ChatUtility.broadcastDev("<dark_gray>Teams shuffled by ${sender?.name ?: "the game"} ${if(ignoreAdmins) "<italic>[Non-Admins]</italic>." else "."}", false)
    }

    fun getTeam(team: Teams): Set<BurbPlayer> {
        return when(team) {
            Teams.SPECTATOR -> spectators
            Teams.PLANTS -> plants
            Teams.ZOMBIES -> zombies
            Teams.NULL -> emptySet()
        }
    }

    fun getParticipants(): Set<BurbPlayer> {
        return this.plants + this.zombies
    }

    fun getSpectators(): Set<BurbPlayer> {
        return this.spectators
    }

    fun getPlants(): Set<BurbPlayer> {
        return this.plants
    }

    fun getZombies(): Set<BurbPlayer> {
        return this.zombies
    }

    fun Set<BurbPlayer>.getPlayerNames(): ArrayList<String> {
        val playerNames = ArrayList<String>()
        for(player in this) {
            playerNames.add(player.playerName)
        }
        return playerNames
    }

    fun Teams.getTeammates(burbPlayer: BurbPlayer): Set<Player> {
        val teamMates = mutableSetOf<Player>()
        when(this) {
            Teams.PLANTS -> {
                for(teamMate in plants) {
                    if(teamMate != burbPlayer) {
                        teamMates.add(teamMate.getBukkitPlayer())
                    }
                }
            }
            Teams.ZOMBIES -> {
                for(teamMate in zombies) {
                    if(teamMate != burbPlayer) {
                        teamMates.add(teamMate.getBukkitPlayer())
                    }
                }
            }
            else -> { /* do nothing */ }
        }
        return teamMates
    }

    fun Teams.areTeamMatesDead(burbPlayer: BurbPlayer): Boolean {
        val teamMates = mutableSetOf<BurbPlayer>()
        when(this) {
            Teams.PLANTS -> {
                for(teamMate in plants) {
                    if(teamMate != burbPlayer) {
                        if(teamMate.isDead) {
                            teamMates.add(teamMate)
                        }
                    }
                }
                return if(teamMates.size <= 0) false else teamMates.size >= plants.size - 1
            }
            Teams.ZOMBIES -> {
                for(teamMate in zombies) {
                    if(teamMate != burbPlayer) {
                        if(teamMate.isDead) {
                            teamMates.add(teamMate)
                        }
                    }
                }
                return if(teamMates.size <= 0) false else teamMates.size >= zombies.size - 1
            }
            else -> { return false }
        }
    }

    fun isTeamDead(team: Teams): Boolean {
        val deadTeammates = mutableSetOf<BurbPlayer>()
        return when(team) {
            Teams.PLANTS -> {
                for(player in plants) {
                    if(player.isDead) {
                        deadTeammates.add(player)
                    }
                }
                deadTeammates.size >= plants.size
            }
            Teams.ZOMBIES -> {
                for(player in zombies) {
                    if(player.isDead) {
                        deadTeammates.add(player)
                    }
                }
                deadTeammates.size >= zombies.size
            }
            else -> false
        }
    }

    fun refreshGlowing() {
        for(player in Bukkit.getOnlinePlayers()) {
            cancelAllGlowing(player)
            enableTeamGlowing(player)
        }
    }

    fun enableTeamGlowing(player: Player) {
        val teamMates = player.burbPlayer().playerTeam.getTeammates(player.burbPlayer())
        if(player.burbPlayer().playerTeam.getTeammates(player.burbPlayer()).isNotEmpty()) {
            for(teamMate in teamMates) {
                GlowingEntities.setGlowing(teamMate, player)
            }
        }
    }

    fun disableTeamGlowing(player: Player) {
        for(teamMate in player.burbPlayer().playerTeam.getTeammates(player.burbPlayer())) {
            GlowingEntities.unsetGlowing(teamMate, player)
        }
    }

    fun cancelAllGlowing(player: Player) {
        for(otherPlayer in Bukkit.getOnlinePlayers()) {
            GlowingEntities.unsetGlowing(otherPlayer, player)
        }
    }

    fun showTeamNametags() {
        plantsDisplayTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS)
        zombiesDisplayTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS)
    }

    fun hideTeamNametags() {
        plantsDisplayTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS)
        zombiesDisplayTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS)
    }

    fun buildDisplayTeams() {
        plantsDisplayTeam.color(NamedTextColor.GREEN)
        plantsDisplayTeam.prefix(Component.text("\uD83E\uDEB4 ").color(NamedTextColor.WHITE))
        plantsDisplayTeam.suffix(Component.text("").color(NamedTextColor.WHITE))
        plantsDisplayTeam.displayName(Component.text("Plants").color(Teams.PLANTS.teamHexColour))
        plantsDisplayTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER)
        plantsDisplayTeam.setAllowFriendlyFire(false)

        zombiesDisplayTeam.color(NamedTextColor.DARK_PURPLE)
        zombiesDisplayTeam.prefix(Component.text("\uD83E\uDDDF ").color(NamedTextColor.WHITE))
        zombiesDisplayTeam.suffix(Component.text("").color(NamedTextColor.WHITE))
        zombiesDisplayTeam.displayName(Component.text("Zombies").color(Teams.ZOMBIES.teamHexColour))
        zombiesDisplayTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER)
        zombiesDisplayTeam.setAllowFriendlyFire(false)

        adminDisplayTeam.color(NamedTextColor.DARK_RED)
        adminDisplayTeam.prefix(Component.text("\uD002 ").color(NamedTextColor.WHITE))
        adminDisplayTeam.suffix(Component.text("").color(NamedTextColor.WHITE))
        adminDisplayTeam.displayName(Component.text("Admin").color(NamedTextColor.DARK_RED))
        adminDisplayTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER)
        adminDisplayTeam.setAllowFriendlyFire(false)

        spectatorDisplayTeam.color(NamedTextColor.GRAY)
        spectatorDisplayTeam.prefix(Component.text("\uD003 ").color(NamedTextColor.WHITE))
        spectatorDisplayTeam.suffix(Component.text("").color(NamedTextColor.WHITE))
        spectatorDisplayTeam.displayName(Component.text("Spectator").color(Teams.SPECTATOR.teamHexColour))
        spectatorDisplayTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER)
        spectatorDisplayTeam.setAllowFriendlyFire(false)
    }

    fun destroyDisplayTeams() {
        plantsDisplayTeam.unregister()
        zombiesDisplayTeam.unregister()
        adminDisplayTeam.unregister()
        spectatorDisplayTeam.unregister()
    }
}

enum class Teams(val teamName: String, val teamHexColour: TextColor, val teamColour: Color, val teamColourTag: String) {
    SPECTATOR("Spectator", TextColor.fromHexString("#aaaaaa")!!, Color.GRAY, "<speccolour>"),
    PLANTS("Plants", TextColor.color(21, 237, 50), Color.LIME, "<plantscolour>"),
    ZOMBIES("Zombies", TextColor.color(136, 21, 237), Color.PURPLE, "<zombiescolour>"),
    NULL("null", TextColor.color(0, 0, 0), Color.BLACK,"<#000000>")
}