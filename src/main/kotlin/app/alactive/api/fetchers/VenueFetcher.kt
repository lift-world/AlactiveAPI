package app.alactive.api.fetchers

import app.alactive.api.classes.HighlightGroup
import app.alactive.api.classes.Venue
import app.alactive.api.services.IDPrefix
import app.alactive.api.services.IDService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import kotlinx.datetime.Clock
import org.neo4j.ogm.session.SessionFactory
import org.neo4j.ogm.session.load
import java.security.DrbgParameters.Capability

@DgsComponent
class VenueFetcher(
    private val sessionFactory: SessionFactory,
    private val idService: IDService,
) {
    /**
     * Returns all fields of an `Venue` with ID `id`.
     */
    @DgsQuery
    fun venue(@InputArgument id: String): Venue? {
        val session = sessionFactory.openSession()
        return session.load<Venue>(id)
    }

    @DgsMutation
    suspend fun createVenue(
        @InputArgument name: String, @InputArgument latitude: Double, @InputArgument longitude: Double,
        @InputArgument country: String, @InputArgument municipality: String, @InputArgument postcode: String,
        @InputArgument address: String, @InputArgument avatar: String? = null,
        @InputArgument description: String? = null, @InputArgument media: List<String> = listOf(),
        @InputArgument highlights: List<HighlightGroup> = listOf(), @InputArgument tags: List<String> = listOf(),
        @InputArgument capacity: Int? = null, @InputArgument type: String? = null,
        @InputArgument managerID: String? = null
    ): Venue {
        val timestamp = Clock.System.now()
        val venue = Venue(
            idService.generateID(IDPrefix.VENUE), name, latitude, longitude, country, municipality, postcode, address,
            avatar, description, media, highlights, tags, capacity, type, timestamp, timestamp
        )
        val session = sessionFactory.openSession()
        session.save(venue)
        return venue
    }

    @DgsMutation
    suspend fun editVenue(
        @InputArgument id: String,
        @InputArgument name: String? = null, @InputArgument latitude: Double? = null, @InputArgument longitude: Double? = null,
        @InputArgument country: String? = null, @InputArgument municipality: String? = null, @InputArgument postcode: String? = null,
        @InputArgument address: String? = null, @InputArgument avatar: String? = null,
        @InputArgument description: String? = null, @InputArgument media: List<String>?,
        @InputArgument highlights: List<HighlightGroup>?, @InputArgument tags: List<String>?,
        @InputArgument capacity: Int? = null, @InputArgument type: String? = null,
        @InputArgument managerID: String? = null
    ): Venue? {
        val session = sessionFactory.openSession()
        val timestamp = Clock.System.now()

        val venue = session.load<Venue>(id)

        val updatedVenue = venue?.copy(
            id = venue.id,
            name = name?: venue.name,
            latitude = latitude?: venue.latitude,
            longitude = longitude?: venue.longitude,
            country = country?: venue.country,
            municipality = municipality?: venue.municipality,
            postcode = postcode?: venue.postcode,
            address = address?: venue.address,
            avatar = avatar?: venue.avatar,
            description = description?: venue.description,
            media = media?: venue.media,
            highlights = highlights?: venue.highlights,
            tags = tags?: venue.tags,
            capacity = capacity?: venue.capacity,
            type = type?: venue.type,
            lastModified = timestamp
        )

        updatedVenue?.hosting = venue?.hosting!!

        session.save(updatedVenue, 2)
        return updatedVenue
    }

    @DgsMutation
    suspend fun deleteVenue(@InputArgument id: String): Boolean {
        val session = sessionFactory.openSession()
        val venue = session.load<Venue>(id)
        session.delete(venue)
        return true
    }
}
