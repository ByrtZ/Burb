package dev.byrt.burb.text

import dev.byrt.burb.resource.ResourcePackChangedEvent
import dev.byrt.burb.text.Formatting.BURB_FONT
import dev.byrt.burb.text.Formatting.GLYPH_FONT
import me.lucyydotp.tinsel.Tinsel
import me.lucyydotp.tinsel.font.FontFamily
import me.lucyydotp.tinsel.font.OffsetMap
import me.lucyydotp.tinsel.font.Spacing
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.ShadowColor
import net.kyori.adventure.text.format.Style
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import kotlin.collections.set

object TextAlignment : Listener {
    private const val FONT_WIDTH = 5
    private const val NUMBER_WIDTH = 4
    private const val NUMBER_WIDTH_SMALL = 3
    private const val SPACE_WIDTH = 1
    private const val BACKGROUND_WIDTH = 133
    private const val LETTER_SPACING = 1
    private const val BACKGROUND_GLYPH = "\uD011"

    private var _tinsel: Tinsel? = null
    val tinsel get() = checkNotNull(_tinsel) { "Tinsel cannot be used yet as resource packs have not loaded" }

    @EventHandler
    private fun packChanged(e: ResourcePackChangedEvent) {
        _tinsel = with(Tinsel.builder()) {
            withFont(
                FontFamily.vanillaWithOffsets(
                    OffsetMap.offsets(Key.key("tinsel", "default"), 8, 0, -2, -12).also {
                        it[0] = Key.key("minecraft", "default")
                    }
                )
            )
            withFont(Spacing.font())

            e.newPack?.path?.let { pack ->
                withFonts(
                    FontFamily.fromResourcePack(pack)
                        .add(BURB_FONT, OffsetMap.offsets(BURB_FONT, 8, 0, -2, -12).also {
                            it[0] = BURB_FONT
                        })
                        .add(GLYPH_FONT)
                        .build()
                )
            }

            build()
        }
    }

    fun centreBossBarText(text: String): Component = tinsel.draw(BACKGROUND_WIDTH, Style.empty()) {
        it.drawAligned(Formatting.glyph(BACKGROUND_GLYPH).shadowColor(ShadowColor.none()), 0.5f)
        it.drawAligned(Formatting.allTags.deserialize(text).font(BURB_FONT), 0.5f)
    }

    fun centreActionBarText(topText: String, bottomText: String): Component {
        val prependComponent = Formatting.allTags.deserialize(
            buildString {
                appendSpaceAndBackground(topText + bottomText)
            }
        )
        val backgroundComponent = Formatting.allTags.deserialize(
            buildString {
                append("<translate:space.-${BACKGROUND_WIDTH / 2}>")
                append("<!shadow>$BACKGROUND_GLYPH</!shadow>")
                append("<translate:space.${BACKGROUND_WIDTH / 2}>")
            }
        )
        val topComponent = Formatting.allTags.deserialize(
            buildString {
                appendSpace(topText, isNegative = true)
                append("${ChatUtility.BURB_FONT_LOW_TAG}$topText")
                appendSpace(topText, isNegative = false)
            }
        )
        val bottomComponent = Formatting.allTags.deserialize(
            buildString {
                appendSpace(bottomText, isNegative = true)
                append("${ChatUtility.BURB_FONT_LOWER_TAG}$bottomText")
                appendSpace(bottomText, isNegative = false)
            }
        )
        return prependComponent.append(backgroundComponent.append(topComponent.append(bottomComponent)))
    }

    private fun appendSpaceAndBackground(text: String): String {
        return "<translate:space.-${text.burbFontTextSize() + BACKGROUND_WIDTH / 2}>"
    }

    private fun appendSpace(text: String, isNegative: Boolean): String {
        val width = if (isNegative) {
            (-text.burbFontTextSize() / 2) - text.burbFontTextSize()
        } else {
            (text.burbFontTextSize() / 2) - text.burbFontTextSize()
        }

        return "<translate:space.${width}>"
    }

    private fun String.burbFontTextSize(): Int {
        return this.sumOf { ch ->
            if (ch in listOf(' ')) SPACE_WIDTH else if (ch in listOf(
                    ':',
                    '|',
                    '.',
                    ',',
                    ';',
                    "'"
                )
            ) SPACE_WIDTH + LETTER_SPACING else if (ch in listOf(
                    '0',
                    '2',
                    '3',
                    '4',
                    '5',
                    '6',
                    '7',
                    '8',
                    '9'
                )
            ) NUMBER_WIDTH + LETTER_SPACING else if (ch in listOf('1')) NUMBER_WIDTH_SMALL + LETTER_SPACING else FONT_WIDTH + LETTER_SPACING
        } - 1
    }
}