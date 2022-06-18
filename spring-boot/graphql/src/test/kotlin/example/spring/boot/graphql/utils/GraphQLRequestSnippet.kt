package example.spring.boot.graphql.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.restdocs.operation.Operation
import org.springframework.restdocs.operation.OperationRequest
import org.springframework.restdocs.snippet.TemplatedSnippet

object GraphQLRequestSnippet : TemplatedSnippet("graphql-request", null) {
    private val objectMapper = jacksonObjectMapper()

    override fun createModel(operation: Operation): Map<String, Any> =
        mapOf("query" to getRequestBody(operation.request))

    private fun getRequestBody(request: OperationRequest): String =
        objectMapper.readTree(request.content).get("query").textValue().trimIndent()
}
