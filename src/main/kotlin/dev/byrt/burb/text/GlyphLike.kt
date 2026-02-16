package dev.byrt.burb.text

import dev.byrt.burb.text.Formatting.GLYPH_FONT

/**
 * An object that holds a glyph.
 */
interface GlyphLike {
    /**
     * The raw glyph string.
     */
    val rawGlyph: String

    /**
     * Gets the glyph as a component, formatted with the glyph font.
     */
    fun glyphAsComponent() = Formatting.glyph(rawGlyph)

    /**
     * Gets the glyph as a MiniMessage tag, formatted with the glyph font.
     */
    fun asMiniMesssage() = "<!shadow><font:$GLYPH_FONT>$rawGlyph</font></!shadow>"
}

