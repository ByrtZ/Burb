package dev.byrt.burb.text

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.translation.MiniMessageTranslator
import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor
import org.yaml.snakeyaml.error.YAMLException
import java.io.InputStream
import java.util.Locale

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
    private data class LangFile(
        val tags: Map<String, String>,
        val messages: Map<String, String>
    )

    // this class is a bit weird-looking for snakeyaml,
    // which needs writable properties and a no-args constructor (hence the defaults)
    data class RawLangFile(
        @Suppress("PropertyName") var message_prefix: String = "",
        var tags: Map<String, String> = emptyMap(),
        var messages: Map<String, Any> = emptyMap(),
    )

    private companion object {
        private fun MutableMap<String, String>.populateLangMap(parent: String?, value: Map<String, Any>) {
            value.forEach { (key, value) ->
                val fullKey = if (parent == null) key else "$parent.$key"
                when (value) {
                    is String, is Number -> this[fullKey] = value.toString()
                    is Map<*, *> -> @Suppress("UNCHECKED_CAST") populateLangMap(fullKey, value as Map<String, Any>)
                    else -> throw YAMLException("$fullKey: Invalid value '$value'")
                }
            }
        }

        private fun createLangFile(stream: InputStream): LangFile {
            val yaml = Yaml(
                CustomClassLoaderConstructor(LangFile::class.java.classLoader, LoaderOptions())
            ).loadAs(stream, RawLangFile::class.java)

            return LangFile(
                yaml.tags,
                buildMap { populateLangMap(yaml.message_prefix.takeIf { it.isNotBlank() }, yaml.messages) }
            )
        }
    }

    init {
        dev.byrt.burb.logger.info("Loaded translations (${langFile.tags.size} tags, ${langFile.messages.size} messages)")
    }

    constructor() : this(createLangFile(BurbTranslator::class.java.getResourceAsStream("/lang/en_US.yml")))

    private val key = Key.key("burb", "translator")
    override fun name(): Key = key

    override fun getMiniMessageString(key: String, locale: Locale): String? = langFile.messages[key]
}