package dev.byrt.burb.util

import dev.byrt.burb.plugin
import org.bukkit.Bukkit

/**
 * Runs a task on the main thread.
 */
public fun onMainThread(runnable: () -> Unit) {
    if (Bukkit.isPrimaryThread()) {
        runnable()
    } else {
        Bukkit.getScheduler().runTask(plugin, runnable)
    }
}