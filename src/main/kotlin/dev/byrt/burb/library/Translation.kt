package dev.byrt.burb.library

import dev.byrt.burb.chat.ChatUtility.BURB_FONT_TAG

object Translation {
    object Generic {
        const val ARROW_PREFIX = "[<yellow>▶<reset>] "
        const val TITLE_SCREEN_ACTIONBAR = "<reset>${BURB_FONT_TAG}PRESS<reset> <burbcolour>$BURB_FONT_TAG<key:key.sneak><reset> ${BURB_FONT_TAG}TO<reset> ${BURB_FONT_TAG}JOIN<reset> ${BURB_FONT_TAG}THE<reset> ${BURB_FONT_TAG}FIGHT<reset>"
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
    object TabList {
        const val SERVER_LIST_PADDING = "<dark_red>■<red>■<dark_red>■<red>■<dark_red>■<red>■<dark_red>■<red>■<dark_red>■<red>■<dark_red>■<red>■<dark_red>■"
        const val SERVER_LIST_TITLE = " <gold><bold>Byrt's Server<reset> "
        const val SERVER_LIST_VERSION = "<dark_gray>v1.0.0<reset>"
        const val SERVER_LIST_GAME = "<white> ● <burbcolour><bold>???<reset>"
        const val SERVER_LIST_EXTRA = "<white> ● <yellow>Coming soon...<reset>"
    }
}