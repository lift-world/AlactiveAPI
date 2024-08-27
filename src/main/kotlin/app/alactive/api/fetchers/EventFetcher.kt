package app.alactive.api.fetchers

import app.alactive.api.classes.AccessPolicy
import app.alactive.api.classes.Event
import app.alactive.api.classes.RuleSection
import app.alactive.api.classes.Venue
import app.alactive.api.services.IDPrefix
import app.alactive.api.services.IDService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import graphql.schema.DataFetchingEnvironment
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.neo4j.ogm.session.SessionFactory
import org.neo4j.ogm.session.load

@DgsComponent
class EventFetcher(
    private val sessionFactory: SessionFactory,
    private val idService: IDService,
) {
    /**
     * Returns all fields of an `Event` with ID `id`.
     */
    @DgsQuery
    fun event(@InputArgument id: String): Event? {
        val session = sessionFactory.openSession()
        return session.load<Event>(id)
    }

    /**
     * Returns all the `Events` that the recommendation engine selected for the requesting user.
     */
    @DgsQuery
    fun recommendMe(): Collection<Event> {
        val session = sessionFactory.openSession()
        // TODO: Integrate with recommendation.
        return session.loadAll(Event::class.java)
    }

    @DgsMutation
    suspend fun createEvent(
        @InputArgument name: String, @InputArgument description: String? = null,
        @InputArgument musicGenres: List<String> = listOf(), @InputArgument video: String,
        @InputArgument media: List<String> = listOf(), @InputArgument accessPolicies: List<AccessPolicy>,
        @InputArgument rules: List<RuleSection> = listOf(), @InputArgument datetime: Instant,
        @InputArgument duration: Int? = null, @InputArgument recurrence: String? = null,
        @InputArgument tags: List<String> = listOf(), @InputArgument hostedBy: String,
        env: DataFetchingEnvironment,
    ): Event {
        val session = sessionFactory.openSession()
        val timestamp = Clock.System.now()

        val venue = session.load<Venue>(hostedBy)
        requireNotNull(venue) { "Could not find Venue with ID $hostedBy!" }

        val event = Event(
            idService.generateID(IDPrefix.EVENT), name, description, musicGenres, video, media, accessPolicies, rules,
            datetime, duration, recurrence, tags, timestamp, timestamp
        )

        event.hostedBy = venue
        session.save(event)

        return event
    }

    @DgsMutation
    suspend fun deleteEvent(@InputArgument id: String): Boolean {
        val session = sessionFactory.openSession()
        val event = session.load<Event>(id)
        session.delete(event)
        return true
    }
}
