package dev.byrt.burb.game

import dev.byrt.burb.logger
import dev.byrt.burb.text.ChatUtility

object Rounds {
    private var round = Round.ONE
    private var totalRounds = 1

    fun nextRound() {
        when(round) {
            Round.ONE -> { setRound(Round.TWO) }
            Round.TWO -> { setRound(Round.THREE) }
            Round.THREE -> { logger.warning("Attempted to increment past round 3.") }
        }
    }

    fun setRound(newRound : Round) {
        if (newRound == round) return
        ChatUtility.broadcastDev(
            "<dark_gray>Round Updated: <red>$round<reset> <aqua>-> <green>$newRound<dark_gray>.",
            true
        )
        this.round = newRound
    }

    fun getRound() : Round {
        return round
    }

    fun setTotalRounds(newRounds: Int) {
        this.totalRounds = newRounds
    }

    fun getTotalRounds() : Int {
        return totalRounds
    }
}

enum class Round {
    ONE,
    TWO,
    THREE
}