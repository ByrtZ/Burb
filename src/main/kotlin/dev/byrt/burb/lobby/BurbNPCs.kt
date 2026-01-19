package dev.byrt.burb.lobby

import dev.byrt.burb.text.Formatting
import dev.byrt.burb.util.Keys
import dev.byrt.burb.library.Sounds

import io.papermc.paper.datacomponent.item.ResolvableProfile

import com.destroystokyo.paper.profile.ProfileProperty
import dev.byrt.burb.interfaces.BurbInterface
import dev.byrt.burb.interfaces.BurbInterfaceType

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Mannequin
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

@Suppress("unstableApiUsage")
object BurbNPCs {
    private const val FALLBACK_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2FlMWNiMzA5NzBlMDAwYmE1NjI4Mjc3MTQ3OGNkZDIyOTI4YjQxYWVjNTZkNTMzMzc0OWYxYjIwMTIxMDljZjUifX19"
    fun spawnAllNPCs() {
        for(npc in BurbNPC.entries) {
            spawnNPC(npc)
        }
    }

    fun spawnNPC(npc: BurbNPC) {
        val mannequin = npc.npcLocation.world.spawn(npc.npcLocation, Mannequin::class.java).apply {
            customName(Formatting.allTags.deserialize("<!i>${npc.npcNameColour}${npc.npcName}"))
            description = Formatting.allTags.deserialize(npc.npcDescription)
            isCustomNameVisible = true
            isInvulnerable = true
            isImmovable = true
            setGravity(false)
            persistentDataContainer.set(Keys.LOBBY_NPC, PersistentDataType.STRING, npc.toString())
            profile = ResolvableProfile.resolvableProfile().apply {
                addProperty(ProfileProperty("textures", npc.npcSkinTexture.ifEmpty { FALLBACK_TEXTURE }))
            }.build()
        }
    }

    fun clearNPCs() {
        for(world in Bukkit.getWorlds()) {
            for(mannequin in world.getEntitiesByClass(Mannequin::class.java)) {
                if(mannequin.persistentDataContainer.has(Keys.LOBBY_NPC)) {
                    mannequin.remove()
                }
            }
        }
    }
}

enum class BurbNPC(val npcName: String, val npcDescription: String, val npcNameColour: String, val npcLocation: Location, val npcSkinTexture: String, val onInteract: (Player, String, String) -> Unit = {_, _, _ -> }) {
    LOBBY_FISHING_ROD_GIVER(
        "Rodney Gibbs",
        "<i>Get yer fishin' rods!",
        "<#ffff00>",
        Location(Bukkit.getWorlds()[0], 92.5, 1.0, 84.5, 135f, 0f),
        "ewogICJ0aW1lc3RhbXAiIDogMTc2Nzk3NzM1ODk3NywKICAicHJvZmlsZUlkIiA6ICIyYjcyZWYyYWUzMmQ0Zjc1OGEyMThlMDI4MTViYmNjZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJ2b2xrb2RhZl82MyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zYzkzOGUwYmFjOGM1MjJkZDBiNmIwZWE1MDQ2YjQ3NDlmZjM0YWZiMzdhZTFiYzc0NTIzZGMxOThhYjllZmJkIgogICAgfQogIH0KfQ==",
        onInteract = { player, npcName, npcNameColour ->
            player.sendMessage(Formatting.allTags.deserialize("<b>${npcNameColour}${npcName}</b><white>: Ready to catch some fishies me hearty?"))
            player.playSound(Sounds.Misc.NPC_INTERACT)
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
            player.playSound(Sounds.Misc.NPC_INTERACT)
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
            player.playSound(Sounds.Misc.NPC_INTERACT)
        }
    )
}