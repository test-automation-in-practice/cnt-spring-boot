package advanced.e2e.gateways.bookcatalogue

import advanced.e2e.domain.Book
import advanced.e2e.domain.BookCatalogue
import advanced.e2e.gateways.defaultHttpClient
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.Request
import org.springframework.http.HttpHeaders.ACCEPT
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class BookCatalogueAccessor(
    private val properties: BookCatalogueProperties
) : BookCatalogue {

    private val client = defaultHttpClient()
    private val objectMapper = jacksonObjectMapper()

    override fun findByIsbn(isbn: String): Book? {
        val request = Request.Builder()
            .url("http://${properties.host}:${properties.port}/api/books/$isbn")
            .header(ACCEPT, APPLICATION_JSON_VALUE)
            .get()
            .build()

        return client.newCall(request).execute()
            .use { response ->
                val body = response.body()?.string() ?: ""
                when (val status = response.code()) {
                    200 -> objectMapper.readTree(body)
                    204, 404 -> null
                    else -> throw IOException("Failed call [status=$status]: $body")
                }
            }
            ?.let(::asBook)
    }

    private fun asBook(it: JsonNode) =
        Book(
            isbn = it["isbn"].textValue(),
            title = it["title"].textValue()
        )

}
