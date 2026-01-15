package dev.byrt.burb.resource

import java.net.URL
import java.nio.ByteBuffer

/**
 * A remote resource pack.
 */
data class RemotePack(
    /**
     * The pack's tag.
     *
     * Tags are docker-style labels attached to the latest version of a build for a particular channel.
     * Exactly what a tag corresponds to is implementation-specific per [dev.byrt.burb.resource.registry.ResourcePackRegistry].
     */
    val tag: String,

    /**
     * The pack's globally unique ID.
     * This can be used to determine when a [tag] has changed.
     */
    val id: String,

    /**
     * A public-facing URL that clients can use to download a pack.
     */
    val url: URL,

    /**
     * The pack's SHA1 hash, if known.
     */
    val hash: ByteArray?
)
