package example.spring.boot.advanced.e2e.gateways.bookcatalogue

import example.spring.boot.advanced.e2e.domain.Book
import example.spring.boot.advanced.e2e.domain.BookCatalogue
import example.spring.boot.advanced.e2e.gateways.common.defaultHttpClient
import okhttp3.Request
import org.springframework.http.HttpHeaders.ACCEPT
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.stereotype.Component
import tools.jackson.databind.JsonNode
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.io.IOException

@Component
class BookCatalogueClient(
    private val properties: BookCatalogueServiceProperties
) : BookCatalogue {

    private val httpClient = defaultHttpClient()
    private val objectMapper = jacksonObjectMapper()

    override fun findByIsbn(isbn: String): Book? {
        val request = Request.Builder()
            .url(properties.url("/api/books/$isbn"))
            .header(ACCEPT, APPLICATION_JSON_VALUE)
            .get()
            .build()

        return httpClient.newCall(request).execute()
            .use { response ->
                val body = response.body.string()
                when (val status = response.code) {
                    200 -> objectMapper.readTree(body)
                    204, 404 -> null
                    else -> throw IOException("Failed call [status=$status]: $body")
                }
            }
            ?.let(::asBook)
    }

    private fun asBook(it: JsonNode) =
        Book(
            isbn = it["isbn"].asString(null),
            title = it["title"].asString(null)
        )

}
