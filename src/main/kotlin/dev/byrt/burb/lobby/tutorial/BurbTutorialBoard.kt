package dev.byrt.burb.lobby.tutorial

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TranslatableComponent

import org.bukkit.Bukkit
import org.bukkit.Location

enum class BurbTutorialBoard(val boardName: String, val boardText: TranslatableComponent, var otherTexts: List<TranslatableComponent> = emptyList(), val boardLocation: Location) {
    SUBURBINATION_TUTORIAL("Suburbination", Component.translatable("burb.tutorial.suburbination"), otherTexts = emptyList(), Location(Bukkit.getWorlds()[0], -5.5, 0.5, 22.5, -140f, 0f)),
    EVENTS_TUTORIAL("Special Events", Component.translatable("burb.tutorial.special_events.generic"), otherTexts = listOf(
        Component.translatable("burb.tutorial.special_events.moon_gravity"),
        Component.translatable("burb.tutorial.special_events.randos_revenge"),
        Component.translatable("burb.tutorial.special_events.treasure_time"),
        Component.translatable("burb.tutorial.special_events.vanquish_showdown")
    ), Location(Bukkit.getWorlds()[0], -11.5, 0.5, 13.5, -90f, 0f)),
}