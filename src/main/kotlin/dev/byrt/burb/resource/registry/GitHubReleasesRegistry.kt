package dev.byrt.burb.resource.registry

import dev.byrt.burb.resource.RemotePack
import dev.byrt.burb.util.BurbHttpClient
import dev.byrt.burb.util.BurbJson
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import java.io.IOException
import java.net.URI
import java.net.URL

/**
 * A GitHub release.
 * See https://docs.github.com/en/rest/releases/releases?apiVersion=2022-11-28#get-the-latest-release
 */
@Serializable
private data class GithubRelease(
    @SerialName("tag_name") val tagName: String,
    val assets: List<Asset>,
) {
    @Serializable
    data class Asset(
        val name: String,
        @SerialName("browser_download_url") val browserDownloadUrl: String,
    )
}

/**
 * A resource pack registry that downloads the latest release, ignoring the provided tag.
 *
 * This is a temporary implementation that works with how the RP is currently distributed.
 * We should probably replace this with an S3 bucket or something.
 */
class GitHubReleasesRegistry(
    /**
     * The path to the GitHub repository, i.e. ByrtZ/BurbResourcePack.
     */
    public val repo: String,
) : ResourcePackRegistry {

    private companion object {
        private const val GITHUB_API = "https://api.github.com"
    }

    init {
        require(repo.matches(Regex("^[A-Za-z0-9_-]+/[A-Za-z0-9_-]+$"))) { "Invalid repo $repo, must be in the format owner/repo" }
    }

    override suspend fun fetchLatestForRef(tag: String): RemotePack? {
        val req = BurbHttpClient.get("$GITHUB_API/repos/$repo/releases/latest") {
            accept(ContentType.Application.Json)
        }

        val release = when {
            req.status == HttpStatusCode.NotFound -> return null
            !req.status.isSuccess() -> throw IOException(req.status.toString())
            else -> BurbJson.decodeFromString<GithubRelease>(req.bodyAsText())
        }

        require(release.assets.size == 1) { "Release ${release.tagName} has ${release.assets.size} assets, expected 1" }

        return RemotePack(
            release.tagName,
            release.tagName,
            URI(release.assets.single().browserDownloadUrl).toURL(),
            null, // We only have SHA-256, not SHA-1
        )
    }
}