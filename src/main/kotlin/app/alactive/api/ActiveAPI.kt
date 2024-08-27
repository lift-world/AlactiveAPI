package app.alactive.api

import org.neo4j.ogm.session.SessionFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.neo4j.ogm.config.Configuration as Neo4jConfiguration

@SpringBootApplication
@ConfigurationPropertiesScan("app.alactive.api")
class ActiveAPI

fun main(args: Array<String>) {
    runApplication<ActiveAPI>(*args)
}

@ConfigurationProperties("alactive")
data class AlactiveConfiguration(
    val database: DatabaseConfiguration,
    val endpoint: EndpointConfiguration,
) {
    data class DatabaseConfiguration(
        val uri: String,
        val username: String,
        val password: String,
    )

    data class EndpointConfiguration(
        val idGeneratorUri: String,
        val recommendationUri: String,
        val s3Uri: String,
    )
}

@Configuration
class Configuration {
    @Bean
    fun sessionFactory(config: AlactiveConfiguration): SessionFactory = SessionFactory(
        Neo4jConfiguration.Builder()
            .uri(config.database.uri)
            .credentials(config.database.username, config.database.password)
            .build(),
        "app.alactive.api.classes"
    )
}
