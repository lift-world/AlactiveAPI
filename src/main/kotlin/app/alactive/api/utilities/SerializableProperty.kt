package app.alactive.api.utilities

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.neo4j.ogm.typeconversion.AttributeConverter

typealias SerializableConverter<T> = AttributeConverter<T, String>

inline fun <reified T : Any> buildSerializableConverter(): SerializableConverter<T> =
    object : SerializableConverter<T> {
        override fun toGraphProperty(value: T?): String? = value?.let { Json.encodeToString<T>(it) }

        override fun toEntityAttribute(value: String?): T? = value?.let { Json.decodeFromString<T>(it) }
    }

class InstantConverter : SerializableConverter<Instant> by buildSerializableConverter()

class LocalDateConverter : SerializableConverter<LocalDate> by buildSerializableConverter()
