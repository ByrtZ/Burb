package dev.byrt.burb.team

import dev.byrt.burb.library.Sounds
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Color

enum class BurbTeam(
    override val teamDisplayName: String,
    val winMusic: Sound,
    val winSound: Sound,
    val loseSound: Sound,
    val teamColour: Color,
    val textColour: TextColor,
) : GameTeam, ComponentLike {
    PLANTS(
        "Plants",
        Sounds.Score.PLANTS_WIN_MUSIC,
        Sounds.Score.PLANTS_WIN,
        Sounds.Score.PLANTS_LOSE,
        Color.LIME,
        TextColor.color(21, 237, 50)
    ),
    ZOMBIES(
        "Zombies",
        Sounds.Score.ZOMBIES_WIN_MUSIC,
        Sounds.Score.ZOMBIES_WIN,
        Sounds.Score.ZOMBIES_LOSE,
        Color.PURPLE,
        TextColor.color(136, 21, 237),
    ),
    ;

    override fun asComponent() = Component.translatable("burb.team.${name.lowercase()}.normal")

    fun uppercaseName() = Component.translatable("burb.team.${name.lowercase()}.uppercase")
}