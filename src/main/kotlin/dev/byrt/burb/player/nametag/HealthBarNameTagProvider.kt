package dev.byrt.burb.player.nametag

import dev.byrt.burb.game.GameManager
import dev.byrt.burb.player.displayname.DisplayNameChangeEvent
import dev.byrt.burb.text.Formatting
import dev.byrt.burb.text.TextAlignment
import me.lucyydotp.tinsel.font.Spacing
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Color
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Display
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityRegainHealthEvent
import kotlin.math.roundToInt

class HealthBarNameTagProvider : NameTagProvider() {
    private companion object {
        private const val BAR_WIDTH = 60

        private const val FILL_END_CHAR = '\uE001'
        private const val FILL_CHAR = '\uE002'
        private const val FILL_TICK_CENTER_CHAR = '\uE003'
        private const val FILL_TICK_LARGE_CHAR = '\uE004'
        private const val FILL_TICK_SMALL_CHAR = '\uE005'
        private const val SPACE_CHAR = '\uF8FF'
    }

    override val lines = 2
    override fun setUpForPlayer(player: Player, nametag: NameTag) {
        nametag[0] = drawHealthbar(player)
        nametag[0].backgroundColor = Color.fromARGB(0)

        nametag[1] = player.displayName()
    }

    private fun buildString(startingIndex: Int, size: Int) = buildString {
        (startingIndex..<startingIndex + size).forEach { i ->
            append(
                when {
                    i == 0 || i == BAR_WIDTH - 1 -> FILL_END_CHAR
                    i == BAR_WIDTH / 2 -> FILL_TICK_CENTER_CHAR
                    i % (BAR_WIDTH / 5) == 0 -> FILL_TICK_LARGE_CHAR
                    i % (BAR_WIDTH / 10) == 0 -> FILL_TICK_SMALL_CHAR
                    else -> FILL_CHAR
                }
            )
            append(SPACE_CHAR)
        }
    }

    private fun drawHealthbar(player: Player, health: Double = player.health): Component {
        val team = GameManager.teams.getTeam(player.uniqueId) ?: return Component.empty()
        val out = Component.text().font(Key.key("burb", "healthbar"))
        val healthPct = health / player.getAttribute(Attribute.MAX_HEALTH)!!.value
        val fillAmount = ((BAR_WIDTH * healthPct).roundToInt() + 1).coerceAtMost(BAR_WIDTH)
        out.append(
            Component.text(buildString(0, fillAmount), team.textColour)
        )
        out.append(
            Component.text(buildString(fillAmount, BAR_WIDTH - fillAmount), NamedTextColor.DARK_GRAY)
        )

        out.append(Spacing.spacing(-BAR_WIDTH - 1))
        out.append(Component.text("\uE000"))

        val healthText = Component.text(health.roundToInt().toString()).font(Formatting.BURB_FONT)
        val healthOffset = (TextAlignment.tinsel.textWidthMeasurer().measure(healthText) / 2f).roundToInt()

        out.append(Spacing.spacing(-(healthOffset + 7)))
        out.append(healthText)
        out.append(Spacing.spacing(7 - healthOffset))
        return out.build()
    }

    private fun updateHealthbar(player: Player, health: Double = player.health) {
        nametags[player.uniqueId]?.set(0, drawHealthbar(player, health))
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private fun onDamage(e: EntityDamageEvent) {
        val player = e.entity as? Player ?: return
        updateHealthbar(player, player.health - e.finalDamage)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private fun onHeal(e: EntityRegainHealthEvent) {
        val player = e.entity as? Player ?: return
        updateHealthbar(player, player.health + e.amount)
    }

    @EventHandler
    private fun onDisplayNameChange(e: DisplayNameChangeEvent) {
        val nametag = nametags[e.player.uniqueId] ?: return
        nametag[1] = e.player.displayName()
    }
}