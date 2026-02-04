package dev.byrt.burb.lobby.npc

import dev.byrt.burb.interfaces.BurbInterface
import dev.byrt.burb.interfaces.BurbInterfaceType
import dev.byrt.burb.item.rarity.ItemRarity
import dev.byrt.burb.text.Formatting
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.entity.Pose

enum class BurbNPC(val npcName: String, val npcDescription: String, val npcNameColour: String, val npcLocation: Location, val npcSkinTexture: String, var npcPose: Pose = Pose.STANDING, val onInteract: (Player, String, String) -> Unit = { _, _, _ -> }) {
    LOBBY_FISHING_ROD_GIVER(
        "Rodney Gibbs",
        "<i>Get yer fishin' rods!",
        "<#ffff00>",
        Location(Bukkit.getWorlds()[0], 92.5, 1.0, 84.5, 135f, 0f),
        "ewogICJ0aW1lc3RhbXAiIDogMTc2Nzk3NzM1ODk3NywKICAicHJvZmlsZUlkIiA6ICIyYjcyZWYyYWUzMmQ0Zjc1OGEyMThlMDI4MTViYmNjZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJ2b2xrb2RhZl82MyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zYzkzOGUwYmFjOGM1MjJkZDBiNmIwZWE1MDQ2YjQ3NDlmZjM0YWZiMzdhZTFiYzc0NTIzZGMxOThhYjllZmJkIgogICAgfQogIH0KfQ==",
        onInteract = { player, npcName, npcNameColour ->
            player.sendMessage(Formatting.allTags.deserialize("<b>${npcNameColour}${npcName}</b><white>: Ready to catch some fishies me hearty?"))
            BurbNPCs.playNPCDialogue(player, BurbNPCSoundSet.GENERIC_NPC_DIALOGUE)
            BurbInterface(player, BurbInterfaceType.FISHING_ROD_GIVER)
        }
    ),
    LOBBY_FISHING_CATALOGUE_VIEWER(
        "Fish Enthusiast",
        "<#5f9afa>View fish collection",
        "<#ffff00>",
        Location(Bukkit.getWorlds()[0], 95.5, 1.0, 85.5, 0f, 0f),
        "ewogICJ0aW1lc3RhbXAiIDogMTYyMTYyODk4NDIzNCwKICAicHJvZmlsZUlkIiA6ICI5OTdjZjFlMmY1NGQ0YzEyOWY2ZjU5ZTVlNjU1YjZmNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJpbzEyIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2UzZDc5NGM4NjcwNzQ3ZDcwNDY1NzIyYTk4NzYxZWJmYzgxYTk3ZjZjNTZmYTRmOTA3NzI4YzlmNWU0NTJhNTUiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
        onInteract = { player, npcName, npcNameColour ->
            player.sendMessage(Formatting.allTags.deserialize("<b>${npcNameColour}${npcName}</b><white>: Want to know anything fishy? I'm your guy!"))
            BurbNPCs.playNPCDialogue(player, BurbNPCSoundSet.GENERIC_NPC_DIALOGUE)
            BurbInterface(player, BurbInterfaceType.FISHING_SELECT)
        }
    ),
    LOBBY_FISHING_SURFER_SHOP(
        "Jimmy Surfsup",
        "<i>Let's catch a wave dude...",
        "<#ffff00>",
        Location(Bukkit.getWorlds()[0], 99.5, 7.0, 91.5, 270f, 0f),
        "ewogICJ0aW1lc3RhbXAiIDogMTc2ODA2NzU2MTA0MiwKICAicHJvZmlsZUlkIiA6ICI5OGQxYTQyNmRlMmU0NjBkYjdjNWExMmY5MGNhODg0OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJLdWJpbm9TSyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81YjgwYjRlNjhhZGI3ZmVjMDRhZDg4ODllYzNkNjk5NzM4MDRhZDUxYzc2NjY5NjRiMDBmOWY0OTJhNWM3YWVkIgogICAgfQogIH0KfQ==",
        onInteract = { player, npcName, npcNameColour ->
            player.sendMessage(Formatting.allTags.deserialize("<b>${npcNameColour}${npcName}</b><white>: Alright mate! Board delivery ain't due in for a little while, keep an eye out though ey?"))
            BurbNPCs.playNPCDialogue(player, BurbNPCSoundSet.GENERIC_NPC_DIALOGUE)
        }
    ),
    LOBBY_SECRET_GNOME_HUNT_START(
        "Suspicious Gnome",
        "<${ItemRarity.SPECIAL.rarityColour}>???",
        "<${ItemRarity.SPECIAL.rarityColour}>",
        Location(Bukkit.getWorlds()[0], 48.5, 1.0, 31.5, -25f, 0f),
        "ewogICJ0aW1lc3RhbXAiIDogMTc0ODgwNDEwNTAyNiwKICAicHJvZmlsZUlkIiA6ICIzOTg5OGFiODFmMjU0NmQxOGIyY2ExMTE1MDRkZGU1MCIsCiAgInByb2ZpbGVOYW1lIiA6ICI4YjJjYTExMTUwNGRkZTUwIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzcxMWJhYmIxZjJiN2JiZGQwNGZiNDQ2YmZiMmRlNTMzMjM1NWY5YjZlNTQzYTc4ZTE5ZThhNGEyNmQ3MDU0NGQiCiAgICB9CiAgfQp9",
        onInteract = { player, npcName, npcNameColour ->
            player.sendMessage(Formatting.allTags.deserialize("<b>${npcNameColour}${npcName}</b><white>: Oh Ho Ho! Human, we are not prepared for you just yet. Return soon... Fa La La!"))
            BurbNPCs.playNPCDialogue(player, BurbNPCSoundSet.ALTERNATE_NPC_DIALOGUE)
        }
    ),
    LOBBY_ADMIN_BYRT(
        "Byrt",
        "<i>Coding is hard...",
        "<#ffff00>",
        Location(Bukkit.getWorlds()[0], 30.5, 0.0, 14.5, 90f, 0f),
        "ewogICJ0aW1lc3RhbXAiIDogMTc3MDA1MzU4MzExMCwKICAicHJvZmlsZUlkIiA6ICI3YjE3NjBiYmVkNTk0Mjc1YmU3ZjhjMGFlYmQzMmRiNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJTdmVuTmlqaHVpczQiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWUxY2IzMDk3MGUwMDBiYTU2MjgyNzcxNDc4Y2RkMjI5MjhiNDFhZWM1NmQ1MzMzNzQ5ZjFiMjAxMjEwOWNmNSIKICAgIH0KICB9Cn0=",
        onInteract = { player, npcName, npcNameColour ->
            player.sendMessage(Formatting.allTags.deserialize("<b>${npcNameColour}${npcName}</b><white>: What do you mean you found <i>another</i> bug? They're going to learn how to throw hands!"))
            BurbNPCs.playNPCDialogue(player, BurbNPCSoundSet.GENERIC_NPC_DIALOGUE)
        }
    ),
    LOBBY_ADMIN_FLAMEY(
        "Flamey",
        "<i>All aboard crew!",
        "<#ffff00>",
        Location(Bukkit.getWorlds()[0], 24.5, 0.0, 26.5, 70f, 0f),
        "ewogICJ0aW1lc3RhbXAiIDogMTc3MDA1NDAzNzg1OSwKICAicHJvZmlsZUlkIiA6ICJjM2ZmNTY5OWZlNWI0OTY2YTYzYzdhMTEzNTBjZGIyNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJUZWNobzkwMDAiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTY3YTZjZDA4ZjJmODliNTNjM2JjNTViNTVlOTY1YmViYzc5MjM5MWViOGY5MTNlODVmYWQ4ZGUzY2Y0MTFlIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
        onInteract = { player, npcName, npcNameColour ->
            player.sendMessage(Formatting.allTags.deserialize("<b>${npcNameColour}${npcName}</b><white>: They asked me, \"How many assets should we have?\", I replied, \"<b>Yes</b>.\""))
            BurbNPCs.playNPCDialogue(player, BurbNPCSoundSet.GENERIC_NPC_DIALOGUE)
        }
    ),
    LOBBY_ADMIN_MASKY(
        "Masky",
        "<i>Pencils at the ready!",
        "<#ffff00>",
        Location(Bukkit.getWorlds()[0], 24.5, 0.0, 29.5, 110f, 0f),
        "ewogICJ0aW1lc3RhbXAiIDogMTc1MTg4OTU2NzgyNywKICAicHJvZmlsZUlkIiA6ICJkYzA5MjA4MTM2ZDg0Y2Y5OWIwMzFmMGI1NzM4OTdmNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJLRUlUSF8wMzAyIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Y5NmNhZmMzOGZmYjFmODE4YTU1M2M0MjQyMjA4ZDAxODQyNGRiYzc3N2I1ZjgyMjhlYzM3YjUzZDZiNWRlMDAiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
        onInteract = { player, npcName, npcNameColour ->
            player.sendMessage(Formatting.allTags.deserialize("<b>${npcNameColour}${npcName}</b><white>: Just one more skin commission... I <strikethrough>do not</strikethrough> promise!"))
            BurbNPCs.playNPCDialogue(player, BurbNPCSoundSet.GENERIC_NPC_DIALOGUE)
        }
    ),
    LOBBY_ADMIN_LUCY(
        "Lucy",
        "<i>*typing noises*",
        "<#ffff00>",
        Location(Bukkit.getWorlds()[0], 43.5, 11.0, 11.5, 65f, 0f),
        "ewogICJ0aW1lc3RhbXAiIDogMTc3MDA1NDExNDUyNSwKICAicHJvZmlsZUlkIiA6ICI1YTQzNmM4NWRiNGQ0N2UzODAyNDdlZmRiOTBkNWRlOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJTYWJlclBhd3MiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDlkMWZkNzk0NmIyMWRkZTM0NGVkMTEyZWM3OGRiNjQzYjQ4MDYzMTdhMWI5ODcyOTFmZTcyNzYxMjNiOTcyMCIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9",
        onInteract = { player, npcName, npcNameColour ->
            player.sendMessage(Formatting.allTags.deserialize("<b>${npcNameColour}${npcName}</b><white>: Don't mind me, just testing in production."))
            BurbNPCs.playNPCDialogue(player, BurbNPCSoundSet.GENERIC_NPC_DIALOGUE)
        }
    );
}