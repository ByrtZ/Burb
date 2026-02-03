package dev.byrt.burb.library

import dev.byrt.burb.plugin
import dev.byrt.burb.text.ChatUtility.BURB_FONT_TAG

import org.bukkit.Bukkit

object Translation {
    object Generic {
        const val ARROW_PREFIX = "[<yellow>▶<reset>] "
        const val ITEM_RECEIVED_PREFIX = "<burbcolour>(\uD83D\uDCB0) "
        const val TITLE_SCREEN_ACTIONBAR = "${BURB_FONT_TAG}<white>Press <burbcolour><key:key.sneak></burbcolour> <white>to join the fight."
        const val CHARACTER_SELECTION_ACTIONBAR = "${BURB_FONT_TAG}PRESS <burbcolour><key:key.sneak></burbcolour> to change character."
        const val DEATH_PREFIX = "<gray>[<#ff3333><unicodeprefix:skull><gray>]<reset> "
    }
    object Tutorial {
        const val BLANK_LINE = "<newline>"
        const val STANDBY = "<red><italic>Standby for the game to begin...<reset>"
    }
    object Teams {
        const val JOIN_TEAM = "You are now on team %d%s<reset>."
        const val LEAVE_TEAM = "You are no longer on team %d%s<reset.>"
        const val JOIN_SPECTATOR = "You are now a <speccolour>Spectator<reset>."
        const val LEAVE_SPECTATOR = "You are no longer a <speccolour>Spectator<reset>."
    }
    object Overtime {
        const val OVERTIME_PREFIX = "<#ff3333><bold>OVERTIME: "
        const val OVERTIME_REASON = "burb.game.state.overtime.prefix"
    }
    object Weapon {
        const val GATLING_CONTROLS = "${BURB_FONT_TAG}HOLD <burbcolour><key:key.sneak></burbcolour> to <red>fire <dark_gray>- <gray>[<yellow>%s<dark_gray>/<yellow>100<gray>]</gray> <dark_gray>- </dark_gray>Deselect <burbcolour>Pea Gatling</burbcolour> to <red>CANCEL</red>.<reset>"
        const val SUNBEAM_CONTROLS = "${BURB_FONT_TAG}HOLD <burbcolour><key:key.sneak></burbcolour> to <red>fire <dark_gray>- <gray>[<yellow>%s<dark_gray>/<yellow>50<gray>]</gray> <dark_gray>- </dark_gray>Deselect <burbcolour>Sunbeam</burbcolour> to <red>CANCEL</red>.<reset>"
        const val CANNON_RODEO_CONTROLS = "${BURB_FONT_TAG}HOLD <burbcolour><key:key.sneak></burbcolour> to <red>fire <dark_gray>- <gray>[<yellow>%s<dark_gray>/<yellow>12<gray>]</gray> <dark_gray>- </dark_gray>Deselect <burbcolour>Cannon Rodeo</burbcolour> to <red>CANCEL</red>.<reset>"
    }
    object TabList {
        const val SERVER_LIST_PADDING = "<plantscolour>■<zombiescolour>■<plantscolour>■<zombiescolour>■<plantscolour>■<zombiescolour>■<plantscolour>■<zombiescolour>■<plantscolour>■<zombiescolour>■<plantscolour>■<zombiescolour>■<plantscolour>■<zombiescolour>■<plantscolour>■"
        const val SERVER_LIST_TITLE = " <burbcolour><bold>SUBURBIA<reset> "
        val SERVER_LIST_VERSION = "<dark_gray>${Bukkit.getMinecraftVersion()}<reset>"
        const val SERVER_LIST_GAME = "<white> ● <yellow>The zombies are coming.<reset>"
        val SERVER_LIST_EXTRA = "<white> ● <dark_gray>${plugin.pluginMeta.displayName}<reset>"
    }
}