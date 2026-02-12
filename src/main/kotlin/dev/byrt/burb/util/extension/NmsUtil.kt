package dev.byrt.burb.util.extension

import net.minecraft.server.level.ServerPlayer
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player

/**
 * The player's NMS [ServerPlayer].
 */
val Player.nms: ServerPlayer get() = (this as CraftPlayer).handle