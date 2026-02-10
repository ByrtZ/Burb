package dev.byrt.burb

import com.github.benmanes.caffeine.cache.Caffeine
import com.noxcrew.interfaces.InterfacesListeners
import dev.byrt.burb.game.Game
import dev.byrt.burb.messenger.BrandMessenger
import dev.byrt.burb.resource.ResourcePackApplier
import dev.byrt.burb.resource.ResourcePackLoader
import dev.byrt.burb.resource.registry.CdnPackRegistry
import dev.byrt.burb.text.Formatting
import dev.byrt.burb.text.TextAlignment
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.cloud.annotations.AnnotationParser
import org.incendo.cloud.description.CommandDescription
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.kotlin.coroutines.annotations.installCoroutineSupport
import org.incendo.cloud.paper.LegacyPaperCommandManager
import org.incendo.cloud.processors.cache.CaffeineCache
import org.incendo.cloud.processors.confirmation.ConfirmationConfiguration
import org.incendo.cloud.processors.confirmation.ConfirmationManager
import org.incendo.cloud.processors.confirmation.annotation.ConfirmationBuilderModifier
import org.reflections.Reflections
import java.time.Duration
import java.util.function.Consumer
import kotlin.io.path.createDirectories

@Suppress("unused", "unstableApiUsage")
class Main : JavaPlugin() {
    private lateinit var commandManager: LegacyPaperCommandManager<CommandSender>
    private lateinit var annotationParser: AnnotationParser<CommandSender>

    lateinit var resourcePackLoader: ResourcePackLoader
        private set

    lateinit var resourcePackApplier: ResourcePackApplier
        private set

    override fun onEnable() {
        logger.info("Starting Burb plugin...")
        server.pluginManager.registerEvents(TextAlignment, this)
        resourcePackLoader = ResourcePackLoader(
            CdnPackRegistry("https://mc-rp.lucyydotp.me/burb"),
            dataPath.resolve("packs").createDirectories(),
            "master"
        )

        resourcePackApplier = ResourcePackApplier(resourcePackLoader)
        server.pluginManager.registerEvents(resourcePackApplier, this)

        Game.setup()
        setupCommands()
        setupEventListeners()
        setupConfigs()
        setupPluginMessageListener()
        InterfacesListeners.install(this)
    }

    override fun onDisable() {
        logger.info("Disabling Burb plugin and cleaning up...")
        Game.cleanup()
    }

    private fun setupCommands() {
        logger.info("Registering commands.")
        commandManager = LegacyPaperCommandManager.createNative(
            this,
            ExecutionCoordinator.simpleCoordinator()
        )
        annotationParser = AnnotationParser(commandManager, CommandSender::class.java)
            .installCoroutineSupport(onlyForSuspending = true)
        setupCommandConfirmation()
        annotationParser.parseContainers()
    }

    private fun setupCommandConfirmation() {
        logger.info("Setting up command confirmation.")
        ConfirmationBuilderModifier.install(annotationParser)

        val confirmationConfig = ConfirmationConfiguration.builder<CommandSender>()
            .cache(CaffeineCache.of(
                Caffeine.newBuilder().expireAfterWrite(Duration.ofSeconds(30)).build()
            ))
            .noPendingCommandNotifier { sender ->
                sender.sendMessage(
                    Formatting.allTags.deserialize("<red>You do not have any pending commands.")
                ) }
            .confirmationRequiredNotifier { sender, ctx ->
                sender.sendMessage(
                    Formatting.allTags.deserialize("<red><b><unicodeprefix:warning></b> This action is potentially disruptive!<newline>Confirm command <green>'/${ctx.commandContext().rawInput().input()}' <red>by running <yellow>'/confirm' <red>to execute.")
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

val plugin = Bukkit.getPluginManager().getPlugin("Burb")!! as Main
val logger = plugin.logger
val messenger = Bukkit.getMessenger()