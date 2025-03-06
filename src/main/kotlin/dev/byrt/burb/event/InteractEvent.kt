package dev.byrt.burb.event

import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.item.ItemUsage

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

@Suppress("unused")
class InteractEvent: Listener {
    @EventHandler
    private fun onInteract(e: PlayerInteractEvent) {
        if(e.player.vehicle != null) {
            e.isCancelled = true
        } else {
            if(GameManager.getGameState() in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
                if(e.player.inventory.itemInMainHand.type == Material.POPPED_CHORUS_FRUIT && e.action.isRightClick && !e.player.hasCooldown(Material.POPPED_CHORUS_FRUIT)) {
                    ItemUsage.useProjectileWeapon(e.player, e.player.inventory.itemInMainHand)
                }
                if(e.player.inventory.itemInMainHand.type == Material.POPPED_CHORUS_FRUIT && e.action.isLeftClick && !e.player.hasCooldown(Material.POPPED_CHORUS_FRUIT)) {
                    ItemUsage.reloadProjectileWeapon(e.player, e.player.inventory.itemInMainHand)
                }
                if(e.player.inventory.itemInMainHand.type == Material.WOODEN_SWORD && e.action.isLeftClick && !e.player.hasCooldown(Material.WOODEN_SWORD)) {
                    ItemUsage.useMeleeWeapon(e.player, e.player.inventory.itemInMainHand)
                }
                if(e.player.inventory.itemInMainHand.type == Material.DISC_FRAGMENT_5 && e.action.isRightClick && !e.player.hasCooldown(Material.DISC_FRAGMENT_5)) {
                    ItemUsage.useAbility(e.player, e.player.inventory.itemInMainHand)
                }
            }
        }
    }
}