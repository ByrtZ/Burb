package dev.byrt.burb.game.events

import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.game.Timer
import dev.byrt.burb.text.Formatting
import org.bukkit.Bukkit

object SpecialEvents {
    private var currentEvent: SpecialEvent? = null
    fun rollSpecialEvent() {
        if(currentEvent == null && GameManager.getGameState() == GameState.IN_GAME && Timer.getTimer() >= 4 * 60 && (0..9).random() == 0) {
            val randomEvent = SpecialEvent.entries.random()
            currentEvent = randomEvent
            
            Bukkit.broadcast(Formatting.allTags.deserialize("${randomEvent.eventName} starting!"))
            when(randomEvent) {
                SpecialEvent.BOOSTED_SCORE_AND_REWARDS -> TODO()
                SpecialEvent.LOW_GRAVITY -> TODO()
                SpecialEvent.RANDOM_CHARACTER -> TODO()
            }
        }
    }
}

enum class SpecialEvent(var eventName: String) {
    BOOSTED_SCORE_AND_REWARDS("Treasure Time"),
    LOW_GRAVITY("Moon Gravity"),
    RANDOM_CHARACTER("Rando's Revenge")
}