package app.alactive.api.services

import app.alactive.api.AlactiveConfiguration
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.net.URL

/**
 * All possible prefixes for Alactive IDs.
 */
enum class IDPrefix(val value: String) {
    VIDEO("video"),
    IMAGE("image"),
    AVATAR("avatar"),
    EVENT("event"),
    VENUE("venue"),
}

/**
 * Provides access to the ID generator, as well as conversion from Alactive identifiers to CDN URIs.
 */
@Service
class IDService(config: AlactiveConfiguration) {
    /**
     * The base-URL for our CDN, pre-calculated to verify its correctness.
     */
    private val cdnURL = URL(config.endpoint.s3Uri)

    /**
     * The web client used to access the ID generator server.
     */
    private val client = WebClient.create(config.endpoint.idGeneratorUri)

    /**
     * Generates an ID using the remote ID generator server.
     *
     * @param prefix The prefix for the new ID.
     * @return The new ID, as a String.
     */
    suspend fun generateID(prefix: IDPrefix): String = client
        .get().uri("/generate?prefix=${prefix.value}")
        .retrieve().awaitBody<String>()

    /**
     * Converts an Alactive ID into a URI pointing to our CDN.
     *
     * @param id The ID to be converted.
     * @return The resulting URI, as a String.
     */
    fun idToURI(id: String): String {
        val prefix = IDPrefix.valueOf(id.substringBefore('-').uppercase())
        return URL(cdnURL, "$prefix/$id").toString()
    }
}
