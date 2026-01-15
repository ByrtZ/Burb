package dev.byrt.burb.resource

import dev.byrt.burb.resource.registry.ResourcePackRegistry
import dev.byrt.burb.util.BurbHttpClient
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bukkit.event.Listener
import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.security.DigestOutputStream
import java.security.MessageDigest
import kotlin.io.path.*

/**
 * Loads resource packs from a remote server, maintaining a local copy and sending them out to clients.
 */
class ResourcePackLoader(
    private val registry: ResourcePackRegistry,
    private val localStorageDir: Path,
) : Listener {

    private companion object {
        private val logger = LoggerFactory.getLogger(ResourcePackLoader::class.java)
        private val tmpdir = System.getProperty("java.io.tmpdir")
    }

    private var tag = "latest"
    var currentPack: RemotePack? = null
        private set(value) {
            field = value
            logger.info("New active pack: ${value?.id}")
        }

    // Only allow one reload at a time
    private val reloadMutex = Mutex()

    init {
        // FIXME(lucy): is blocking here the best idea?
        runBlocking {
            loadPack(tag)
        }
    }

    /**
     * Loads the pack from the registry.
     */
    private suspend fun loadPack(tag: String) = reloadMutex.withLock {
        // Get the pack info
        logger.info("Loading new pack for tag $tag")
        val newPack = registry.fetchLatestForRef(tag) ?: throw IllegalStateException("No pack for tag $tag")
        logger.info("Fetched remote pack ${newPack.id} for tag $tag")

        val localPackPath = localStorageDir.resolve("${newPack.id}.zip")
        val localHashPath = localStorageDir.resolve("${newPack.id}.zip.sha1")

        // Download the pack if needed
        if (!localPackPath.exists()) {
            val tmpfile = Path.of(tmpdir, "${newPack.id}.zip")
            val md = MessageDigest.getInstance("SHA-1")

            tmpfile.outputStream(
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            ).use { outStream ->
                BurbHttpClient
                    .get(newPack.url)
                    .bodyAsChannel()
                    .copyTo(DigestOutputStream(outStream, md))
            }

            logger.info("Downloading pack ${newPack.id} ${newPack.url}")

            val localHash = md.digest()
            if (newPack.hash != null && !newPack.hash.contentEquals(localHash)) {
                throw IllegalStateException("Downloaded pack hash does not match expected hash")
            }

            tmpfile.moveTo(localPackPath)
            localHashPath.writeBytes(localHash)

            currentPack = newPack.copy(hash = localHash)
            return@withLock
        }

        // The pack exists, so double-check the hash
        val hash = localHashPath.readBytes()
        if (newPack.hash != null && !newPack.hash.contentEquals(hash)) {
            throw IllegalStateException("Locally stored pack hash does not match expected hash")
        }
        currentPack = newPack.copy(hash = hash)
    }
}