package dev.byrt.burb.text

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.translation.MiniMessageTranslator
import org.bukkit.configuration.file.YamlConstructor
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.error.YAMLException
import java.io.InputStream
import java.util.Locale

private data class LangFile(
    val tags: Map<String, String>,
    val messages: Map<String, String>
)

data class RawLangFile(
    val tags: Map<String, String>,
    val messages: Map<String, String>
)

private fun MutableMap<String, String>.populate(parent: String?, value: Map<String, Any>) {
    value.forEach { (key, value) ->
        val fullKey = if (parent == null) key else "$parent.$key"
        when (value) {
            is String, is Number -> this[fullKey] = value.toString()
            is Map<*, *> -> @Suppress("UNCHECKED_CAST") populate(fullKey, value as Map<String, Any>)
            else -> throw YAMLException("$fullKey: Invalid value '$value'")
        }
    }
}

private fun LangFile(stream: InputStream): LangFile {
    val yaml = Yaml().loadAs(stream, RawLangFile::class.java)

    return LangFile(
        yaml.tags,
        buildMap { populate(null, yaml.messages) }
    )
}

class BurbTranslator private constructor(private val langFile: LangFile) : MiniMessageTranslator(
    MiniMessage.builder()
        .tags(
            TagResolver.builder()
                .resolvers(Formatting.allTags.tags())
                .resolvers(langFile.tags.map { (tag, value) -> Placeholder.parsed(tag, value) })
                .build()
        )
        .build()

) {
    init {
        dev.byrt.burb.logger.info("Loaded translations (${langFile.tags.size} tags, ${langFile.messages.size} messages)")
    }

    constructor(): this(LangFile(BurbTranslator::class.java.getResourceAsStream("/lang/en_US.yml")))

    private val key = Key.key("burb", "translator")
    override fun name(): Key = key

    override fun getMiniMessageString(key: String, locale: Locale): String? {
        TODO("Not yet implemented")
    }
}