package dev.byrt.burb.util

import org.bukkit.entity.Player

import java.util.HashMap
import java.util.UUID

object Cooldowns {
    private var npcInteractionCooldowns = HashMap<UUID, Long>()
    private const val NPC_INTERACTION_COOLDOWN_TIME = 1250

    fun attemptNpcInteraction(player: Player): Boolean {
        return if(!npcInteractionCooldowns.containsKey(player.uniqueId) || System.currentTimeMillis() - npcInteractionCooldowns[player.uniqueId]!! > NPC_INTERACTION_COOLDOWN_TIME) {
            npcInteractionCooldowns[player.uniqueId] = System.currentTimeMillis()
            true
        } else {
            false
        }
    }
}