package dev.byrt.burb.messenger

import dev.byrt.burb.logger
import dev.byrt.burb.util.Noxesium
import dev.byrt.burb.util.NoxesiumChannel

import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener

class NoxesiumMessenger : PluginMessageListener {
    override fun onPluginMessageReceived(channel : String, player : Player, message : ByteArray) {
        val protocolVersion = parseIntByteArray(player, message)
        logger.info("Channel: $channel | Player: ${player.name} | Message: $protocolVersion")

        if(channel == NoxesiumChannel.NOXESIUM_V1_CLIENT_INFORMATION_CHANNEL.channel || channel == NoxesiumChannel.NOXESIUM_V2_CLIENT_INFORMATION_CHANNEL.channel) {
            Noxesium.addNoxesiumUser(player, protocolVersion)
        }
    }

    private fun parseIntByteArray(player: Player, bytes : ByteArray) : Int {
        // Could quite possibly be destroyed if Daniel changes how information is sent from Noxesium.
        try {
            logger.info("[NOX] ByteArray passed and read for player ${player.name}.")
            return bytes.inputStream().read()
        } catch(e: Exception) {
            logger.info("[NOX] Failed to read ByteArray for player ${player.name}, disabling Noxesium features for this user.")
            return -1
        }
    }
}