package dev.byrt.burb.event

import dev.byrt.burb.library.Sounds
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class InteractEvent: Listener {
    @EventHandler
    private fun onInteract(e: PlayerInteractEvent) {
        if(e.player.inventory.itemInMainHand.type == Material.GOLDEN_SWORD && e.action.isRightClick && !e.player.hasCooldown(Material.GOLDEN_SWORD)) {
            val arrow = e.player.world.spawn(e.player.eyeLocation, Arrow::class.java)
            arrow.shooter = e.player
            val arrowVelocity = e.player.location.direction.multiply(2.25)
            arrow.velocity = arrowVelocity
            e.player.world.playSound(Sounds.Weapon.FOOT_SOLDIER_WEAPON_FIRE)
        }
    }
}