package dev.byrt.burb.chat

import dev.byrt.burb.chat.Formatting.allTags
import dev.byrt.burb.chat.Formatting.restrictedTags
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.util.Noxesium
import io.papermc.paper.chat.ChatRenderer

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer

import org.bukkit.Bukkit
import org.bukkit.entity.Player

object ChatUtility {
    /** Sends a message to the specified audience. **/
    fun messageAudience(recipient: Audience, message: String, restricted: Boolean, vararg placeholders: TagResolver) {
        val resolvers = mutableListOf<TagResolver>()
        for(p in placeholders) {
            resolvers.add(p)
        }

        recipient.sendMessage(formatMessage(message, restricted, TagResolver.resolver(resolvers)))
    }

    /** Formats a message, which can produce different results depending on if restricted or not. **/
    fun formatMessage(message: String, restricted: Boolean, vararg placeholders: TagResolver): Component {
        val resolvers = mutableListOf<TagResolver>()
        for(p in placeholders) {
            resolvers.add(p)
        }

        return if (restricted) {
            restrictedTags.deserialize(message, TagResolver.resolver(resolvers))
        } else {
            allTags.deserialize(message, TagResolver.resolver(resolvers))
        }
    }

    /** Sends a message to the admin channel which includes all online admins. **/
    fun broadcastAdmin(rawMessage: String, isSilent: Boolean) {
        val admin = Audience.audience(Bukkit.getOnlinePlayers())
            .filterAudience { (it as Player).hasPermission("burb.group.admin") }
        admin.sendMessage(
            allTags.deserialize("<prefix:admin>: $rawMessage")
        )
        if(!isSilent) {
            admin.playSound(Sounds.Misc.ADMIN_MESSAGE)
        }
    }

    /** Sends a message to the dev channel which includes all online devs. **/
    fun broadcastDev(rawMessage: String, isSilent: Boolean) {
        val dev = Audience.audience(Bukkit.getOnlinePlayers())
            .filterAudience { (it as Player).hasPermission("burb.group.dev") }
        dev.sendMessage(
            allTags.deserialize("<prefix:dev>: $rawMessage")
        )
        if(!isSilent) {
            dev.playSound(Sounds.Misc.ADMIN_MESSAGE)
        }
    }
}

object GlobalRenderer : ChatRenderer {
    override fun render(source: Player, sourceDisplayName: Component, message: Component, viewer: Audience): Component {
        val playerHead = Noxesium.buildSkullComponent(source.uniqueId, false, 0, 0, 1.0f)
        val plainMessage = PlainTextComponentSerializer.plainText().serialize(message)
        return if(source.hasPermission("burb.group.admin")) {
            playerHead
                .append(allTags.deserialize("<prefix:admin> <dark_red>${source.name}<reset>: $plainMessage"))
        } else {
            playerHead
                .append(allTags.deserialize("<prefix:spectator> <white>${source.name}<reset>: $plainMessage"))
        }
    }
}