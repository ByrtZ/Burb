package dev.byrt.burb.command

import io.papermc.paper.command.brigadier.CommandSourceStack

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.annotations.processing.CommandContainer

@Suppress("unused", "unstableApiUsage")
@CommandContainer
class BuildingCommands {
    @Command("model <name>")
    @CommandDescription("Spawns an Item Display entity with the specified model.")
    @Permission("burb.cmd.model")
    fun echo(css: CommandSourceStack, name: String) {
        if(css.sender is Player) {
            val sender = css.sender as Player
            val modelDisplay = sender.location.world.spawn(Location(sender.location.world, sender.x, sender.y, sender.z, 0.0f, 0.0f), ItemDisplay::class.java)
            val modelItem = ItemStack(Material.ECHO_SHARD, 1)
            val modelItemMeta = modelItem.itemMeta
            modelItemMeta.itemModel = NamespacedKey("minecraft", name)
            modelItem.itemMeta = modelItemMeta
            modelDisplay.setItemStack(modelItem)
        }
    }
}