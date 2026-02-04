package dev.byrt.burb.event

import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.interfaces.BurbInterface
import dev.byrt.burb.interfaces.BurbInterfaceType
import dev.byrt.burb.item.ServerItem
import dev.byrt.burb.item.ability.BurbAbilities
import dev.byrt.burb.item.weapon.BurbWeapons
import dev.byrt.burb.lobby.npc.BurbNPC
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.util.Cooldowns
import dev.byrt.burb.util.Keys

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.data.*
import org.bukkit.entity.Mannequin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.persistence.PersistentDataType

import java.util.Locale.getDefault

@Suppress("unused")
class InteractEvent: Listener {
    @EventHandler
    private fun onInteract(e: PlayerInteractEvent) {
        if(e.player.vehicle != null) {
            if(e.player.burbPlayer().isDead) {
                e.isCancelled = true
            }
        } else {
            if(GameManager.getGameState() in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
                if(e.player.inventory.itemInMainHand.type == Material.POPPED_CHORUS_FRUIT && e.action.isRightClick && !e.player.hasCooldown(Material.POPPED_CHORUS_FRUIT)) {
                    BurbWeapons.useProjectileWeapon(e.player, e.player.inventory.itemInMainHand)
                }
                if(e.player.inventory.itemInMainHand.type == Material.POPPED_CHORUS_FRUIT && e.action.isLeftClick && !e.player.hasCooldown(Material.POPPED_CHORUS_FRUIT)) {
                    BurbWeapons.reloadProjectileWeapon(e.player, e.player.inventory.itemInMainHand)
                }
                if(e.player.inventory.itemInMainHand.type == Material.WOODEN_SWORD && e.action.isLeftClick && !e.player.hasCooldown(Material.WOODEN_SWORD)) {
                    BurbWeapons.useMeleeWeapon(e.player, e.player.inventory.itemInMainHand)
                }
                if(e.player.inventory.itemInMainHand.type in listOf(Material.YELLOW_DYE, Material.ORANGE_DYE, Material.RED_DYE) && e.action.isRightClick && !e.player.hasCooldown(e.player.inventory.itemInMainHand.type)) {
                    BurbAbilities.useAbility(e.player, e.player.inventory.itemInMainHand)
                }
            }
            if(GameManager.getGameState() == GameState.IDLE) {
                if(e.player.inventory.itemInMainHand == ServerItem.getProfileItem() && e.action.isRightClick && e.player.gameMode in listOf(GameMode.SURVIVAL, GameMode.ADVENTURE)) {
                    BurbInterface(e.player, BurbInterfaceType.MY_PROFILE)
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

    @EventHandler
    private fun onInteractEntity(e: PlayerInteractEntityEvent) {
        /** On interact with mannequin, get PDC to check if is registered NPC **/
        if(GameManager.getGameState() == GameState.IDLE) {
            if(e.player.inventory.itemInMainHand.type == Material.AIR && e.rightClicked is Mannequin && e.player.gameMode  in listOf(GameMode.SURVIVAL, GameMode.ADVENTURE)) {
                val npcPdcString = e.rightClicked.persistentDataContainer.get(Keys.LOBBY_NPC, PersistentDataType.STRING)
                if(npcPdcString != null) {
                    val burbNPC = BurbNPC.valueOf(npcPdcString.uppercase(getDefault()))
                    if(Cooldowns.attemptNpcInteraction(e.player)) {
                        burbNPC.onInteract(e.player, burbNPC.npcName, burbNPC.npcNameColour)
                    }
                }
            }
        }
        e.isCancelled = true
    }
}