package dev.byrt.burb.event

import dev.byrt.burb.interfaces.BurbInterface
import dev.byrt.burb.interfaces.BurbInterfaceType

import org.bukkit.entity.AreaEffectCloud
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDismountEvent

class DismountEvent: Listener {
    @EventHandler
    private fun onDismount(e: EntityDismountEvent) {
        if(e.entity is Player && e.dismounted is AreaEffectCloud) {
            val dismounted = e.dismounted as AreaEffectCloud
            val player = e.entity as Player
            if(dismounted.scoreboardTags.contains("${player.uniqueId}-title-vehicle") && !dismounted.scoreboardTags.contains("${player.uniqueId}-used")) {
                BurbInterface(player, BurbInterfaceType.TEAM_SELECT)
                dismounted.scoreboardTags.add("${player.uniqueId}-used")
            }
            e.isCancelled = true
        }
    }
}