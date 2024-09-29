package dev.byrt.burb.library

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound

object Sounds {
    object Music {
        val GAME_MUSIC = Sound.sound(Key.key(""), Sound.Source.VOICE, 1f, 1f)
        const val OVERTIME_INTRO_MUSIC = ""
        val OVERTIME_MUSIC = Sound.sound(Key.key(""), Sound.Source.VOICE, 0.75f, 1f)
        const val GAME_STARTING_MUSIC = ""
        const val GAME_OVER_MUSIC = ""
        const val ROUND_OVER_MUSIC = ""
        val LOBBY_INTRO = Sound.sound(Key.key("burb.lobby.intro"), Sound.Source.VOICE, 0.75f, 1f)
        val LOBBY_WAITING = Sound.sound(Key.key("burb.lobby.waiting"), Sound.Source.VOICE, 0.75f, 1f)
        val GAME_INTRO_JINGLE = Sound.sound(Key.key("burb.game.intro_jingle"), Sound.Source.VOICE, 1f, 1f)
        val NULL = Sound.sound(Key.key(""), Sound.Source.VOICE, 0f, 0f)
    }
    object Timer {
        const val STARTING_123 = ""
        const val STARTING_GO = ""
        const val CLOCK_TICK = ""
        const val CLOCK_TICK_HIGH = ""
    }
    object Round {
        const val ROUND_END = ""
        const val ENTRANCE = ""
    }
    object GameOver {
        const val GAME_OVER = ""
    }
    object Start {
        const val START_GAME_SUCCESS = "block.respawn_anchor.set_spawn"
        const val START_GAME_FAIL = "entity.enderman.teleport"
    }
    object Score {
        val ELIMINATION = Sound.sound(Key.key("burb.generic.vanquish"), Sound.Source.VOICE, 1f, 1f)
    }
    object Alert {
        const val GENERAL_ALERT = "mcc.game.map_alert"
        const val GENERAL_UPDATE = "mcc.game.map_update"
        const val OVERTIME_ALERT = "block.portal.travel"
    }
    object Tutorial {
        const val TUTORIAL_POP = "entity.item.pickup"
    }
    object Misc {
        val ADMIN_MESSAGE = Sound.sound(Key.key("ui.button.click"), Sound.Source.MASTER, 1f, 2f)
    }
    object Weapon {
        val FOOT_SOLDIER_WEAPON_FIRE = Sound.sound(Key.key("burb.weapon.foot_soldier.fire"), Sound.Source.VOICE, 0.75f, 1f)
    }
}