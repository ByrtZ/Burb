package dev.byrt.burb.text

import dev.byrt.burb.resource.ResourcePackChangedEvent
import dev.byrt.burb.text.Formatting.BURB_FONT
import dev.byrt.burb.text.Formatting.GLYPH_FONT
import me.lucyydotp.tinsel.Tinsel
import me.lucyydotp.tinsel.font.FontFamily
import me.lucyydotp.tinsel.font.OffsetMap
import me.lucyydotp.tinsel.font.Spacing
import net.kyori.adventure.key.Key
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object TextFormatter : Listener {
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
}