package dev.byrt.burb.event

import dev.byrt.burb.library.Sounds
import dev.byrt.burb.lobby.fishing.Fishing
import dev.byrt.burb.text.Formatting
import org.bukkit.Tag
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent

@Suppress("unused", "unstableApiUsage")
class PlayerFish: Listener {
    @EventHandler
    fun onPlayerFish(event: PlayerFishEvent) {
        if(event.hook.hookedEntity != null) {
            val hookedEntity = event.hook.hookedEntity!!
            if(hookedEntity !is Item) {
                event.hook.remove()
                event.isCancelled = true
                return
            }
        }

        event.expToDrop = 0

        if(event.state !== PlayerFishEvent.State.CAUGHT_FISH) return
        val item = event.caught as Item

        if(Tag.ITEMS_FISHES.isTagged(item.itemStack.type)) {
            Fishing.catchFish(event.player, item, (event.caught as Item).location, null, null)
        } else {
            event.player.playSound(Sounds.Misc.INTERFACE_ERROR)
            event.player.sendActionBar(Formatting.allTags.deserialize("<red>Your fishing line snapped!"))
            item.remove()
        }
    }
}