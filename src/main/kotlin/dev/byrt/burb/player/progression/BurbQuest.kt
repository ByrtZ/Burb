package dev.byrt.burb.player.progression

//TODO: Questing; low priority
enum class BurbQuest(questName: String, requiredStat: Int, xpReward: Int) {
    VANQUISH_PLANTS_5("Vanquish 5 Plants", 5, 75),
    VANQUISH_ZOMBIES_5("Vanquish 5 Zombies", 5, 75),
    USE_ABILITY_10("Use Abilities 10 times", 10, 25),
    WIN_GAMES_1("Win 1 game of Suburbination", 1, 150)
}