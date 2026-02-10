package dev.byrt.burb.game.events

import dev.byrt.burb.game.GameManager
import dev.byrt.burb.game.GameState
import dev.byrt.burb.game.Timer
import dev.byrt.burb.game.visual.GameDayTime
import dev.byrt.burb.game.visual.GameVisuals
import dev.byrt.burb.library.Translation
import dev.byrt.burb.music.Jukebox
import dev.byrt.burb.music.Music
import dev.byrt.burb.plugin
import dev.byrt.burb.text.ChatUtility
import dev.byrt.burb.text.Formatting
import dev.byrt.burb.text.TextAlignment

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.bossbar.BossBar

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable

object SpecialEvents {
    private var currentEvent: SpecialEvent? = null
    private const val EVENT_DURATION = 180

    fun rollSpecialEvent() {
        if(currentEvent == null && GameManager.getGameState() == GameState.IN_GAME && Timer.getTimer() >= 5 * 60 && (0..9).random() == 0 && Timer.getTimer() != GameManager.GameTime.IN_GAME_TIME) {
            startEvent(SpecialEvent.entries.random())
        }
    }

    fun startEvent(event: SpecialEvent) {
        if(currentEvent != null || GameManager.getGameState() != GameState.IN_GAME) return
        currentEvent = event
        Bukkit.broadcast(Formatting.allTags.deserialize("${Translation.Generic.ARROW_PREFIX}${event.eventNameFormatted}<reset>has begun!"))
        runEvent(event)
    }

    private fun runEvent(event: SpecialEvent) {
        val bossBar = BossBar.bossBar(
            Formatting.allTags.deserialize(""),
            0f,
            BossBar.Color.RED,
            BossBar.Overlay.PROGRESS
        )
        GameVisuals.setDayTime(GameDayTime.NIGHT)

        object : BukkitRunnable() {
            var ticks = 0
            var seconds = 0
            var transition = 0.0
            var isTransitionReverse = false
            override fun run() {
                if(ticks == 0 && seconds == 0) {
                    event.onStart(Bukkit.getOnlinePlayers())
                }
                if(ticks % 10 == 0) {
                    Bukkit.getOnlinePlayers().forEach { bossBar.addViewer(it) }
                }
                if(ticks % 2 == 0) {
                    // Calculate transition before string is updated
                    if(event != SpecialEvent.RANDOS_REVENGE) {
                        if(isTransitionReverse) transition -= 0.1 else transition += 0.1
                        if(transition < 0.0) {
                            transition = 0.0
                            isTransitionReverse = false
                        }
                        if(transition > 1.0) {
                            transition = 1.0
                            isTransitionReverse = true
                        }
                    } else {
                        transition++
                    }
                    bossBar.name(TextAlignment.centreBossBarText(event.bossBarString.replace("%s", "%02d:%02d".format((EVENT_DURATION - seconds) / 60, (EVENT_DURATION - seconds) % 60))
                        .replace("%f", "${if(event == SpecialEvent.RANDOS_REVENGE) transition.toInt() else transition}")
                    ))
                }

                event.onTick(Bukkit.getOnlinePlayers(), ticks, seconds)

                if(ticks == 0 && seconds >= EVENT_DURATION || GameManager.getGameState() != GameState.IN_GAME || Timer.getTimer() <= 120) {
                    Bukkit.broadcast(Formatting.allTags.deserialize("${Translation.Generic.ARROW_PREFIX}${event.eventNameFormatted}<reset>has ended."))
                    bossBar.removeViewer(Audience.audience(Bukkit.getOnlinePlayers()))
                    currentEvent = null
                    GameVisuals.setDayTime(GameDayTime.DAY)
                    cancel()
                }

                ticks++
                if (ticks >= 20) {
                    ticks = 0
                    seconds++
                }
            }
        }.runTaskTimer(plugin, 0L, 1L)
    }
    fun getCurrentEvent(): SpecialEvent? = currentEvent
    fun isEventRunning(): Boolean = currentEvent != null
}

enum class SpecialEvent(
    val eventName: String,
    val eventNameFormatted: String,
    val bossBarString: String,
    val onStart: (Collection<Player>) -> Unit = {},
    val onTick: (Collection<Player>, Int, Int) -> Unit = { _, _, _ -> }
) {
    TREASURE_TIME(
        "Treasure Time",
        "<gradient:gold:yellow:gold:yellow:gold>Treasure Time ",
        "${ChatUtility.BURB_FONT_TAG}<transition:gold:yellow:%f>TREASURE TIME<white>: %s",
        onStart = { players -> players.forEach { Jukebox.startMusicLoop(it, Music.TREASURE_TIME_LOW) } },
        onTick = { players, ticks, seconds -> if(ticks == 0 && seconds == 60) players.forEach { Jukebox.startMusicLoop(it, Music.TREASURE_TIME_HIGH) } }
    ),
    MOON_GRAVITY(
        "Moon Gravity",
        "<gradient:dark_purple:light_purple:dark_purple:light_purple:dark_purple>Moon Gravity ",
        "${ChatUtility.BURB_FONT_TAG}<transition:dark_purple:light_purple:%f>MOON GRAVITY<white>: %s",
        onStart = { players -> players.forEach { Jukebox.startMusicLoop(it, Music.LOBBY_UNDERWORLD) } },
        onTick = { players, ticks, _ ->
            if (ticks % 10 == 0) {
                players.forEach {
                    it.addPotionEffects(
                        listOf(
                            PotionEffect(PotionEffectType.JUMP_BOOST, 20, 7, false, false),
                            PotionEffect(PotionEffectType.SLOW_FALLING, 20, 0, false, false)
                        )
                    )
                }
            }
        }
    ),
    RANDOS_REVENGE(
        "Rando's Revenge",
        "<rainbow>Rando's Revenge ",
        "${ChatUtility.BURB_FONT_TAG}<rainbow:%f>RANDO'S REVENGE</rainbow>: %s",
        onStart = { players -> players.forEach { Jukebox.startMusicLoop(it, Music.RANDOS_REVENGE) } },
    ),
    VANQUISH_SHOWDOWN(
        "Showdown",
        "<gradient:dark_red:red:dark_red>Showdown ",
        "${ChatUtility.BURB_FONT_TAG}<transition:red:dark_red:%f>Showdown<white>: %s",
        onStart = { players -> players.forEach { Jukebox.startMusicLoop(it, Music.VANQUISH_SHOWDOWN) } },
    );
}