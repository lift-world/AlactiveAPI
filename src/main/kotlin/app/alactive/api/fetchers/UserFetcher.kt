package app.alactive.api.fetchers

import app.alactive.api.classes.User
import app.alactive.api.classes.Visibility
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import graphql.schema.DataFetchingEnvironment
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import org.neo4j.ogm.session.SessionFactory
import org.neo4j.ogm.session.load

@DgsComponent
class UserFetcher(private val sessionFactory: SessionFactory) {
    /** Returns the requesting `User` (who must be authenticated). */
    @DgsQuery
    fun me(env: DataFetchingEnvironment): User = TODO()

    /** Returns true if the requesting `User` exists (i.e., has been onboarded) in our database. */
    @DgsQuery
    fun doIExist(env: DataFetchingEnvironment): Boolean = TODO()

    /**
     * Returns all fields of a `User` with handle `handle`.
     *
     * **Parameter `handle`**: The `handle` of the `User` to search for.
     */
    @DgsQuery
    fun user(@InputArgument handle: String, env: DataFetchingEnvironment): User? {
        val session = sessionFactory.openSession()
        return session.load<User>(handle)
    }

    /**
     * Returns the newly created `User`.
     *
     * **Parameter `--`**: The fields necessary to create a new `User`.
     */
    @DgsMutation
    fun createUser(
        @InputArgument handle: String, @InputArgument authID: String, @InputArgument name: String,
        @InputArgument birthday: LocalDate, @InputArgument occupation: String? = null,
        @InputArgument isStudent: Boolean? = null, @InputArgument avatar: String? = null,
        @InputArgument links: List<String> = listOf(), @InputArgument biography: String? = null,
        @InputArgument visibility: Visibility? = null, env: DataFetchingEnvironment
    ): User? {
        val timestamp = Clock.System.now()
        val user = User(
            handle, authID, name, birthday, occupation, isStudent, avatar, links, biography, visibility, timestamp,
            timestamp, timestamp
        )

        // TODO: Validate as necessary.

        val session = sessionFactory.openSession()
        session.save(user)
        return user
    }

    /**
     * Edits the requesting `User` and returns the edited `User`.
     *
     * **Parameter `--`**: The fields which are editable for a `User`.
     */
    @DgsMutation
    fun updateUser(
        name: String? = null, birthday: LocalDate? = null, occupation: String? = null, isStudent: Boolean? = null,
        avatar: String? = null, links: List<String> = listOf(), biography: String? = null,
        visibility: Visibility? = null, env: DataFetchingEnvironment,
    ): User = TODO("Need to identify requesting user.")

    /** Deletes the requesting `User`, and returns success. */
    @DgsMutation
    fun deleteUser(env: DataFetchingEnvironment): Boolean = TODO("Need to identify requesting user.")

    /** Sets the current `User` to follow `target` according to `follow`, and returns the requesting `User`. */
    @DgsMutation
    fun setFollow(@InputArgument me: String, @InputArgument target: String, @InputArgument follow: Boolean): User {
        val session = sessionFactory.openSession()

        val meUser = session.load<User>(me)
        val targetUser = session.load<User>(target)

        requireNotNull(targetUser) { "Cannot set follow for non-existent user!" }
        requireNotNull(meUser)

        require(me != target) { "Cannot follow oneself!" }

        if (follow) {
            meUser.follows.add(targetUser)
            targetUser.followers.add(meUser)
        } else {
            meUser.follows.remove(targetUser)
            targetUser.followers.remove(meUser)
        }

        session.save(listOf(meUser, targetUser))
        return meUser
    }

    /** Sets the current `User` to like `target` according to `follow`, and returns the requesting `User`. */
    @DgsMutation
    fun setLike(target: String, like: Boolean): User = TODO("Need to identify requesting user.")
}
