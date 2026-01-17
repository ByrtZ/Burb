package dev.byrt.burb.command

import dev.byrt.burb.plugin
import dev.byrt.burb.text.ChatUtility
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.annotations.processing.CommandContainer
import org.incendo.cloud.processors.confirmation.annotation.Confirmation

@Suppress("unused")
@CommandContainer
@Permission("burb.cmd.pack")
class PackCommands {

    @Command("pack refresh")
    @Confirmation
    suspend fun packRefresh(sender: CommandSender) {
        plugin.resourcePackLoader.reloadPack()
        ChatUtility.broadcastDev(
            "<yellow>${sender.name} <green>refreshed<reset> the <notifcolour>resource pack<reset>.",
            false
        )
    }

    @Command("pack on [player]")
    fun enableForPlayer(sender: CommandSender, @Argument player: Player?) {
        plugin.resourcePackApplier.enablePacks(player ?: sender as? Player ?: return)
    }

    @Command("pack off [player]")
    fun disableForPlayer(sender: CommandSender, @Argument player: Player?) {
        plugin.resourcePackApplier.disablePacks(player ?: sender as? Player ?: return)
    }
}