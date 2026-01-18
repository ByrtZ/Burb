package dev.byrt.burb.text

import dev.byrt.burb.team.Teams
import net.kyori.adventure.key.Key

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags

object Formatting {
    val BURB_FONT = Key.key("burb", "font")
    val GLYPH_FONT = Key.key("burb", "glyph")

    /**
     * Creates a new text component with the glyph font.
     */
    fun glyph(value: String) = Component.text(value).font(GLYPH_FONT).color(NamedTextColor.WHITE)

    /** Prefix enum for allowing MiniMessage usage of the <prefix:NAME> tag in messages. **/
    enum class Prefix(val prefixName: String, val value: String) {
        NO_PREFIX("", ""),
        DEV_PREFIX("dev", "\uD001"),
        ADMIN_PREFIX("admin", "\uD002"),
        SPECTATOR_PREFIX("spectator", "\uD003"),
        WARNING_PREFIX("warning", "⚠"),
        SKULL_PREFIX("skull", "☠"),
        LOCK_PREFIX("locked", "\uD83D\uDD12"),
        UNLOCKED_PREFIX("unlocked", "\uD83D\uDD13");

        companion object {
            fun ofName(str : String): Prefix {
                for(p in entries) {
                    if (p.prefixName == str) return p
                }
                return NO_PREFIX
            }
        }
    }

    private val BURB_COLOUR = TagResolver.resolver("burbcolour", Tag.styling(TextColor.color(34, 224, 97)))
    private val PLANTS_COLOUR = TagResolver.resolver("plantscolour", Tag.styling(Teams.PLANTS.teamHexColour))
    private val ZOMBIES_COLOUR = TagResolver.resolver("zombiescolour", Tag.styling(Teams.ZOMBIES.teamHexColour))
    private val SPECTATOR_COLOUR = TagResolver.resolver("speccolour", Tag.styling(Teams.SPECTATOR.teamHexColour))
    private val NOTIFICATION_COLOUR = TagResolver.resolver("notifcolour", Tag.styling(TextColor.color(219, 0, 96)))

    val allTags = MiniMessage.builder()
        .tags(
            TagResolver.builder()
                .resolver(StandardTags.defaults())
                .resolver(BURB_COLOUR)
                .resolver(PLANTS_COLOUR)
                .resolver(ZOMBIES_COLOUR)
                .resolver(SPECTATOR_COLOUR)
                .resolver(NOTIFICATION_COLOUR)
                .resolver(prefix())
                .build()
        )
        .build()

    val restrictedTags = MiniMessage.builder()
        .tags(
            TagResolver.builder()
                .resolver(StandardTags.color())
                .resolver(StandardTags.decorations())
                .resolver(StandardTags.reset())
                .resolver(BURB_COLOUR)
                .resolver(PLANTS_COLOUR)
                .resolver(ZOMBIES_COLOUR)
                .resolver(SPECTATOR_COLOUR)
                .resolver(NOTIFICATION_COLOUR)
                .build()
        )
        .build()

    /** Builds a prefix tag. **/
    private fun prefix() : TagResolver {
        return TagResolver.resolver("prefix") { args, _ ->
            val prefixName = args.popOr("Name not supplied.")
            Tag.selfClosingInserting(
                Component.text(Prefix.ofName(prefixName.toString()).value).font(GLYPH_FONT)
            )
        }
    }
}