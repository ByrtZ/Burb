package dev.byrt.burb.event

import dev.byrt.burb.text.Formatting
import dev.byrt.burb.interfaces.BurbInterface
import dev.byrt.burb.interfaces.BurbInterfaceType
import dev.byrt.burb.plugin

import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.*
import org.bukkit.scheduler.BukkitRunnable

@Suppress("unused")
class InventoryEvent: Listener {
    @EventHandler
    private fun onInventoryClick(e : InventoryClickEvent) {
        e.isCancelled = e.whoClicked.gameMode != GameMode.CREATIVE
    }

    @EventHandler
    private fun onInventoryMove(e : InventoryMoveItemEvent) {
        e.isCancelled = true
    }

    @EventHandler
    private fun onInventoryDrag(e : InventoryDragEvent) {
        e.isCancelled = e.whoClicked.gameMode != GameMode.CREATIVE
    }

    @EventHandler
    private fun onInventoryPickupItem(e : InventoryPickupItemEvent) {
        e.isCancelled = true
    }

    @EventHandler
    private fun onInterfaceClose(e: InventoryCloseEvent) {
        if(e.view.title() in listOf(Formatting.allTags.deserialize("<!i><b><burbcolour><shadow:#0:0.75>${BurbInterfaceType.TEAM_SELECT.interfaceName}"), Formatting.allTags.deserialize("<!i><b><burbcolour><shadow:#0:0.75>${BurbInterfaceType.CHARACTER_SELECT.interfaceName}"))) {
            if(e.reason == InventoryCloseEvent.Reason.PLAYER) {
                object : BukkitRunnable() {
                    override fun run() {
                        when(e.view.title()) {
                            Formatting.allTags.deserialize("<!i><b><burbcolour><shadow:#0:0.75>${BurbInterfaceType.TEAM_SELECT.interfaceName}") -> {
                                BurbInterface(e.player as Player, BurbInterfaceType.TEAM_SELECT)
                            }
                            Formatting.allTags.deserialize("<!i><b><burbcolour><shadow:#0:0.75>${BurbInterfaceType.CHARACTER_SELECT.interfaceName}") -> {
                                BurbInterface(e.player as Player, BurbInterfaceType.CHARACTER_SELECT)
                            }
                        }
                    }
                }.runTaskLater(plugin, 1L)
            }
        }
    }
}