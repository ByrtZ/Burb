package dev.byrt.burb.library

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound

object Sounds {
    object Music {
        val OVERTIME_MUSIC = Sound.sound(Key.key("burb.game.overtime"), Sound.Source.VOICE, 0.75f, 1f)
        val POST_GAME_MUSIC = Sound.sound(Key.key("burb.game.post_game.1"), Sound.Source.VOICE, 0.75f, 1f)
        val LOBBY_TITLE_SCREEN = Sound.sound(Key.key("burb.lobby.title_screen"), Sound.Source.VOICE, 0.35f, 1f)
        val LOBBY_INTRO = Sound.sound(Key.key("burb.lobby.intro"), Sound.Source.VOICE, 0.4f, 1f)
        val LOBBY_WAITING = Sound.sound(Key.key("burb.lobby.waiting"), Sound.Source.VOICE, 0.75f, 1f)
        val LOADING_MELODY = Sound.sound(Key.key("burb.game.loading_melody"), Sound.Source.VOICE, 0.75f, 1f)
        val GAME_INTRO_JINGLE = Sound.sound(Key.key("burb.game.intro_jingle"), Sound.Source.VOICE, 1f, 1f)
        val SUBURBINATION_PLANTS = Sound.sound(Key.key("burb.game.suburbination.plants"), Sound.Source.VOICE, 0.75f, 1f)
        val SUBURBINATION_ZOMBIES = Sound.sound(Key.key("burb.game.suburbination.zombies"), Sound.Source.VOICE, 0.75f, 1f)
        val RANDOM_LOW = Sound.sound(Key.key("burb.music.low.random"), Sound.Source.VOICE, 0.4f, 1f)
        val RANDOM_MEDIUM = Sound.sound(Key.key("burb.music.medium.random"), Sound.Source.VOICE, 0.4f, 1f)
        val RANDOM_HIGH = Sound.sound(Key.key("burb.music.high.random"), Sound.Source.VOICE, 0.4f, 1f)
        val DOWNTIME_LOOP = Sound.sound(Key.key("event.downtime.loop"), Sound.Source.VOICE, 1f, 1f)
        val DOWNTIME_SUSPENSE = Sound.sound(Key.key("event.downtime.suspense"), Sound.Source.VOICE, 1f, 1f)
        val NULL = Sound.sound(Key.key(""), Sound.Source.VOICE, 0f, 0f)
    }
    object Timer {
        val STARTING_123 = Sound.sound(Key.key("burb.generic.tick"), Sound.Source.VOICE, 1.25f, 1f)
        val STARTING_GO = Sound.sound(Key.key("block.end_portal.spawn"), Sound.Source.VOICE, 0.75f, 1f)
        val TICK = Sound.sound(Key.key("burb.generic.tick"), Sound.Source.VOICE, 1.25f, 1f)
        val CLOCK_TICK = Sound.sound(Key.key("burb.generic.clock_tick"), Sound.Source.VOICE, 1.25f, 1f)
        val CLOCK_TICK_HIGH = Sound.sound(Key.key("burb.generic.clock_tick"), Sound.Source.VOICE, 1.25f, 1.25f)
    }
    object Round {
        val ROUND_END = Sound.sound(Key.key("block.respawn_anchor.deplete"), Sound.Source.VOICE, 1f, 1f)
        val GAME_OVER = Sound.sound(Key.key("ui.toast.challenge_complete"), Sound.Source.VOICE, 0.75f, 1f)
    }
    object Score {
        val ELIMINATION = Sound.sound(Key.key("burb.generic.vanquish"), Sound.Source.VOICE, 1f, 1f)
        val CAPTURE_FRIENDLY = Sound.sound(Key.key("burb.game.capture.friendly"), Sound.Source.VOICE, 1.25f, 1f)
        val CAPTURE_UNFRIENDLY = Sound.sound(Key.key("burb.game.capture.unfriendly"), Sound.Source.VOICE, 1.25f, 1f)
        val DEATH = Sound.sound(Key.key("burb.game.death"), Sound.Source.VOICE, 1f, 1f)
        val DEATH_STATS = Sound.sound(Key.key("burb.game.death_stats"), Sound.Source.VOICE, 0.75f, 1f)
        val RESPAWN = Sound.sound(Key.key("block.bubble_column.upwards_inside"), Sound.Source.VOICE, 2f, 0.0f)
        val PLANTS_WIN = Sound.sound(Key.key("burb.game.win.plants"), Sound.Source.VOICE, 0.75f, 1f)
        val PLANTS_LOSE = Sound.sound(Key.key("burb.game.lose.plants"), Sound.Source.VOICE, 0.75f, 1f)
        val ZOMBIES_WIN = Sound.sound(Key.key("burb.game.win.zombies"), Sound.Source.VOICE, 0.75f, 1f)
        val ZOMBIES_LOSE = Sound.sound(Key.key("burb.game.lose.zombies"), Sound.Source.VOICE, 0.75f, 1f)
    }
    object Alert {
        val ALARM = Sound.sound(Key.key("burb.generic.alarm"), Sound.Source.VOICE, 1.25f, 1f)
    }
    object Tutorial {
        const val TUTORIAL_POP = "entity.item.pickup"
    }
    object Misc {
        val ADMIN_MESSAGE = Sound.sound(Key.key("ui.button.click"), Sound.Source.MASTER, 0.5f, 2f)
        val INTERFACE_INTERACT = Sound.sound(Key.key("ui.button.click"), Sound.Source.MASTER, 0.5f, 1f)
        val INTERFACE_INTERACT_FAIL = Sound.sound(Key.key("entity.enderman.teleport"), Sound.Source.MASTER, 0.5f, 0f)
        val TITLE_SCREEN_ENTER = Sound.sound(Key.key("entity.breeze.shoot"), Sound.Source.MASTER, 1f, 0.75f)
        val ODE_TO_JOY = Sound.sound(Key.key("burb.generic.ode_to_joy"), Sound.Source.VOICE, 0.75f, 1f)
        val SUCCESS = Sound.sound(Key.key("burb.generic.success"), Sound.Source.VOICE, 0.75f, 1f)
        val LEVEL_UP_PLANTS = Sound.sound(Key.key("burb.generic.level_up.plants"), Sound.Source.VOICE, 1f, 1f)
        val LEVEL_UP_ZOMBIES = Sound.sound(Key.key("burb.generic.level_up.zombies"), Sound.Source.VOICE, 1f, 1f)
    }
    object Weapon {
        const val RELOAD_TICK = "block.note_block.hat"
        val RELOAD_SUCCESS = Sound.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER, 1f, 1.5f)
    }
}