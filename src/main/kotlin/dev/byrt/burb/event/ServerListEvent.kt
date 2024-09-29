package dev.byrt.burb.event

import com.destroystokyo.paper.event.server.PaperServerListPingEvent

import dev.byrt.burb.chat.ChatUtility
import dev.byrt.burb.library.Translation

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ServerListEvent: Listener {
    @EventHandler
    private fun onServerPing(e: PaperServerListPingEvent) {
        e.version = "Byrtrium v1.21.x"
        e.motd(ChatUtility.formatMessage("${Translation.TabList.SERVER_LIST_PADDING}${Translation.TabList.SERVER_LIST_TITLE}${Translation.TabList.SERVER_LIST_PADDING}<newline>${Translation.TabList.SERVER_LIST_VERSION}${Translation.TabList.SERVER_LIST_GAME}${Translation.TabList.SERVER_LIST_EXTRA}", false))
    }
}