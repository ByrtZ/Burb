package dev.byrt.burb.team

import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.format.TextColor

/**
 * A team. Intended to be implemented by an enum.
 */
interface GameTeam: ComponentLike {
    val teamDisplayName: String
    val textColour: TextColor
}