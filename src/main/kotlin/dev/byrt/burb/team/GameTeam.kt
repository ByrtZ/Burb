package dev.byrt.burb.team

import net.kyori.adventure.text.format.TextColor

/**
 * A team. Intended to be implemented by an enum.
 */
interface GameTeam {
    val teamDisplayName: String
    val textColour: TextColor
}