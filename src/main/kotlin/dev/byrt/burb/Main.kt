package dev.byrt.burb

import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    override fun onEnable() {
        // Plugin startup logic
        logger.info("Starting Burb plugin...")
    }

    override fun onDisable() {
        // Plugin shutdown logic
        logger.info("Cleaning up Burb plugin...")
    }
}
