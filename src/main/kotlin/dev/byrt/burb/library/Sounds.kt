package dev.byrt.burb.library

import net.kyori.adventure.key.Key.key
import net.kyori.adventure.sound.Sound.Source
import net.kyori.adventure.sound.Sound.sound

object Sounds {
    object Music {
        val OVERTIME_MUSIC = sound(key("burb.game.overtime"), Source.VOICE, 0.75f, 1f)
        val POST_GAME_MUSIC = sound(key("burb.game.post_game.1"), Source.VOICE, 0.75f, 1f)
        val LOBBY_TITLE_SCREEN = sound(key("burb.lobby.title_screen"), Source.VOICE, 0.35f, 1f)
        val LOBBY_INTRO = sound(key("burb.lobby.intro"), Source.VOICE, 0.4f, 1f)
        val LOBBY_WAITING = sound(key("burb.lobby.waiting"), Source.VOICE, 0.75f, 1f)
        val LOBBY_UNDERWORLD = sound(key("burb.lobby.underworld"), Source.VOICE, 0.5f, 1f)
        val LOBBY_UNDERWORLD_CHALLENGE_INTRO = sound(key("burb.lobby.underworld.challenge_intro"), Source.VOICE, 0.5f, 1f)
        val LOBBY_UNDERWORLD_CHALLENGE_LOW = sound(key("burb.lobby.underworld.challenge.0"), Source.VOICE, 0.5f, 1f)
        val LOBBY_UNDERWORLD_CHALLENGE_MEDIUM = sound(key("burb.lobby.underworld.challenge.1"), Source.VOICE, 0.5f, 1f)
        val LOBBY_UNDERWORLD_CHALLENGE_HIGH = sound(key("burb.lobby.underworld.challenge.2"), Source.VOICE, 0.5f, 1f)
        val LOBBY_UNDERWORLD_CHALLENGE_INTENSE = sound(key("burb.lobby.underworld.challenge.3"), Source.VOICE, 0.5f, 1f)
        val LOADING_MELODY = sound(key("burb.game.loading_melody"), Source.VOICE, 0.75f, 1f)
        val GAME_INTRO_JINGLE = sound(key("burb.game.intro_jingle"), Source.VOICE, 1f, 1f)
        val SUBURBINATION_PLANTS = sound(key("burb.game.suburbination.plants"), Source.VOICE, 0.75f, 1f)
        val SUBURBINATION_ZOMBIES = sound(key("burb.game.suburbination.zombies"), Source.VOICE, 0.75f, 1f)
        val RANDOM_LOW = sound(key("burb.music.low.random"), Source.VOICE, 0.4f, 1f)
        val RANDOM_MEDIUM = sound(key("burb.music.medium.random"), Source.VOICE, 0.4f, 1f)
        val RANDOM_HIGH = sound(key("burb.music.high.random"), Source.VOICE, 0.4f, 1f)
        val TREASURE_TIME_INTRO = sound(key("burb.game.event.treasure_intro"), Source.VOICE, 0.5f, 1f)
        val TREASURE_TIME_LOW = sound(key("burb.game.event.treasure_low"), Source.VOICE, 0.5f, 1f)
        val TREASURE_TIME_HIGH = sound(key("burb.game.event.treasure_high"), Source.VOICE, 0.5f, 1f)
        val RANDOS_REVENGE = sound(key("burb.game.event.randos_revenge"), Source.VOICE, 0.5f, 1f)
        val VANQUISH_SHOWDOWN = sound(key("burb.game.event.vanquish_showdown"), Source.VOICE, 0.3f, 1f)
        val DOWNTIME_LOOP = sound(key("event.downtime.loop"), Source.VOICE, 1f, 1f)
        val DOWNTIME_SUSPENSE = sound(key("event.downtime.suspense"), Source.VOICE, 1f, 1f)
        val NULL = sound(key(""), Source.VOICE, 0f, 0f)
    }
    object Timer {
        val STARTING_123 = sound(key("burb.generic.tick"), Source.VOICE, 1.25f, 1f)
        val STARTING_GO = sound(key("block.end_portal.spawn"), Source.VOICE, 0.75f, 1f)
        val TICK = sound(key("burb.generic.tick"), Source.VOICE, 1.25f, 1f)
        val CLOCK_TICK = sound(key("burb.generic.clock_tick"), Source.VOICE, 1.25f, 1f)
        val CLOCK_TICK_HIGH = sound(key("burb.generic.clock_tick"), Source.VOICE, 1.25f, 1.25f)
    }
    object Round {
        val ROUND_END = sound(key("block.respawn_anchor.deplete"), Source.VOICE, 1f, 1f)
        val GAME_OVER = sound(key("ui.toast.challenge_complete"), Source.VOICE, 0.75f, 1f)
    }
    object Score {
        val ELIMINATION = sound(key("burb.generic.vanquish"), Source.VOICE, 1f, 1f)
        val CAPTURE_FRIENDLY = sound(key("burb.game.capture.friendly"), Source.VOICE, 1.25f, 1f)
        val CAPTURE_UNFRIENDLY = sound(key("burb.game.capture.unfriendly"), Source.VOICE, 1.25f, 1f)
        val DEATH = sound(key("burb.game.death"), Source.VOICE, 1f, 1f)
        val DEATH_STATS = sound(key("burb.game.death_stats"), Source.VOICE, 0.75f, 1f)
        val TEAM_WIPE = sound(key("burb.game.event.vanquish_showdown.team_wipe"), Source.VOICE, 0.5f, 1f)
        val RESPAWN = sound(key("block.bubble_column.upwards_inside"), Source.VOICE, 2f, 0.0f)
        val VANQUISH_SHOWDOWN_RESPAWN = sound(key("burb.game.event.vanquish_showdown.respawn"), Source.VOICE, 0.5f, 1f)
        val VANQUISH_SHOWDOWN_POST_RESPAWN = sound(key("burb.game.event.vanquish_showdown.post_respawn"), Source.VOICE, 0.75f, 1f)
        val PLANTS_WIN_MUSIC = sound(key("burb.game.win_generic.plants"), Source.VOICE, 0.5f, 1f)
        val PLANTS_WIN = sound(key("burb.game.win.plants"), Source.VOICE, 0.75f, 1f)
        val PLANTS_LOSE = sound(key("burb.game.lose.plants"), Source.VOICE, 0.75f, 1f)
        val ZOMBIES_WIN_MUSIC = sound(key("burb.game.win_generic.zombies"), Source.VOICE, 0.5f, 1f)
        val ZOMBIES_WIN = sound(key("burb.game.win.zombies"), Source.VOICE, 0.75f, 1f)
        val ZOMBIES_LOSE = sound(key("burb.game.lose.zombies"), Source.VOICE, 0.75f, 1f)
    }
    object Alert {
        val ALARM = sound(key("burb.generic.alarm"), Source.VOICE, 1.25f, 1f)
    }
    object Tutorial {
        const val TUTORIAL_POP = "entity.item.pickup"
    }
    object Misc {
        val ADMIN_MESSAGE = sound(key("ui.button.click"), Source.MASTER, 0.5f, 2f)
        val INTERFACE_INTERACT = sound(key("block.vault.insert_item"), Source.MASTER, 1f, 1f)
        val INTERFACE_ENTER_SUB_MENU = sound(key("block.vault.activate"), Source.MASTER, 1f, 1f)
        val INTERFACE_BACK = sound(key("block.vault.deactivate"), Source.MASTER, 1f, 1f)
        val INTERFACE_ERROR = sound(key("block.vault.reject_rewarded_player"), Source.MASTER, 1f, 1f)
        val TITLE_SCREEN_ENTER = sound(key("entity.breeze.shoot"), Source.MASTER, 1f, 0.75f)
        val ODE_TO_JOY = sound(key("burb.generic.ode_to_joy"), Source.VOICE, 0.75f, 1f)
        val SUCCESS = sound(key("burb.generic.success"), Source.VOICE, 0.75f, 1f)
        val LEVEL_UP_PLANTS = sound(key("burb.generic.level_up.plants"), Source.VOICE, 1f, 1f)
        val LEVEL_UP_ZOMBIES = sound(key("burb.generic.level_up.zombies"), Source.VOICE, 1f, 1f)
        val RANDO_NEW_CHARACTER = sound(key("burb.lobby.shop.pack_jingle"), Source.VOICE, 0.5f, 1f)
        val NPC_INTERACT = sound(key("entity.villager.trade"), Source.VOICE, 1f, 1f)
        val NPC_INTERACT_HIT = sound(key("entity.villager.hurt"), Source.VOICE, 1f, 1.25f)
        val NPC_ALT_INTERACT = sound(key("entity.pillager.ambient"), Source.VOICE, 0.75f, 2f)
        val NPC_ALT_INTERACT_HIT = sound(key("entity.pillager.hurt"), Source.VOICE, 0.75f, 2f)
        val NPC_DEEP_INTERACT = sound(key("entity.illusioner.ambient"), Source.VOICE, 0.75f, 0.75f)
        val NPC_DEEP_INTERACT_HIT = sound(key("entity.illusioner.hurt"), Source.VOICE, 0.75f, 0.5f)
        val COMPLETE = sound(key("burb.generic.complete"), Source.VOICE, 0.75f, 1f)
        val TENSE = sound(key("burb.generic.tense"), Source.VOICE, 0.5f, 1f)
    }
    object Weapon {
        const val RELOAD_TICK = "block.note_block.hat"
        val RELOAD_SUCCESS = sound(key("block.note_block.pling"), Source.VOICE, 1f, 1.5f)
        val ABILITY_COMBO_LEFT = sound(key("block.stone_pressure_plate.click_off"), Source.VOICE, 1f, 1.25f)
        val ABILITY_COMBO_RIGHT = sound(key("block.stone_pressure_plate.click_off"), Source.VOICE, 1f, 1.75f)
        val ABILITY_COMBO_CAST = sound(key("entity.experience_orb.pickup"), Source.MASTER, 0.75f, 0.5f)
    }
    object Fishing {
        val FISH_FLOP = sound(key("entity.cod.flop"), Source.VOICE, 1f, 1f)
        val EPIC_CATCH = sound(key("entity.wither.spawn"), Source.VOICE, 0.5f, 1.25f)
        val LEGENDARY_CATCH = sound(key("entity.ender_dragon.death"), Source.VOICE, 0.15f, 2f)
        val LEGENDARY_CATCH_EXPLODE = sound(key("entity.generic.explode"), Source.VOICE, 0.5f, 1f)
        val MYTHIC_CATCH = sound(key("block.portal.travel"), Source.VOICE, 0.5f, 2f)
        val UNREAL_CATCH = sound(key("ambient.cave"), Source.VOICE, 10f, 2f)
        val UNREAL_CATCH_SPAWN = sound(key("entity.warden.sonic_boom"), Source.VOICE, 2f, 2f)
        val UNREAL_CATCH_SPAWN_BATS = sound(key("entity.warden.death"), Source.VOICE, 2f, 1f)
        val TRANSCENDENT_CATCH = sound(key("entity.blaze.ambient"), Source.VOICE, 2f, 0.75f)
        val TRANSCENDENT_CATCH_SPAWN = sound(key("entity.elder_guardian.curse"), Source.VOICE, 1.5f, 0.5f)
        val CELESTIAL_CATCH = sound(key("item.totem.use"), Source.VOICE, 2f, 0.75f)
        val CELESTIAL_CATCH_SPAWN = sound(key("item.trident.thunder"), Source.VOICE, 5f, 1.25f)
        val SHINY_CATCH = sound(key("block.amethyst_cluster.step"), Source.VOICE, 2f, 2f)
        val SHADOW_CATCH = sound(key("entity.wither.ambient"), Source.VOICE, 0.5f, 0f)
        val OBFUSCATED_CATCH = sound(key("entity.shulker.ambient"), Source.VOICE, 1.25f, 0.75f)
    }
}