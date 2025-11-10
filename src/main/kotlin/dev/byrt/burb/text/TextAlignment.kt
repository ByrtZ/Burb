package dev.byrt.burb.text

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer

object TextAlignment {
    private const val FONT_WIDTH = 5
    private const val NUMBER_WIDTH = 4
    private const val NUMBER_WIDTH_SMALL = 3
    private const val SPACE_WIDTH = 1
    private const val BACKGROUND_WIDTH = 133
    private const val LETTER_SPACING = 1
    private const val BACKGROUND_GLYPH = "\uD011"

    fun centreBossBarText(text: String): Component {
        val plainText = PlainTextComponentSerializer.plainText().serialize(Formatting.allTags.deserialize(text))

        val textWidth = plainText.burbFontTextSize()
        val shift = (BACKGROUND_WIDTH + textWidth) / 2

        val textSpaceTag = "<translate:space.-${shift}>"
        val backgroundSpaceTag = "<translate:space.-${(BACKGROUND_WIDTH - textWidth) / 2}>"

        val component = Formatting.allTags.deserialize("<!shadow>$backgroundSpaceTag$BACKGROUND_GLYPH$textSpaceTag</!shadow>${ChatUtility.BURB_FONT_TAG}$text")
        return component
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
        val width = if(isNegative) {
            (-text.burbFontTextSize() / 2) - text.burbFontTextSize()
        } else {
            (text.burbFontTextSize() / 2) - text.burbFontTextSize()
        }

        return "<translate:space.${width}>"
    }

    private fun String.burbFontTextSize(): Int {
        return this.sumOf { ch -> if(ch in listOf(' ')) SPACE_WIDTH else if(ch in listOf(':', '|', '.', ',', ';')) SPACE_WIDTH + LETTER_SPACING else if(ch in listOf('0', '2', '3', '4', '5', '6', '7', '8', '9')) NUMBER_WIDTH + LETTER_SPACING else if(ch in listOf('1')) NUMBER_WIDTH_SMALL + LETTER_SPACING else FONT_WIDTH + LETTER_SPACING } - 1
    }
}