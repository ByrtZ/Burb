package dev.byrt.burb.command

import dev.byrt.burb.text.ChatUtility
import dev.byrt.burb.text.Formatting
import dev.byrt.burb.game.ScoreManager
import dev.byrt.burb.interfaces.BurbInterface
import dev.byrt.burb.interfaces.BurbInterfaceType
import dev.byrt.burb.item.ItemRarity
import dev.byrt.burb.item.ItemType
import dev.byrt.burb.lobby.LobbyManager
import dev.byrt.burb.logger
import dev.byrt.burb.player.progression.BurbLevel
import dev.byrt.burb.player.progression.BurbProgression
import dev.byrt.burb.plugin
import dev.byrt.burb.team.Teams
import dev.byrt.burb.text.TextAlignment
import dev.byrt.burb.util.CommitGrabber

import io.papermc.paper.command.brigadier.CommandSourceStack
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.bossbar.BossBar

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

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

    @Command("class")
    @CommandDescription("Returns to title screen.")
    @Permission("burb.cmd.class")
    fun classCommand(css: CommandSourceStack) {
        if(css.sender is Player) {
            val player = css.sender as Player
            LobbyManager.playerJoinTitleScreen(player)
        }
    }

    @Command("debug score <team>")
    @CommandDescription("Debug display score")
    @Permission("burb.cmd.debug")
    fun debugDisplayScore(css: CommandSourceStack, @Argument("team") team: Teams) {
        if(css.sender is Player) {
            val player = css.sender as Player
            player.sendMessage(Formatting.allTags.deserialize("$team DISPLAY SCORE: ${ScoreManager.getDisplayScore(team)} (${if(team == Teams.PLANTS) ScoreManager.getPlantsScore() else if(team == Teams.ZOMBIES) ScoreManager.getZombiesScore() else -1})"))
        }
    }

    @Command("latestupdate")
    @CommandDescription("Grabs latest commit")
    @Permission("burb.cmd.updates")
    fun latestCommit(css: CommandSourceStack) {
        if(css.sender is Player) {
            ChatUtility.broadcastDev("<dark_gray>Fetching latest commit.", false)
            CommitGrabber.grabLatestCommit()
        }
    }

    @Command("debug tasks")
    @CommandDescription("Lists all running tasks")
    @Permission("burb.cmd.debug")
    fun debugTasks(css: CommandSourceStack) {
        logger.warning("Active Workers:")
        Bukkit.getScheduler().activeWorkers.forEach { w -> logger.info("ID: ${w.taskId} ||||| Thread: ${w.thread} ||||| Thread name: ${w.thread.name} ||||| Owner: ${w.owner.name}") }
        logger.warning("Pending Tasks:")
        Bukkit.getScheduler().pendingTasks.forEach { t -> logger.info("ID: ${t.taskId} ||||| Sync: ${t.isSync} ||||| Owner: ${t.owner.name} ||||| Cancelled: ${t.isCancelled}") }
    }

    @Command("debug bossbar <text>")
    @CommandDescription("Shows temporary test bossbar")
    @Permission("burb.cmd.debug")
    fun debugBossbar(css: CommandSourceStack, @Argument("text") text: Array<String>) {
        if(css.sender is Player) {
            val tempBossBar = BossBar.bossBar(TextAlignment.centreBossBarText(text.joinToString(" ")), 0f, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS).apply {
                addViewer(Audience.audience(css.sender as Player))
            }
            object : BukkitRunnable() {
                override fun run() {
                    for(player in Bukkit.getOnlinePlayers()) {
                        tempBossBar.removeViewer(player)
                    }
                }
            }.runTaskLater(plugin, 200L)
        }
    }

    @Command("progression add_xp <xp>")
    @CommandDescription("Debug command for XP")
    @Permission("burb.cmd.debug")
    fun debugXp(css: CommandSourceStack, @Argument("xp") xp: Int) {
        if(css.sender is Player) {
            val player = css.sender as Player
            BurbProgression.appendExperience(player, xp)
        }
    }

    @Command("progression set_level <level>")
    @CommandDescription("Debug command for levels")
    @Permission("burb.cmd.debug")
    fun debugLevel(css: CommandSourceStack, @Argument("level") level: BurbLevel) {
        if(css.sender is Player) {
            val player = css.sender as Player
            BurbProgression.setLevel(player, level)
        }
    }
}