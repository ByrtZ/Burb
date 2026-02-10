package dev.byrt.burb.team

import dev.byrt.burb.library.Sounds
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike

enum class BurbTeam(
    override val teamDisplayName: String,
    val winMusic: Sound,
    val winSound: Sound,
    val loseSound: Sound,
) : GameTeam, ComponentLike {
    PLANTS("Plants", Sounds.Score.PLANTS_WIN_MUSIC, Sounds.Score.PLANTS_WIN, Sounds.Score.PLANTS_LOSE),
    ZOMBIES("Zombies", Sounds.Score.ZOMBIES_WIN_MUSIC, Sounds.Score.ZOMBIES_WIN, Sounds.Score.ZOMBIES_LOSE),
    ;

    override fun asComponent() = Component.translatable("burb.team.${name.lowercase()}")
}