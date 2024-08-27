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
data class Event(
    /**
     * This property uniquely identifies an `Event`.
     *
     * Standard: Alactive-Identifier, prefix "event".
     */
    @Id val id: String = "",
    /**
     * The user-facing name of this `Event`.
     */
    val name: String = "",
    /**
     * The user-facing description of this `Event`.
     */
    val description: String? = null,
    /**
     * The genres of music that will be at this `Event`.
     */
    val musicGenres: List<String> = listOf(),
    /**
     * The cover video associated with this `Event`.
     *
     * Standard: Alactive-Identifier, prefix "video".
     */
    val video: String = "",
    /**
     * The pieces of media associated with this `Event`.
     *
     * Standard: Alactive-Identifier, prefix "video" or "image".
     */
    val media: List<String> = listOf(),
    /**
     * The methods with which a user can gain entry to this `Event`.
     */
    @Convert(AccessPolicyConverter::class)
    val accessPolicies: List<AccessPolicy> = listOf(),
    /**
     * The host-specified rules for this `Event`. These are organised in sections, each with a title. Each section has
     * individual rules which have an icon and some text.
     */
    @Convert(RuleSectionConverter::class)
    val rules: List<RuleSection> = listOf(),
    /**
     * The (first, if recurring) starting date and time of this `Event`.
     */
    @Convert(InstantConverter::class)
    val datetime: Instant = Instant.DISTANT_PAST,
    /**
     * The duration of this `Event` in seconds.
     */
    val duration: Int? = null,
    /**
     * The recurrence rule for this `Event`, if any.
     *
     * **Standard**: RFC5545 RRULE string.
     */
    val recurrence: String? = null,
    /**
     * The set of boolean information characterizing this Event. (i.e. "dancers", "bottleShow", etc.)
     * **Standard**: All the possible tags must be uniquely standardize from Alacitve
     */
    val tags: List<String> = listOf(),
    /**
     * When this `Event` was created.
     */
    @Convert(InstantConverter::class)
    val created: Instant = Instant.DISTANT_PAST,
    /**
     * When any attribute of this `Event` was last modified.
     */
    @Convert(InstantConverter::class)
    val lastModified: Instant = Instant.DISTANT_PAST,
) {
    /**
     * The `Venue` which is hosting this `Event`.
     */
    @Relationship(type = "HOSTED_BY", direction = Relationship.Direction.OUTGOING)
    lateinit var hostedBy: Venue

    /**
     * The set of `User`s who have liked this `Event`.
     */
    val likedBy: MutableSet<User> = mutableSetOf()
}

/**
 * An `AccessPolicy` represents a method with which a `User` can gain entry to an `Event`
 */
@Serializable
data class AccessPolicy(
    val type: String,
    val minPrice: String,
    val maxPrice: String,
    val currency: String,
    val info: String,
)

class AccessPolicyConverter : SerializableConverter<List<AccessPolicy>> by buildSerializableConverter()

/**
 * A `RuleSection` contains a titled segment of rules to be displayed on the frontend.
 */
@Serializable
data class RuleSection(
    val title: String,
    val rules: List<Rule>,
)

class RuleSectionConverter : SerializableConverter<List<RuleSection>> by buildSerializableConverter()

/**
 * A `Rule` is an iconned description of a restriction at an `Event`.
 */
@Serializable
data class Rule(
    val icon: String,
    val text: String,
)
