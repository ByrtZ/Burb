package dev.byrt.burb.game

import dev.byrt.burb.game.events.SpecialEvent
import dev.byrt.burb.game.events.SpecialEvents
import dev.byrt.burb.team.BurbTeam
import dev.byrt.burb.text.InfoBoardManager

object Scores {
    private var plantsScore = 0
    private var zombiesScore = 0
    const val WIN_SCORE = 100000

    fun getWinningTeam(): BurbTeam? = when {
        plantsScore > zombiesScore -> BurbTeam.PLANTS
        zombiesScore > plantsScore -> BurbTeam.ZOMBIES
        else -> null
    }

    private fun teamScoreWinCheck() {
        if ((plantsScore >= WIN_SCORE || zombiesScore >= WIN_SCORE) && (GameManager.getGameState() == GameState.IN_GAME || GameManager.getGameState() == GameState.OVERTIME)) {
            GameManager.nextState()
        }
    }

    fun getPlacementMap(): Map<BurbTeam, Int> {
        return mutableMapOf(Pair(BurbTeam.PLANTS, plantsScore), Pair(BurbTeam.ZOMBIES, zombiesScore)).toList().sortedBy { (_, scores) -> scores }.reversed().toMap()
    }

    fun addScore(team: BurbTeam, score: Int) {
        if (team == BurbTeam.PLANTS) this.plantsScore += score * if(SpecialEvents.getCurrentEvent() == SpecialEvent.TREASURE_TIME) 2 else 1
        if (team == BurbTeam.ZOMBIES) this.zombiesScore += score * if(SpecialEvents.getCurrentEvent() == SpecialEvent.TREASURE_TIME) 2 else 1
        InfoBoardManager.updateScore()
        teamScoreWinCheck()
    }

    fun setPlantsScore(score: Int) {
        this.plantsScore = score
        InfoBoardManager.updateScore()
        teamScoreWinCheck()
    }

    fun setZombiesScore(score: Int) {
        this.zombiesScore = score
        InfoBoardManager.updateScore()
        teamScoreWinCheck()
    }

    fun getDisplayScore(teams: BurbTeam): Int {
        return when(teams) {
            BurbTeam.PLANTS -> {
                plantsScore.floorDiv(1000)
            }
            BurbTeam.ZOMBIES -> {
                zombiesScore.floorDiv(1000)
            }
        }
    }

    fun getPlantsScore(): Int {
        return this.plantsScore
    }

    fun getZombiesScore(): Int {
        return this.zombiesScore
    }
}