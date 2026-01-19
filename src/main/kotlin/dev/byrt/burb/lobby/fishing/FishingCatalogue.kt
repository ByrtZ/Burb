package dev.byrt.burb.lobby.fishing

import dev.byrt.burb.logger

import org.bukkit.Material
import org.bukkit.entity.Player

object FishingCatalogue {
    fun logFish(player: Player, material: Material, fishRarity: FishRarity) {
        logger.info("${player.name} caught $fishRarity ${material.name}")
        //existing amount caught
        //increment
        //save to config
    }

    fun getTotalCaught() {

    }

    fun getRarityCaught() {

    }
}