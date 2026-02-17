package dev.byrt.burb.player.cosmetics

import dev.byrt.burb.text.Formatting
import net.kyori.adventure.text.Component

enum class BurbCosmeticObtainmentSource(val obtainmentSourceComponent: Component) {
    STICKER_SHOP(Formatting.allTags.deserialize("<translate:burb.cosmetic.obtainment.sticker_shop>")),
    FOOTBALL_GAME(Formatting.allTags.deserialize("<translate:burb.cosmetic.obtainment.football_game>")),
    NPC_SHOP(Formatting.allTags.deserialize("<translate:burb.cosmetic.obtainment.npc_shop>")),
    HIDDEN(Formatting.allTags.deserialize("<translate:burb.cosmetic.obtainment.hidden>")),
    GNOME_TASK(Formatting.allTags.deserialize("<translate:burb.cosmetic.obtainment.gnome_task>")),
    ERROR(Formatting.allTags.deserialize("<translate:burb.cosmetic.obtainment.error>"));
}