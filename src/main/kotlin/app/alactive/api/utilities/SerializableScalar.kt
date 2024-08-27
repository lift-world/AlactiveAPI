package app.alactive.api.utilities

import com.netflix.graphql.dgs.DgsScalar
import graphql.GraphQLContext
import graphql.execution.CoercedVariables
import graphql.language.NullValue
import graphql.language.StringValue
import graphql.language.Value
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.CoercingSerializeException
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.util.*

typealias StringCoercing<T> = Coercing<T, String>

inline fun <reified T> buildSerializableScalar(): StringCoercing<T> = object : StringCoercing<T> {
    override fun serialize(dataFetcherResult: Any, graphQLContext: GraphQLContext, locale: Locale): String {
        if (dataFetcherResult is T) return Json.encodeToString<T>(serializer<T>(), dataFetcherResult)
        throw CoercingSerializeException("Not a valid ${T::class.simpleName}")
    }

    override fun parseValue(input: Any, graphQLContext: GraphQLContext, locale: Locale): T =
        Json.decodeFromString<T>(input.toString()).runCatching { this }.getOrElse {
            throw CoercingParseValueException("$input is not a valid representation of ${T::class.simpleName}!")
        }

    override fun parseLiteral(
        input: Value<*>,
        variables: CoercedVariables,
        graphQLContext: GraphQLContext,
        locale: Locale
    ): T? = when (input) {
        is StringValue -> Json.decodeFromString<T>(input.value).runCatching { this }.getOrNull()
        is NullValue -> null
        else -> throw CoercingParseLiteralException(
            "$input is not a valid representation of enum ${T::class.simpleName}!"
        )
    }
}

abstract class SerializableScalar<T>(
    private val delegate: StringCoercing<T>,
) : StringCoercing<T> {
    override fun serialize(dataFetcherResult: Any, graphQLContext: GraphQLContext, locale: Locale): String? =
        delegate.serialize(dataFetcherResult, graphQLContext, locale)

    override fun parseValue(input: Any, graphQLContext: GraphQLContext, locale: Locale): T? =
        delegate.parseValue(input, graphQLContext, locale)

    override fun parseLiteral(
        input: Value<*>,
        variables: CoercedVariables,
        graphQLContext: GraphQLContext,
        locale: Locale
    ): T? = delegate.parseLiteral(input, variables, graphQLContext, locale)
}

@DgsScalar(name = "LocalDate")
object LocalDateScalar : SerializableScalar<LocalDate>(buildSerializableScalar())

@DgsScalar(name = "Instant")
class InstantScalar : SerializableScalar<Instant>(buildSerializableScalar())
