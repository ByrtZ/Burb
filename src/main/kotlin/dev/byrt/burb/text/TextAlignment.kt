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
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.format.ShadowColor
import net.kyori.adventure.text.format.Style
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import kotlin.collections.set

object TextAlignment : Listener {
    private const val BACKGROUND_WIDTH = 133
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

    fun centreBossBarText(component: Component): Component = tinsel.draw(BACKGROUND_WIDTH, Style.empty()) {
        it.drawAligned(Formatting.glyph(BACKGROUND_GLYPH).shadowColor(ShadowColor.none()), 0.5f)
        it.drawAligned(component.font(BURB_FONT), 0.5f)
    }

    fun centreBossBarText(text: String): Component = centreBossBarText(Formatting.allTags.deserialize(text))
}