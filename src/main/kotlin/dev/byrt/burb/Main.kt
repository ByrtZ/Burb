package dev.byrt.burb

import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    override fun onEnable() {
        logger.info("Starting Burb plugin...")
    }

    override fun onDisable() {
        logger.info("Cleaning up Burb plugin...")
    }
}
