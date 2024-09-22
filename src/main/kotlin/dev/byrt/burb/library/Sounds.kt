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
    object Queue {
        const val QUEUE_JOIN = "block.note_block.flute"
        const val QUEUE_LEAVE = "block.note_block.didgeridoo"
        const val QUEUE_FIND_GAME = "block.end_portal.spawn"
        const val QUEUE_TELEPORT = "block.portal.trigger"
        const val QUEUE_TICK = "block.note_block.pling"
    }
    object Score {
        const val ACQUIRED = ""
        const val BIG_ACQUIRED = ""
        const val UNDO_ELIMINATION = ""
        const val TEAM_ELIMINATED = ""
    }
    object Alert {
        const val GENERAL_ALERT = "mcc.game.map_alert"
        const val GENERAL_UPDATE = "mcc.game.map_update"
        const val OVERTIME_ALERT = "block.portal.travel"
    }
    object Tutorial {
        const val TUTORIAL_POP = "entity.item.pickup"
    }
    object Command {
        const val SHUFFLE_START = "block.note_block.flute"
        const val SHUFFLE_COMPLETE = "block.note_block.flute"
        const val SHUFFLE_FAIL = "block.note_block.didgeridoo"
        const val WHITELIST_START = "block.note_block.flute"
        const val WHITELIST_COMPLETE = "block.note_block.flute"
        const val WHITELIST_FAIL = "block.note_block.didgeridoo"
        const val PING = "entity.experience_orb.pickup"
        const val BUILDMODE_SUCCESS = "entity.mooshroom.convert"
        const val BUILDMODE_FAIL = "entity.enderman.teleport"
    }
    object Misc {
        val ADMIN_MESSAGE = Sound.sound(Key.key("ui.button.click"), Sound.Source.MASTER, 1f, 2f)
    }
}