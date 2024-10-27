package dev.byrt.burb.library

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound

object Sounds {
    object Music {
        val GAME_MUSIC = Sound.sound(Key.key(""), Sound.Source.VOICE, 1f, 1f)
        val OVERTIME_MUSIC = Sound.sound(Key.key("burb.game.overtime.1"), Sound.Source.VOICE, 0.75f, 1f)
        const val GAME_STARTING_MUSIC = ""
        const val GAME_OVER_MUSIC = ""
        const val ROUND_OVER_MUSIC = ""
        val LOBBY_INTRO = Sound.sound(Key.key("burb.lobby.intro"), Sound.Source.VOICE, 0.75f, 1f)
        val LOBBY_WAITING = Sound.sound(Key.key("burb.lobby.waiting"), Sound.Source.VOICE, 0.75f, 1f)
        val GAME_INTRO_JINGLE = Sound.sound(Key.key("burb.game.intro_jingle"), Sound.Source.VOICE, 1f, 1f)
        val SUBURBINATION_PLANTS = Sound.sound(Key.key("burb.game.suburbination.plants"), Sound.Source.VOICE, 0.5f, 1f)
        val SUBURBINATION_ZOMBIES = Sound.sound(Key.key("burb.game.suburbination.zombies"), Sound.Source.VOICE, 0.5f, 1f)
        val DOWNTIME_LOOP = Sound.sound(Key.key("event.downtime.loop"), Sound.Source.VOICE, 1f, 1f)
        val DOWNTIME_SUSPENSE = Sound.sound(Key.key("event.downtime.suspense"), Sound.Source.VOICE, 1f, 1f)
        val NULL = Sound.sound(Key.key(""), Sound.Source.VOICE, 0f, 0f)
    }
    object Timer {
        val STARTING_123 = Sound.sound(Key.key("block.note_block.pling"), Sound.Source.VOICE, 1f, 1f)
        const val STARTING_GO = ""
        val CLOCK_TICK = Sound.sound(Key.key("block.note_block.bass"), Sound.Source.VOICE, 1f, 1f)
        val CLOCK_TICK_HIGH = Sound.sound(Key.key("block.note_block.bass"), Sound.Source.VOICE, 1f, 2f)
    }
    object Round {
        val ROUND_END = Sound.sound(Key.key("block.respawn_anchor.deplete"), Sound.Source.VOICE, 1f, 1f)
        val GAME_OVER = Sound.sound(Key.key("ui.toast.challenge_complete"), Sound.Source.VOICE, 0.75f, 1f)
    }
    object Start {
        const val START_GAME_SUCCESS = "block.respawn_anchor.set_spawn"
        const val START_GAME_FAIL = "entity.enderman.teleport"
    }
    object Score {
        val ELIMINATION = Sound.sound(Key.key("burb.generic.vanquish"), Sound.Source.VOICE, 1f, 1f)
        val DEATH = Sound.sound(Key.key("item.trident.thunder"), Sound.Source.VOICE, 1f, 1.25f)
        val RESPAWN = Sound.sound(Key.key("block.bubble_column.upwards_inside"), Sound.Source.VOICE, 0.75f, 0.0f)
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
        val ADMIN_MESSAGE = Sound.sound(Key.key("ui.button.click"), Sound.Source.MASTER, 0.5f, 2f)
    }
    object Weapon {
        val FOOT_SOLDIER_WEAPON_FIRE = Sound.sound(Key.key("burb.weapon.foot_soldier.fire"), Sound.Source.VOICE, 0.75f, 1f)
    }
}