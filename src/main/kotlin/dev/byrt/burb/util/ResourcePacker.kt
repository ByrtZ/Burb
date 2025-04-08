package dev.byrt.burb.util

import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.logger

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

import net.kyori.adventure.resource.ResourcePackInfo
import net.kyori.adventure.resource.ResourcePackRequest

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

import java.net.URI
import java.security.MessageDigest
import java.util.*

object ResourcePacker {
    private const val PACK_URL = "https://github.com/ByrtZ/BurbResourcePack/releases/latest/download/Burb.zip"
    fun applyPackPlayer(player: Player) = runBlocking {
        if(GameManager.getGameState() == GameState.IDLE) {
            player.teleport(Location(Bukkit.getWorlds()[0], 0.5, -1000.0, 0.5))
            player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, PotionEffect.INFINITE_DURATION, 0))
        }
        launch {
            val latestPack = getLatestPack()
            player.sendResourcePacks(ResourcePackRequest.resourcePackRequest().packs({ ResourcePackInfo.resourcePackInfo(UUID.randomUUID(), URI(latestPack.first), latestPack.second)}))
        }
    }

    fun removePackPlayer(player: Player) {
        player.removeResourcePacks()
        player.clearResourcePacks()
    }

    private suspend fun getLatestPack(): Pair<String, String> {
        try {
            val bytes = fetch(PACK_URL)
            val hash = hash(bytes)
            return Pair(PACK_URL, hash)
        } catch(e: Exception) {
            logger.severe("Failed to fetch file or generate SHA-1 hash\nStack Trace:\n${e.stackTrace}\nMessage:\n${e.message}")
            return Pair("null", "null")
        }
    }

    private suspend fun fetch(url: String): ByteArray {
        val client = HttpClient(CIO)
        return client.use { clt ->
            val response: HttpResponse = clt.get(url)
            response.readBytes()
        }
    }

    private fun hash(data: ByteArray): String {
        val messageDigest = MessageDigest.getInstance("SHA-1")
        val hashBytes = messageDigest.digest(data)
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}