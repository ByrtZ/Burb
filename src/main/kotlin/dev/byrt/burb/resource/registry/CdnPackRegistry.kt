package dev.byrt.burb.resource.registry

import dev.byrt.burb.resource.RemotePack
import dev.byrt.burb.util.BurbHttpClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import java.net.URI

@Serializable
private data class CdnPackMeta(
    val packHash: String,
    val commitId: String,
)

/**
 * A resource pack registry backed by a CDN. The CDN hosts both packs (packs/<sha1>.zip) and metadata
 * files (meta/<tag>.json) mapping tags to pack hashes.
 */
class CdnPackRegistry(
    private val cdnUrl: String,
) : ResourcePackRegistry {

    override suspend fun fetchLatestForRef(tag: String): RemotePack? {
        val meta = BurbHttpClient.get("$cdnUrl/meta/$tag.json").body<CdnPackMeta>()

        return RemotePack(
            tag,
            meta.packHash,
            URI("$cdnUrl/packs/${meta.packHash}.zip").toURL(),
            meta.packHash.hexToByteArray()
        )
    }
}