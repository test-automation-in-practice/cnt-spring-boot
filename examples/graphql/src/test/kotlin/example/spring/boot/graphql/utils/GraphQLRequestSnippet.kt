package example.spring.boot.graphql.utils

import org.springframework.restdocs.operation.Operation
import org.springframework.restdocs.operation.OperationRequest
import org.springframework.restdocs.snippet.TemplatedSnippet
import tools.jackson.module.kotlin.jacksonObjectMapper

object GraphQLRequestSnippet : TemplatedSnippet("graphql-request", null) {
    private val objectMapper = jacksonObjectMapper()

    override fun createModel(operation: Operation): Map<String, Any> =
        mapOf("query" to getQueryFromRequestBody(operation.request))

    private fun getQueryFromRequestBody(request: OperationRequest): String =
        objectMapper.readTree(request.content).get("query").asString(null).trimIndent()
}
