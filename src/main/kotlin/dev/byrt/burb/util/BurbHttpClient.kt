package dev.byrt.burb.util

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.defaultRequest
import kotlinx.serialization.json.Json

/**
 * A globally reusable HTTP client.
 */
val BurbHttpClient = HttpClient(CIO) {
    defaultRequest {
        headers.append("User-Agent", "Burb")
    }
}

val BurbJson = Json {
    ignoreUnknownKeys = true
}