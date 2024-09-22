package dev.byrt.burb.data

import java.util.*

data class BurbPlayerData(val id : UUID, var playerType : PlayerType)

class BurbPlayer {

}

enum class PlayerType {
    SPECTATOR,
    PARTICIPANT
}
