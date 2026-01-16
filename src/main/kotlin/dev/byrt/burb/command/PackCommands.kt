package dev.byrt.burb.command

import dev.byrt.burb.plugin
import dev.byrt.burb.text.ChatUtility
import io.papermc.paper.command.brigadier.CommandSourceStack
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
    suspend fun packRefresh(css: CommandSourceStack) {
        plugin.resourcePackLoader.reloadPack()
        ChatUtility.broadcastDev(
            "<yellow>${css.sender.name} <green>refreshed<reset> the <notifcolour>resource pack<reset>.",
            false
        )
    }

    @Command("pack on [player]")
    fun enableForPlayer(css: CommandSourceStack, @Argument player: Player?) {
        plugin.resourcePackApplier.enablePacks(player ?: css.sender as? Player ?: return)
    }

    @Command("pack off [player]")
    fun disableForPlayer(css: CommandSourceStack, @Argument player: Player?) {
        plugin.resourcePackApplier.disablePacks(player ?: css.sender as? Player ?: return)
    }
}