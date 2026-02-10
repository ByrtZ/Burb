package dev.byrt.burb.event

import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.interfaces.BurbInterface
import dev.byrt.burb.interfaces.BurbInterfaceType
import dev.byrt.burb.item.ItemManager
import dev.byrt.burb.item.ServerItem
import dev.byrt.burb.item.ability.combo.BurbAbilityComboClick
import dev.byrt.burb.item.ability.combo.BurbAbilityComboManager
import dev.byrt.burb.item.weapon.BurbWeapons
import dev.byrt.burb.lobby.npc.BurbNPC
import dev.byrt.burb.player.BurbCharacter
import dev.byrt.burb.player.PlayerManager.burbPlayer
import dev.byrt.burb.util.Cooldowns
import dev.byrt.burb.util.Keys

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.data.*
import org.bukkit.entity.Mannequin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
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
                /** VERIFY MAIN WEAPON BEFORE DOING ANYTHING **/
                if(ItemManager.verifyMainWeapon(e.player.inventory.itemInMainHand)) {
                    val mainWeapon = e.player.inventory.itemInMainHand
                    val burbPlayer = e.player.burbPlayer()
                    /** CHECK COOLDOWN, NONE OF THE FOLLOWING ACTIONS SHOULD BE USABLE UNDER COOLDOWN **/
                    if(!burbPlayer.bukkitPlayer().hasCooldown(mainWeapon)) {
                        /** ABILITY COMBOS **/
                        if(BurbAbilityComboManager.hasCombo(burbPlayer)) { /** APPEND TO EXISTING COMBO **/
                            when(e.action) {
                                Action.LEFT_CLICK_BLOCK -> BurbAbilityComboManager.abilityCombo(burbPlayer, BurbAbilityComboClick.LEFT)
                                Action.LEFT_CLICK_AIR -> BurbAbilityComboManager.abilityCombo(burbPlayer, BurbAbilityComboClick.LEFT)
                                Action.RIGHT_CLICK_BLOCK -> BurbAbilityComboManager.abilityCombo(burbPlayer, BurbAbilityComboClick.RIGHT)
                                Action.RIGHT_CLICK_AIR -> BurbAbilityComboManager.abilityCombo(burbPlayer, BurbAbilityComboClick.RIGHT)
                                else -> {}
                            }
                        } else { /** CREATE NEW COMBO **/
                            if(burbPlayer.playerCharacter in listOf(BurbCharacter.PLANTS_SCOUT, BurbCharacter.PLANTS_RANGED, BurbCharacter.PLANTS_HEALER, BurbCharacter.ZOMBIES_SCOUT, BurbCharacter.ZOMBIES_RANGED, BurbCharacter.ZOMBIES_HEALER)) {
                                when(e.action) {
                                    /** RELOAD IF SNEAKING AND NO COMBO ACTIVE OR CREATE NEW COMBO **/
                                    in listOf(Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK) -> {
                                        if (burbPlayer.bukkitPlayer().isSneaking) {
                                            BurbWeapons.reloadProjectileWeapon(burbPlayer.bukkitPlayer(), mainWeapon)
                                        } else {
                                            BurbAbilityComboManager.abilityCombo(burbPlayer, BurbAbilityComboClick.LEFT)
                                        }
                                    }
                                    /** USE WEAPON REGULARLY IF NO COMBO ACTIVE **/
                                    in listOf(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK) -> {
                                        BurbWeapons.useProjectileWeapon(e.player, e.player.inventory.itemInMainHand)
                                    }
                                    else -> {} // Nothing
                                }
                            }
                            if(e.action.isRightClick && burbPlayer.playerCharacter in listOf(BurbCharacter.PLANTS_HEAVY, BurbCharacter.ZOMBIES_HEAVY)) {
                                when(e.action) {
                                    /** CREATE NEW COMBO **/
                                    in listOf(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK) -> {
                                        BurbAbilityComboManager.abilityCombo(burbPlayer, BurbAbilityComboClick.RIGHT)
                                    }
                                    /** USE WEAPON REGULARLY IF NO COMBO ACTIVE **/
                                    in listOf(Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK) -> {
                                        BurbWeapons.useMeleeWeapon(e.player, e.player.inventory.itemInMainHand)
                                    }
                                    else -> {} // Nothing
                                }
                            }
                        }
                    }
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
                val mannequin = e.rightClicked as Mannequin
                if(npcPdcString != null) {
                    val burbNPC = BurbNPC.valueOf(npcPdcString.uppercase(getDefault()))
                    if(Cooldowns.attemptNpcInteraction(e.player)) {
                        burbNPC.onInteract(e.player, burbNPC, mannequin)
                    }
                }
            }
        }
        e.isCancelled = true
    }
}