package dev.byrt.burb.event

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.FoodLevelChangeEvent

class HungerEvent: Listener {
    @EventHandler
    private fun onHungerChange(e: FoodLevelChangeEvent) {
        e.entity.foodLevel = 20
        e.isCancelled = true
    }
}