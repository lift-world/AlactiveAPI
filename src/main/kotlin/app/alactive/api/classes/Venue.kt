package app.alactive.api.classes

import app.alactive.api.utilities.InstantConverter
import app.alactive.api.utilities.SerializableConverter
import app.alactive.api.utilities.buildSerializableConverter
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship
import org.neo4j.ogm.annotation.typeconversion.Convert

@NodeEntity
data class Venue(
    /**
     * Unique identifier.
     */
    @Id val id: String = "",
    /**
     * The user-facing name. Does not have to be unique.
     */
    val name: String = "",
    /**
     * Geographical latitude in degrees.
     */
    val latitude: Double = 0.0,
    /**
     * Geographical longitude in degrees.
     */
    val longitude: Double = 0.0,
    /**
     * The country this `Venue` is in.
     * **Standard**: ISO 3166-1 alpha-3 code.
     */
    val country: String = "",
    /**
     * City. Usually obtained from AWS Location Services.
     */
    val municipality: String = "",
    /**
     * Postcode. Usually obtained from AWS Location Services.
     */
    val postcode: String = "",
    /**
     * Rest of the address. This will usually be whatever the user inputs.
     */
    val address: String = "",
    /**
     * Avatar. Note that if none is present, one will be generated based on their name and/or some seeded colours.
     *
     * **Standard**: Alactive-Identifier, prefix “image”.
     */
    val avatar: String? = null,
    /**
     * The user-facing description.
     */
    val description: String? = null,
    /**
     * The media files. Ordering is respected.
     */
    val media: List<String> = listOf(),
    /**
     * The specific media files referring to the Highlights of this `Venue`.
     * These are organized in groups, each with a title and a front cover. Each group has individual video files.
     */
    @Convert(HighlightGroupConverter::class)
    val highlights: List<HighlightGroup> = listOf(),
    /**
     * The set of boolean information characterizing this `Venue`. (i.e. "dancers", "bottleShow", etc.)
     */
    val tags: List<String> = listOf(),
    /**
     * The maximum declared capacity of this `Venue`.
     */
    val capacity: Int? = null,
    /**
     * The general descriptive type of this `Venue`.
     */
    val type: String? = null,
    /**
     * When this `Venue` was created.
     */
    @Convert(InstantConverter::class)
    val created: Instant = Instant.DISTANT_PAST,
    /**
     * When an attribute of this `Venue` was last modified.
     */
    @Convert(InstantConverter::class)
    val lastModified: Instant = Instant.DISTANT_PAST,
) {
    @Relationship(type = "HOSTED_BY", direction = Relationship.Direction.INCOMING)
    var hosting: MutableSet<Event> = mutableSetOf()

    //    /**
    //     * The `Host` which manages this `Venue`. If none is present, that means we (Alactive) manually manage this `Venue`
    //     * listing.
    //     */
    //    val manager: Host? = null,
}

@Serializable
data class HighlightGroup(
    val title: String,
    val cover: String,
    val videos: List<String>
)

class HighlightGroupConverter : SerializableConverter<List<HighlightGroup>> by buildSerializableConverter()
