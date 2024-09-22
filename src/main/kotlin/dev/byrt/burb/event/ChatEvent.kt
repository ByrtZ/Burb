package dev.byrt.burb.event

import dev.byrt.burb.chat.GlobalRenderer

import io.papermc.paper.event.player.AsyncChatEvent

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ChatEvent: Listener {
    @EventHandler
    fun onChat(e: AsyncChatEvent) {
        e.renderer(GlobalRenderer)
    }
}