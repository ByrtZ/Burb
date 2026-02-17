package dev.byrt.burb.player.cosmetics

import net.kyori.adventure.text.Component

enum class BurbCosmeticObtainmentSource(val obtainmentSourceComponent: Component) {
    STICKER_SHOP(Component.translatable("burb.cosmetic.obtainment.sticker_shop")),
    FOOTBALL_GAME(Component.translatable("burb.cosmetic.obtainment.football_game")),
    NPC_SHOP(Component.translatable("burb.cosmetic.obtainment.npc_shop")),
    HIDDEN(Component.translatable("burb.cosmetic.obtainment.hidden")),
    GNOME_TASK(Component.translatable("burb.cosmetic.obtainment.gnome_task")),
    ERROR(Component.translatable("burb.cosmetic.obtainment.error"));
}