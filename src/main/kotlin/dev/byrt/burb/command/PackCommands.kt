package dev.byrt.burb.command

import dev.byrt.burb.text.ChatUtility
import dev.byrt.burb.util.ResourcePacker

import io.papermc.paper.command.brigadier.CommandSourceStack

import org.bukkit.Bukkit
import org.bukkit.entity.Player

import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.annotations.processing.CommandContainer
import org.incendo.cloud.processors.confirmation.annotation.Confirmation

@Suppress("unused", "unstableApiUsage")
@CommandContainer
class PackCommands {
    @Command("pack refresh")
    @Permission("burb.cmd.pack")
    @Confirmation
    fun packRefresh(css: CommandSourceStack) {
        ChatUtility.broadcastDev("<yellow>${css.sender.name} <green>refreshed<reset> the <notifcolour>resource pack<reset> for all online users.", false)
        val online = Bukkit.getOnlinePlayers()
        online.forEach { onlinePlayer -> ResourcePacker.removePackPlayer(onlinePlayer) }
        online.forEach { onlinePlayer -> ResourcePacker.applyPackPlayer(onlinePlayer) }
    }

    @Command("pack refresh <player>")
    @Permission("burb.cmd.pack")
    @Confirmation
    fun packRefresh(css: CommandSourceStack, player: Player) {
        ChatUtility.broadcastDev("<yellow>${css.sender.name} <green>refreshed<reset> the <notifcolour>resource pack<reset> for ${player.name}.", false)
        ResourcePacker.removePackPlayer(player)
        ResourcePacker.applyPackPlayer(player)
    }

    @Command("pack push")
    @Permission("burb.cmd.pack")
    @Confirmation
    fun packPush(css: CommandSourceStack) {
        ChatUtility.broadcastDev("<yellow>${css.sender.name} <green>pushed<reset> the <notifcolour>resource pack<reset> to all online users.", false)
        Bukkit.getOnlinePlayers().forEach { online -> ResourcePacker.applyPackPlayer(online) }
    }

    @Command("pack push <player>")
    @Permission("burb.cmd.pack")
    @Confirmation
    fun packPush(css: CommandSourceStack, player: Player) {
        ChatUtility.broadcastDev("<yellow>${css.sender.name} <green>pushed<reset> the <notifcolour>resource pack<reset> to ${player.name}.", false)
        ResourcePacker.applyPackPlayer(player)
    }

    @Command("pack pop")
    @Permission("burb.cmd.pack")
    @Confirmation
    fun packPop(css: CommandSourceStack) {
        ChatUtility.broadcastDev("<yellow>${css.sender.name} <red>popped<reset> the <notifcolour>resource pack<reset> from all online users.", false)
        Bukkit.getOnlinePlayers().forEach { online -> ResourcePacker.removePackPlayer(online) }
    }

    @Command("pack pop <player>")
    @Permission("burb.cmd.pack")
    @Confirmation
    fun packPop(css: CommandSourceStack, player: Player) {
        ChatUtility.broadcastDev("<yellow>${css.sender.name} <green>pushed<reset> the <notifcolour>resource pack<reset>, applying to all online users.", false)
        ResourcePacker.removePackPlayer(player)
    }
}