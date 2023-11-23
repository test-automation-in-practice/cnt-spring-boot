package example.spring.boot.graphql

import example.spring.boot.graphql.business.pageIndexRange
import example.spring.boot.graphql.business.pageSizeRange
import graphql.ErrorType
import graphql.GraphQLContext
import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import graphql.execution.CoercedVariables
import graphql.language.IntValue
import graphql.language.Value
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.CoercingSerializeException
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLScalarType
import graphql.schema.idl.RuntimeWiring
import jakarta.validation.ConstraintViolationException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.execution.DataFetcherExceptionResolver
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter
import org.springframework.graphql.execution.RuntimeWiringConfigurer
import org.springframework.validation.BindException
import java.util.Locale

@Configuration
class GraphQLConfiguration : RuntimeWiringConfigurer {

    @Bean
    fun dataFetcherExceptionResolver(): DataFetcherExceptionResolver =
        GraphQlExceptionResolver()

    override fun configure(builder: RuntimeWiring.Builder) {
        builder
            .scalar(scalarType("PageIndex", IntRangeCoercing(pageIndexRange)))
            .scalar(scalarType("PageSize", IntRangeCoercing(pageSizeRange)))
    }

    private fun scalarType(name: String, coercing: Coercing<*, *>): GraphQLScalarType =
        GraphQLScalarType.newScalar().name(name).coercing(coercing).build()

    private class IntRangeCoercing(private val range: IntRange) : Coercing<Int, Int> {
        override fun serialize(dataFetcherResult: Any, graphQLContext: GraphQLContext, locale: Locale): Int {
            if (dataFetcherResult is Int) return dataFetcherResult
            throw CoercingSerializeException("Unable to serialize [$dataFetcherResult] as an Int")
        }

        override fun parseValue(input: Any, graphQLContext: GraphQLContext, locale: Locale): Int {
            if (input is Number && input.toInt() in range) return input.toInt()
            throw CoercingParseValueException("Value [$input] is not in range: $range")
        }

        override fun parseLiteral(
            input: Value<*>,
            variables: CoercedVariables,
            graphQLContext: GraphQLContext,
            locale: Locale
        ): Int {
            if (input is IntValue && input.value.toInt() in range) return input.value.toInt()
            throw CoercingParseLiteralException("Value $input is not in range: $range")
        }
    }

    private class GraphQlExceptionResolver : DataFetcherExceptionResolverAdapter() {

        override fun resolveToSingleError(ex: Throwable, env: DataFetchingEnvironment): GraphQLError? =
            when (ex) {
                is BindException -> handle(ex, env) // thrown if argument cannot be bound - like in this showcase
                is ConstraintViolationException -> handle(ex, env)
                else -> null
            }

        private fun handle(ex: BindException, env: DataFetchingEnvironment): GraphQLError? =
            when (val fe = ex.bindingResult.fieldError) {
                null -> null
                else -> when (fe.code) {
                    "typeMismatch" -> GraphqlErrorBuilder.newError()
                        .errorType(ErrorType.ValidationError)
                        .message("Invalid value [${fe.rejectedValue}] for '${fe.objectName}' in '${fe.field}'")
                        .path(env.executionStepInfo.path)
                        .location(env.field.sourceLocation)
                        .build()

                    else -> null
                }
            }

        private fun handle(ex: ConstraintViolationException, env: DataFetchingEnvironment): GraphQLError? =
            GraphqlErrorBuilder.newError()
                .errorType(ErrorType.ValidationError)
                .message(
                    ex.constraintViolations.joinToString(separator = " | ") {
                        "Invalid value [${it.invalidValue}] for '${it.propertyPath}': ${it.message}"
                    }
                )
                .path(env.executionStepInfo.path)
                .location(env.field.sourceLocation)
                .build()

    }

}
