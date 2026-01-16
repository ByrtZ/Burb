package dev.byrt.burb.command

import dev.byrt.burb.text.ChatUtility
import dev.byrt.burb.text.Formatting
import dev.byrt.burb.game.Scores
import dev.byrt.burb.game.location.SpawnPoints
import dev.byrt.burb.interfaces.BurbInterface
import dev.byrt.burb.interfaces.BurbInterfaceType
import dev.byrt.burb.item.ItemRarity
import dev.byrt.burb.item.ItemType
import dev.byrt.burb.item.SubRarity
import dev.byrt.burb.lobby.FishRarity
import dev.byrt.burb.lobby.LobbyFishing
import dev.byrt.burb.lobby.BurbLobby
import dev.byrt.burb.lobby.BurbNPC
import dev.byrt.burb.lobby.BurbNPCs
import dev.byrt.burb.logger
import dev.byrt.burb.player.cosmetics.BurbCosmetic
import dev.byrt.burb.player.cosmetics.BurbCosmetics
import dev.byrt.burb.player.progression.BurbLevel
import dev.byrt.burb.player.progression.BurbPlayerData
import dev.byrt.burb.plugin
import dev.byrt.burb.team.Teams
import dev.byrt.burb.text.TextAlignment
import dev.byrt.burb.util.CommitIntegration

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.bossbar.BossBar

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.GameMode

import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Item
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
    fun adminChat(sender: Player, @Argument("text") text: Array<String>) {
        ChatUtility.broadcastAdmin(
            "<skull:${sender.name}><dark_red>${sender.name}<white>: ${text.joinToString(" ")}",
            false
        )
    }

    @Command("dc <text>")
    @CommandDescription("Changes the value of the specified setting.")
    @Permission("burb.cmd.dev_chat")
    fun devChat(sender: Player, @Argument("text") text: Array<String>) {
        ChatUtility.broadcastDev(
            "<skull:${sender.name}><gold>${sender.name}<white>: ${text.joinToString(" ")}",
            false
        )
    }

    @Command("debug catch <rarity> <subrarity>")
    @Permission("tbd.command.debug")
    fun debug(
        player: Player,
        @Argument("rarity") rarity: FishRarity,
        @Argument("subrarity") subrarity: SubRarity
    ) {
        if (player.gameMode == GameMode.CREATIVE) {
            player.sendMessage(Component.text("Simulating catch of rarity $rarity"))
            val loc = player.location
            object : BukkitRunnable() {
                override fun run() {
                    val item = loc.world.spawn(loc, Item::class.java)
                    item.itemStack = ItemStack(Material.BEEF, 1)
                    LobbyFishing.catchFish(player, item, item.location, rarity, subrarity)
                }
            }.runTaskLater(plugin, 100L)
        }
    }

    @Command("debug item_rarities")
    @CommandDescription("Debug command for item rarity testing.")
    @Permission("burb.cmd.debug")
    fun debugTestRarities(player: Player) {
        for (rarity in ItemRarity.entries) {
            val rarityTestItem = ItemStack(Material.COD, 1)
            val rarityTestItemMeta = rarityTestItem.itemMeta
            rarityTestItemMeta.displayName(
                Component.text("Test Item").color(TextColor.fromHexString(rarity.rarityColour)).decoration(
                    TextDecoration.ITALIC, false
                )
            )
            val rarityTestItemLore = listOf(
                Component.text(rarity.rarityGlyph, NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false),
                Component.text("Debug item.", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
            )
            rarityTestItemMeta.lore(rarityTestItemLore)
            rarityTestItem.itemMeta = rarityTestItemMeta
            player.inventory.addItem(ItemStack(rarityTestItem))
        }
    }

    @Command("debug item_types")
    @CommandDescription("Debug command for item type testing.")
    @Permission("burb.cmd.debug")
    fun debugTestTypes(player: Player) {
        for (type in ItemType.entries) {
            val typeTestItem = ItemStack(Material.SALMON, 1)
            val typeTestItemMeta = typeTestItem.itemMeta
            typeTestItemMeta.displayName(
                Component.text("Test Item").color(NamedTextColor.WHITE).decoration(
                    TextDecoration.ITALIC, false
                )
            )
            val typeTestItemLore = listOf(
                Component.text(type.typeGlyph, NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false),
                Component.text("Debug item.", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
            )
            typeTestItemMeta.lore(typeTestItemLore)
            typeTestItem.itemMeta = typeTestItemMeta
            player.inventory.addItem(ItemStack(typeTestItem))
        }
    }

    @Command("interface <type>")
    @CommandDescription("Debug command for interfaces.")
    @Permission("burb.cmd.interfaces")
    fun interfaceCommand(sender: Player, @Argument("type") type: BurbInterfaceType) {
        BurbInterface(sender, type)
    }

    @Command("class")
    @CommandDescription("Returns to title screen.")
    @Permission("burb.cmd.class")
    fun classCommand(sender: Player) {
        BurbLobby.playerJoinTitleScreen(sender)
    }

    @Command("debug score <team>")
    @CommandDescription("Debug display score")
    @Permission("burb.cmd.debug")
    fun debugDisplayScore(sender: CommandSender, @Argument("team") team: Teams) {
        sender.sendMessage(Formatting.allTags.deserialize("$team DISPLAY SCORE: ${Scores.getDisplayScore(team)} (${if (team == Teams.PLANTS) Scores.getPlantsScore() else if (team == Teams.ZOMBIES) Scores.getZombiesScore() else -1})"))
    }

    @Command("latestupdate")
    @CommandDescription("Grabs latest commit")
    @Permission("burb.cmd.updates")
    fun latestCommit(sender: CommandSender) {
        ChatUtility.broadcastDev("<dark_gray>Fetching latest commit.", false)
        CommitIntegration.grabLatestCommit()
    }

    @Command("debug tasks")
    @CommandDescription("Lists all running tasks")
    @Permission("burb.cmd.debug")
    fun debugTasks(sender: CommandSender) {
        ChatUtility.broadcastDev(
            "<dark_gray>Tasks printed to console, <click:open_url:'https://panel.pebblehost.com'>[Click here]</click> to view.",
            true
        )
        logger.warning("Active Workers:")
        Bukkit.getScheduler().activeWorkers.forEach { w -> logger.info("ID: ${w.taskId} ||||| Thread: ${w.thread} ||||| Thread name: ${w.thread.name} ||||| Owner: ${w.owner.name}") }
        logger.warning("Pending Tasks:")
        Bukkit.getScheduler().pendingTasks.forEach { t -> logger.info("ID: ${t.taskId} ||||| Sync: ${t.isSync} ||||| Owner: ${t.owner.name} ||||| Cancelled: ${t.isCancelled}") }
    }

    @Command("debug bossbar <text>")
    @CommandDescription("Shows temporary test bossbar")
    @Permission("burb.cmd.debug")
    fun debugBossbar(sender: Player, @Argument("text") text: Array<String>) {
        val tempBossBar = BossBar.bossBar(
            TextAlignment.centreBossBarText(text.joinToString(" ")),
            0f,
            BossBar.Color.WHITE,
            BossBar.Overlay.PROGRESS
        ).apply {
            addViewer(Audience.audience(sender))
        }
        object : BukkitRunnable() {
            override fun run() {
                for (player in Bukkit.getOnlinePlayers()) {
                    tempBossBar.removeViewer(player)
                }
            }
        }.runTaskLater(plugin, 200L)
    }

    @Command("debug actionbar <low> <lower>")
    @CommandDescription("Shows temporary test actionbar")
    @Permission("burb.cmd.debug")
    fun debugActionbar(player: Player, @Argument("low") low: String, @Argument("lower") lower: String) {
        player.sendActionBar(TextAlignment.centreActionBarText(low, lower))
    }

    @Command("progression add_xp <xp>")
    @CommandDescription("Debug command for XP")
    @Permission("burb.cmd.debug")
    fun debugXp(player: Player, @Argument("xp") xp: Int) {
        BurbPlayerData.appendExperience(player, xp)
    }

    @Command("progression set_level <level>")
    @CommandDescription("Debug command for levels")
    @Permission("burb.cmd.debug")
    fun debugLevel(player: Player, @Argument("level") level: BurbLevel) {
        BurbPlayerData.setLevel(player, level)
    }

    @Command("cosmetic item <cosmetic> [player]")
    @CommandDescription("Debug command for cosmetics")
    @Permission("burb.cmd.debug")
    fun debugCosmeticItem(
        sender: CommandSender,
        @Argument("cosmetic") cosmetic: BurbCosmetic,
        @Argument("player") player: Player?
    ) {
        val targetPlayer = player ?: sender as? Player ?: return
        BurbCosmetics.giveCosmeticItem(targetPlayer, cosmetic)
    }

    @Command("cosmetic give <cosmetic> [player]")
    @CommandDescription("Debug command for cosmetics")
    @Permission("burb.cmd.debug")
    fun debugCosmeticUnlock(
        sender: CommandSender,
        @Argument("cosmetic") cosmetic: BurbCosmetic,
        @Argument("player") player: Player?
    ) {
        if (cosmetic == BurbCosmetic.INVALID_COSMETIC) return
        val targetPlayer = player ?: sender as? Player ?: return
        BurbCosmetics.unlockCosmetic(targetPlayer, cosmetic)
    }

    @Command("cosmetic give_all [player]")
    @CommandDescription("Debug command for cosmetics")
    @Permission("burb.cmd.debug")
    fun debugCosmeticUnlockAll(sender: CommandSender, @Argument("player") player: Player?) {
        val targetPlayer = player ?: sender as? Player ?: return
        BurbCosmetic.entries.forEach { cosmetic ->
            if (cosmetic == BurbCosmetic.INVALID_COSMETIC) return@forEach
            BurbCosmetics.unlockCosmetic(
                targetPlayer,
                cosmetic
            )
        }
    }


    @Command("cosmetic equip <cosmetic>")
    @CommandDescription("Debug command for cosmetics")
    @Permission("burb.cmd.debug")
    fun debugCosmeticEquip(player: Player, @Argument("cosmetic") cosmetic: BurbCosmetic) {
        BurbCosmetics.equipCosmetic(player, cosmetic, false)
    }

    @Command("cosmetic unequip <type>")
    @CommandDescription("Debug command for cosmetics")
    @Permission("burb.cmd.debug")
    fun debugCosmeticUnequip(player: Player, @Argument("type") type: ItemType) {
        BurbCosmetics.unequipCosmetic(player, type)
    }

    @Command("cosmetic view_all")
    @CommandDescription("Debug command for cosmetics")
    @Permission("burb.cmd.debug")
    fun debugCosmeticView(player: Player) {
        BurbInterface(player, BurbInterfaceType.ALL_COSMETICS)
    }

    @Command("debug npc spawn <npc>")
    @CommandDescription("Debug command for NPCs")
    @Permission("burb.cmd.debug")
    fun debugSpawnNPC(player: Player, @Argument("npc") npc: BurbNPC) {
        ChatUtility.broadcastDev("<dark_gray>${player.name} spawned NPC: ${npc.npcName} [$npc]", false)
        BurbNPCs.spawnNPC(npc)
    }

    @Command("debug npc spawn_all")
    @CommandDescription("Debug command for NPCs")
    @Permission("burb.cmd.debug")
    fun debugSpawnAllNPCs(player: Player) {
        ChatUtility.broadcastDev("<dark_gray>${player.name} spawned all registered NPCs", false)
        BurbNPCs.spawnAllNPCs()
    }

    @Command("debug npc destroy_all")
    @CommandDescription("Debug command for NPCs")
    @Permission("burb.cmd.debug")
    fun debugDestroyAllNPCs(sender: CommandSender) {
        BurbNPCs.clearNPCs()
    }

    @Command("debug list_spawns")
    @CommandDescription("Debug command for spawn points")
    @Permission("burb.cmd.debug")
    fun debugSpawnsList(player: Player) {
        player.sendMessage(Formatting.allTags.deserialize("<plantscolour>Plant Spawns:"))
        SpawnPoints.getPlantSpawns()
            .forEach { spawn -> player.sendMessage(Formatting.allTags.deserialize("<burbcolour>Spawn at ${spawn.x}, ${spawn.y}, ${spawn.z} <#ffff00><click:run_command:'/tp @s ${spawn.x} ${spawn.y} ${spawn.z} ${spawn.yaw} ${spawn.pitch}'>[Click to teleport]</click>")) }
        player.sendMessage(Formatting.allTags.deserialize("<zombiescolour>Zombie Spawns:"))
        SpawnPoints.getZombieSpawns()
            .forEach { spawn -> player.sendMessage(Formatting.allTags.deserialize("<burbcolour>Spawn at ${spawn.x}, ${spawn.y}, ${spawn.z} <#ffff00><click:run_command:'/tp @s ${spawn.x} ${spawn.y} ${spawn.z} ${spawn.yaw} ${spawn.pitch}'>[Click to teleport]</click>")) }

    }
}