package dev.byrt.burb.lobby.npc

import dev.byrt.burb.library.Sounds
import net.kyori.adventure.sound.Sound

enum class BurbNPCSoundSet(val baseSound: Sound, val hitSound: Sound) {
    GENERIC_NPC_DIALOGUE(Sounds.Misc.NPC_INTERACT, Sounds.Misc.NPC_INTERACT_HIT),
    ALTERNATE_NPC_DIALOGUE(Sounds.Misc.NPC_ALT_INTERACT, Sounds.Misc.NPC_ALT_INTERACT_HIT),
}