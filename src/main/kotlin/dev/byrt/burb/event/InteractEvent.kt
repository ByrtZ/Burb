package dev.byrt.burb.event

import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.item.ItemUsage

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.data.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

@Suppress("unused")
class InteractEvent: Listener {
    @EventHandler
    private fun onInteract(e: PlayerInteractEvent) {
        if(e.player.vehicle != null) {
            if(e.player.vehicle?.scoreboardTags?.contains("${e.player.uniqueId}-death-vehicle") == true) {
                e.isCancelled = true
            }
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
                if(e.player.inventory.itemInMainHand.type in listOf(Material.YELLOW_DYE, Material.ORANGE_DYE, Material.RED_DYE) && e.action.isRightClick && !e.player.hasCooldown(e.player.inventory.itemInMainHand.type)) {
                    ItemUsage.useAbility(e.player, e.player.inventory.itemInMainHand)
                }
            }
            if(e.player.gameMode != GameMode.CREATIVE) {
                if(e.action.isRightClick
                    && e.clickedBlock?.blockData is Openable
                    || e.clickedBlock?.blockData is Directional
                    || e.clickedBlock?.blockData is Orientable
                    || e.clickedBlock?.blockData is Rotatable
                    || e.clickedBlock?.blockData is Powerable
                    || e.clickedBlock?.type == Material.FLOWER_POT
                    || e.clickedBlock?.type == Material.BEACON
                    || e.clickedBlock?.type?.name?.startsWith("POTTED_") == true) {
                    e.isCancelled = true
                }
            }
        }
    }
}