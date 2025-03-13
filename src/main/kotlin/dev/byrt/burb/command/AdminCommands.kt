package dev.byrt.burb.command

import dev.byrt.burb.chat.ChatUtility
import dev.byrt.burb.interfaces.BurbInterface
import dev.byrt.burb.interfaces.BurbInterfaceType
import dev.byrt.burb.item.ItemRarity
import dev.byrt.burb.item.ItemType

import io.papermc.paper.command.brigadier.CommandSourceStack

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.annotations.processing.CommandContainer

@Suppress("unused", "unstableApiUsage")
@CommandContainer
class AdminCommands {
    @Command("ac <text>")
    @CommandDescription("Changes the value of the specified setting.")
    @Permission("burb.cmd.admin_chat")
    fun adminChat(css: CommandSourceStack, @Argument("text") text: Array<String>) {
        if(css.sender is Player) {
            val player = css.sender as Player
            ChatUtility.broadcastAdmin("<skull:${player.name}><dark_red>${player.name}<white>: ${text.joinToString(" ")}", false)
        }
    }

    @Command("dc <text>")
    @CommandDescription("Changes the value of the specified setting.")
    @Permission("burb.cmd.dev_chat")
    fun devChat(css: CommandSourceStack, @Argument("text") text: Array<String>) {
        if(css.sender is Player) {
            val player = css.sender as Player
            ChatUtility.broadcastDev("<skull:${player.name}><gold>${player.name}<white>: ${text.joinToString(" ")}", false)
        }
    }

    @Command("item test rarities")
    @CommandDescription("Debug command for item rarity testing.")
    @Permission("burb.cmd.debug")
    fun debugTestRarities(css: CommandSourceStack) {
        if(css.sender is Player) {
            val player = css.sender as Player
            for(rarity in ItemRarity.entries) {
                val rarityTestItem = ItemStack(Material.COD, 1)
                val rarityTestItemMeta = rarityTestItem.itemMeta
                rarityTestItemMeta.displayName(
                    Component.text("Test Item").color(TextColor.fromHexString(rarity.rarityColour)).decoration(
                        TextDecoration.ITALIC, false))
                val rarityTestItemLore = listOf(
                    Component.text(rarity.rarityGlyph, NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false),
                    Component.text("Debug item.", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                )
                rarityTestItemMeta.lore(rarityTestItemLore)
                rarityTestItem.itemMeta = rarityTestItemMeta
                player.inventory.addItem(ItemStack(rarityTestItem))
            }
        }
    }

    @Command("item test types")
    @CommandDescription("Debug command for item type testing.")
    @Permission("burb.cmd.debug")
    fun debugTestTypes(css: CommandSourceStack) {
        if(css.sender is Player) {
            val player = css.sender as Player
            for(type in ItemType.entries) {
                val typeTestItem = ItemStack(Material.SALMON, 1)
                val typeTestItemMeta = typeTestItem.itemMeta
                typeTestItemMeta.displayName(
                    Component.text("Test Item").color(NamedTextColor.WHITE).decoration(
                        TextDecoration.ITALIC, false))
                val typeTestItemLore = listOf(
                    Component.text(type.typeGlyph, NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false),
                    Component.text("Debug item.", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                )
                typeTestItemMeta.lore(typeTestItemLore)
                typeTestItem.itemMeta = typeTestItemMeta
                player.inventory.addItem(ItemStack(typeTestItem))
            }
        }
    }

    @Command("interface <type>")
    @CommandDescription("Debug command for interfaces.")
    @Permission("burb.cmd.interfaces")
    fun interfaceCommand(css: CommandSourceStack, @Argument("type") type: BurbInterfaceType) {
        if(css.sender is Player) {
            val player = css.sender as Player
            BurbInterface(player, type)
        }
    }
}