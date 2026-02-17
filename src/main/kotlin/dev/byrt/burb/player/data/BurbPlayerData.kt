package dev.byrt.burb.player.data

import dev.byrt.burb.logger
import dev.byrt.burb.player.progression.BurbLevel
import dev.byrt.burb.plugin
import dev.byrt.burb.text.ChatUtility
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.util.*

object BurbPlayerData {
    const val EXPERIENCE_PATH = ".experience"
    const val LEVEL_PATH = ".level"
    private val playerData = mutableMapOf<UUID, FileConfiguration>()

    fun getPlayerData(player: Player) {
        try {
            val folder = plugin.dataFolder
            if(!folder.exists()) folder.mkdirs()
            val playerFile = File(folder,"${player.uniqueId}.yml")
            if(!playerFile.exists()) playerFile.createNewFile()
            val fileConfiguration = YamlConfiguration.loadConfiguration(playerFile)
            if(fileConfiguration.get("${player.uniqueId}${EXPERIENCE_PATH}") == null) {
                fileConfiguration.set("${player.uniqueId}${EXPERIENCE_PATH}", 0)
            }
            if(fileConfiguration.get("${player.uniqueId}${LEVEL_PATH}") == null) {
                fileConfiguration.set("${player.uniqueId}${LEVEL_PATH}", BurbLevel.LEVEL_1.name)
            }
            val level = BurbLevel.valueOf(fileConfiguration.get("${player.uniqueId}${LEVEL_PATH}").toString()).ordinal + 1
            val exp = fileConfiguration.get("${player.uniqueId}${EXPERIENCE_PATH}") as Int / BurbLevel.valueOf(fileConfiguration.get("${player.uniqueId}${LEVEL_PATH}").toString()).requiredXp.toFloat()
            player.level = level
            player.exp = exp
            fileConfiguration.save(playerFile)
            if(playerData.containsKey(player.uniqueId)) playerData.remove(player.uniqueId)
            playerData[player.uniqueId] = fileConfiguration
            logger.info("Player data fetched for player ${player.name} (${player.uniqueId})")
        } catch(e: Exception) {
            ChatUtility.broadcastDev("<#ff3333>Something went wrong while trying to fetch player data for player ${player.name} (${player.uniqueId}).", false)
            logger.warning("Something went wrong while trying to fetch player data for player ${player.name} (${player.uniqueId}).")
            e.printStackTrace()
        }
    }

    fun getPlayerConfiguration(player: Player): FileConfiguration {
        return playerData[player.uniqueId]!!
    }
}