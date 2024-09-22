package dev.byrt.burb.messenger

import dev.byrt.burb.chat.ChatUtility
import dev.byrt.burb.logger

import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener

import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*

class BrandMessenger : PluginMessageListener {
    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        try {
            val brand = String(message, Charset.defaultCharset()).substring(1)
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            val rawString = "<notifcolour>${player.name}<white> joined using <notifcolour>$brand<white>."
            if(brand.lowercase().trim() == "vanilla") {
                rawString.plus(" <dark_gray><i>(Could be un-named client).")
            }
            ChatUtility.broadcastDev(rawString, true)
            logger.info("(BRAND) ${player.name} joined using $brand.")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }
}