package dev.byrt.burb

import com.noxcrew.interfaces.InterfacesListeners
import dev.byrt.burb.game.Game
import dev.byrt.burb.messenger.BrandMessenger

import io.papermc.paper.command.brigadier.CommandSourceStack

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

import org.incendo.cloud.annotations.AnnotationParser
import org.incendo.cloud.description.CommandDescription
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.PaperCommandManager
import org.incendo.cloud.processors.cache.SimpleCache
import org.incendo.cloud.processors.confirmation.ConfirmationConfiguration
import org.incendo.cloud.processors.confirmation.ConfirmationManager
import org.incendo.cloud.processors.confirmation.annotation.ConfirmationBuilderModifier

import org.reflections.Reflections

import java.lang.Exception
import java.time.Duration
import java.util.function.Consumer

@Suppress("unstableApiUsage")
class Main : JavaPlugin() {
    private lateinit var commandManager: PaperCommandManager<CommandSourceStack>
    private lateinit var annotationParser: AnnotationParser<CommandSourceStack>
    override fun onEnable() {
        logger.info("Starting Burb plugin...")
        Game.setup()
        setupCommands()
        setupEventListeners()
        setupConfigs()
        setupPluginMessageListener()
        InterfacesListeners.install(this)
    }

    override fun onDisable() {
        logger.info("Cleaning up Burb plugin...")
        Game.cleanup()
    }

    private fun setupCommands() {
        logger.info("Registering commands.")
        commandManager = PaperCommandManager.builder()
            .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
            .buildOnEnable(this)

        annotationParser = AnnotationParser(commandManager, CommandSourceStack::class.java)
        annotationParser.parseContainers()

        setupCommandConfirmation()
    }

    private fun setupCommandConfirmation() {
        logger.info("Setting up command confirmation.")
        ConfirmationBuilderModifier.install(annotationParser)

        val confirmationConfig = ConfirmationConfiguration.builder<CommandSourceStack>()
            .cache(SimpleCache.of())
            .noPendingCommandNotifier { css ->
                css.sender.sendMessage(
                    Component.text(
                        "You do not have any pending commands.",
                        NamedTextColor.RED
                    )
                ) }
            .confirmationRequiredNotifier { css, ctx ->
                css.sender.sendMessage(
                    Component.text("Confirm command ", NamedTextColor.RED).append(
                        Component.text("'/${ctx.commandContext()}' ", NamedTextColor.GREEN)
                    ).append(Component.text("by running ", NamedTextColor.RED)).append(
                        Component.text("'/confirm' ", NamedTextColor.YELLOW)
                    ).append(Component.text("to execute.", NamedTextColor.RED))
                ) }
            .expiration(Duration.ofSeconds(30))
            .build()

        val confirmationManager = ConfirmationManager.confirmationManager(confirmationConfig)
        commandManager.registerCommandPostProcessor(confirmationManager.createPostprocessor())

        commandManager.command(
            commandManager.commandBuilder("confirm")
                .handler(confirmationManager.createExecutionHandler())
                .commandDescription(CommandDescription.commandDescription("Confirm a pending command."))
                .permission("burb.confirm")
                .build()
        )
    }

    private fun setupEventListeners() {
        logger.info("Registering events.")
        val reflections = Reflections("dev.byrt.burb.event")
        val listeners = reflections.getSubTypesOf(Listener::class.java)

        listeners.forEach(Consumer { listener : Class<out Listener> ->
                try {
                    val instance = listener.getConstructor().newInstance()
                    server.pluginManager.registerEvents(instance, this)
                } catch (e : Exception) {
                    e.printStackTrace()
                }
            }
        )
    }

    private fun setupPluginMessageListener() {
        logger.info("Registering plugin messengers.")
        messenger.registerIncomingPluginChannel(this, "minecraft:brand", BrandMessenger())
    }

    private fun setupConfigs() {
        logger.info("Setting up configurations.")
    }
}

val plugin = Bukkit.getPluginManager().getPlugin("Burb")!!
val logger = plugin.logger
val messenger = Bukkit.getMessenger()