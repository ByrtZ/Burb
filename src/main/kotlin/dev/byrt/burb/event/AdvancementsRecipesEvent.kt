package dev.byrt.burb.event

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRecipeDiscoverEvent
import org.bukkit.event.player.PlayerStatisticIncrementEvent

@Suppress("unused")
class AdvancementsRecipesEvent : Listener {
    @EventHandler
    private fun onRecipeUnlock(e: PlayerRecipeDiscoverEvent) {
        e.player.discoveredRecipes.clear()
        e.isCancelled = true
    }

    @EventHandler
    private fun onAdvancementGain(e: PlayerAdvancementCriterionGrantEvent) {
        e.isCancelled = true
    }

    @EventHandler
    private fun onStatisticIncrement(e: PlayerStatisticIncrementEvent) {
        e.isCancelled = true
    }
}