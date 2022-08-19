package example.spring.boot.graphql

import example.spring.boot.graphql.business.pageIndexRange
import example.spring.boot.graphql.business.pageSizeRange
import graphql.language.IntValue
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.CoercingSerializeException
import graphql.schema.GraphQLScalarType
import graphql.schema.idl.RuntimeWiring
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.execution.RuntimeWiringConfigurer

@Configuration
class GraphQLConfiguration : RuntimeWiringConfigurer {

    override fun configure(builder: RuntimeWiring.Builder) {
        builder
            .scalar(scalarType("PageIndex", IntRangeCoercing(pageIndexRange)))
            .scalar(scalarType("PageSize", IntRangeCoercing(pageSizeRange)))
    }

    private fun scalarType(name: String, coercing: Coercing<*, *>): GraphQLScalarType =
        GraphQLScalarType.newScalar().name(name).coercing(coercing).build()

    private class IntRangeCoercing(private val range: IntRange) : Coercing<Int, Int> {
        override fun serialize(dataFetcherResult: Any): Int {
            if (dataFetcherResult is Int) return dataFetcherResult
            throw CoercingSerializeException("Unable to serialize [$dataFetcherResult] as an Int")
        }

        override fun parseValue(input: Any): Int {
            if (input is Number && input.toInt() in range) return input.toInt()
            throw CoercingParseValueException("Value [$input] is not in range: $range")
        }

        override fun parseLiteral(input: Any): Int {
            if (input is IntValue && input.value.toInt() in range) return input.value.toInt()
            throw CoercingParseLiteralException("Value $input is not in range: $range")
        }
    }

}
