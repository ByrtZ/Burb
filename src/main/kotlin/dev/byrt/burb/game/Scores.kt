package dev.byrt.burb.game

import dev.byrt.burb.game.events.SpecialEvent
import dev.byrt.burb.game.events.SpecialEvents
import dev.byrt.burb.team.Teams
import dev.byrt.burb.text.InfoBoardManager

object Scores {
    private var plantsScore = 0
    private var zombiesScore = 0
    private const val WIN_SCORE = 200000

    fun getWinningTeam(): Teams {
        if (plantsScore > zombiesScore) return Teams.PLANTS
        if (zombiesScore > plantsScore) return Teams.ZOMBIES
        return Teams.NULL
    }

    private fun teamScoreWinCheck() {
        if ((plantsScore >= WIN_SCORE || zombiesScore >= WIN_SCORE) && (GameManager.getGameState() == GameState.IN_GAME || GameManager.getGameState() == GameState.OVERTIME)) {
            GameManager.nextState()
        }
    }

    fun getPlacementMap(): Map<Teams, Int> {
        return mutableMapOf(Pair(Teams.PLANTS, plantsScore), Pair(Teams.ZOMBIES, zombiesScore)).toList().sortedBy { (_, scores) -> scores }.reversed().toMap()
    }

    fun addScore(team: Teams, score: Int) {
        if (team == Teams.PLANTS) this.plantsScore += score * if(SpecialEvents.getCurrentEvent() == SpecialEvent.BOOSTED_SCORE_AND_REWARDS) 2 else 1
        if (team == Teams.ZOMBIES) this.zombiesScore += score * if(SpecialEvents.getCurrentEvent() == SpecialEvent.BOOSTED_SCORE_AND_REWARDS) 2 else 1
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

    fun getDisplayScore(teams: Teams): Int {
        return when(teams) {
            Teams.PLANTS -> {
                this.plantsScore.floorDiv(1000)
            }
            Teams.ZOMBIES -> {
                this.zombiesScore.floorDiv(1000)
            } else -> -1
        }
    }

    fun getPlantsScore(): Int {
        return this.plantsScore
    }

    fun getZombiesScore(): Int {
        return this.zombiesScore
    }
}