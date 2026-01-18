package dev.byrt.burb.util

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val BurbJson = Json {
    ignoreUnknownKeys = true
}

/**
 * A globally reusable HTTP client.
 */
val BurbHttpClient = HttpClient(CIO) {
    defaultRequest {
        headers.append("User-Agent", "Burb")
    }

    install(ContentNegotiation) {
        json(BurbJson)
    }
}