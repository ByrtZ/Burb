package dev.byrt.burb.event

import dev.byrt.burb.library.Sounds

import org.bukkit.Material
import org.bukkit.entity.Snowball
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class InteractEvent: Listener {
    @EventHandler
    private fun onInteract(e: PlayerInteractEvent) {
        if(e.player.inventory.itemInMainHand.type == Material.GOLDEN_SWORD && e.action.isRightClick && !e.player.hasCooldown(Material.GOLDEN_SWORD)) {
            e.player.setCooldown(Material.GOLDEN_SWORD, 4)
            val snowball = e.player.world.spawn(e.player.eyeLocation, Snowball::class.java)
            snowball.shooter = e.player
            val snowballVelocity = e.player.location.direction.multiply(2.5)
            snowball.velocity = snowballVelocity
            e.player.playSound(Sounds.Weapon.FOOT_SOLDIER_WEAPON_FIRE)
        }
    }
}