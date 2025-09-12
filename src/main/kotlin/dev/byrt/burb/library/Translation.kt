package dev.byrt.burb.library

import dev.byrt.burb.text.ChatUtility.BURB_FONT_TAG

import org.bukkit.Bukkit

object Translation {
    object Generic {
        const val ARROW_PREFIX = "[<yellow>▶<reset>] "
        const val TITLE_SCREEN_ACTIONBAR = "<reset>${BURB_FONT_TAG}PRESS<reset> <burbcolour>$BURB_FONT_TAG<key:key.sneak><reset> ${BURB_FONT_TAG}TO<reset> ${BURB_FONT_TAG}JOIN<reset> ${BURB_FONT_TAG}THE<reset> ${BURB_FONT_TAG}FIGHT<reset>"
        const val CHARACTER_SELECTION_ACTIONBAR = "<reset>${BURB_FONT_TAG}PRESS<reset> <burbcolour>$BURB_FONT_TAG<key:key.sneak><reset> ${BURB_FONT_TAG}TO<reset> ${BURB_FONT_TAG}CHANGE<reset> ${BURB_FONT_TAG}CHARACTER<reset>"
    }
    object Tutorial {
        const val BLANK_LINE = "<newline>"
        const val STANDBY = "<red><italic>Standby for the game to begin...<reset>"
    }
    object Teams {
        const val JOIN_TEAM = "You are now on team %d%s<reset>."
        const val LEAVE_TEAM = "You are no longer on team %d%s<reset.>"
    }
    object Overtime {
        const val OVERTIME_PREFIX = "<red><bold>OVERTIME: "
        const val OVERTIME_REASON = "burb.game.state.overtime.prefix"
    }
    object Weapon {
        const val GATLING_CONTROLS = "${BURB_FONT_TAG}HOLD<reset> <burbcolour>$BURB_FONT_TAG<key:key.sneak><reset> ${BURB_FONT_TAG}TO<reset> ${BURB_FONT_TAG}<red>FIRE<reset>${BURB_FONT_TAG}<reset> <dark_gray>- <gray>[<yellow>%s<dark_gray>/<yellow>100<gray>] <dark_gray>- <reset>${BURB_FONT_TAG}DESELECT<reset> <burbcolour>${BURB_FONT_TAG}GATLING  CANNON<reset> ${BURB_FONT_TAG}TO<reset> ${BURB_FONT_TAG}<red>CANCEL<reset>${BURB_FONT_TAG}.<reset>"
    }
    object TabList {
        const val SERVER_LIST_PADDING = "<dark_red>■<red>■<dark_red>■<red>■<dark_red>■<red>■<dark_red>■<red>■<dark_red>■<red>■<dark_red>■<red>■<dark_red>■"
        const val SERVER_LIST_TITLE = " <gold><bold>Byrt's Server<reset> "
        val SERVER_LIST_VERSION = "<dark_gray>${Bukkit.getMinecraftVersion()}<reset>"
        const val SERVER_LIST_GAME = "<white> ● <burbcolour><bold>???<reset>"
        const val SERVER_LIST_EXTRA = "<white> ● <yellow>Coming soon...<reset>"
    }
}