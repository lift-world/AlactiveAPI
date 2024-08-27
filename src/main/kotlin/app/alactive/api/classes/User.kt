package app.alactive.api.classes

import app.alactive.api.utilities.InstantConverter
import app.alactive.api.utilities.LocalDateConverter
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.Index
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship
import org.neo4j.ogm.annotation.typeconversion.Convert

@NodeEntity
data class User(
    /**
     * User-facing unique identifier.
     */
    @Id val handle: String = "",
    /**
     * Unique identifier in our Auth0 domain.
     */
    @Index(unique = true)
    val authID: String = "",
    /**
     * Real life name (ideally).
     */
    val name: String = "",
    /**
     * Birthday.
     */
    @Convert(LocalDateConverter::class)
    val birthday: LocalDate = LocalDate.fromEpochDays(0),
    /**
     * The place of work or study. To determine which one of the two, see `isStudent`.
     */
    val occupation: String? = null,
    /**
     * Whether the `User` is working or a student. Primarily used when displaying `occupation`.
     */
    val isStudent: Boolean? = null,
    /**
     * Avatar. Note that if none is present, one will be generated based on their name.
     *
     * **Standard**: Alactive-Identifier, prefix “avatar”.
     */
    val avatar: String? = null,
    /**
     * List of social media and/or personal links.
     */
    val links: List<String> = listOf(),
    /**
     * The self-provided biography.
     */
    val biography: String? = null,
    /**
     * Visibility level of this `User`'s profile.
     */
    val visibility: Visibility? = null,
    /**
     * When this `User` was created.
     */
    @Convert(InstantConverter::class)
    val created: Instant = Instant.DISTANT_PAST,
    /**
     * When any attribute of this `User` was last modified.
     */
    @Convert(InstantConverter::class)
    val lastModified: Instant = Instant.DISTANT_PAST,
    /**
     * When this `User` last made an API request (and was hence active).
     */
    @Convert(InstantConverter::class)
    val lastSeen: Instant = Instant.DISTANT_PAST,
//    /**
//     * The `Event`s, `Host`s, and `Venue`s liked by this `User`.
//     *
//     * **Inverse**: `Likeable::likedBy`, GraphQL only.
//     */
//    @Relationship(type = "LIKED", direction = Relationship.Direction.OUTGOING)
//    val liked: List<Likeable> = listOf(),
//    /**
//     * The `Host`s which this `User` manages.
//     */
//    val manages: List<Host> = listOf(),
) {
    /**
     * The `User`s which this `User` follows.
     *
     * **Inverse**: `User::followers`.
     */
    @Relationship(type = "FOLLOWS", direction = Relationship.Direction.OUTGOING)
    val follows: MutableSet<User> = mutableSetOf()

    /**
     * The other `User`s which follow this `User`.
     *
     * **Inverse**: `User::follows`.
     */
    @Relationship(type = "FOLLOWS", direction = Relationship.Direction.INCOMING)
    val followers: MutableSet<User> = mutableSetOf()
}

/**
 * The visibility level of a particular `User`.
 */
enum class Visibility {
    PUBLIC,
    FRIENDS_ONLY,
    PRIVATE,
}

/**
 * Relationship-less projections for the set of types which can be liked by a `User`.
 */
interface Likeable {
    /**
     * The unique ID for this `Likeable` object.
     */
    val id: String
}
