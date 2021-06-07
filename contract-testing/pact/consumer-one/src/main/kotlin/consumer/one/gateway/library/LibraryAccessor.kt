package consumer.one.gateway.library

import consumer.one.model.Book
import org.springframework.http.HttpEntity.EMPTY
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

// Component used to communicate with the provider service via HTTP.
// Uses a simple RestTemplate to make the HTTP calls and transforms the provider's response model into this
// service's internal model.

@Service
class LibraryAccessor(
    private val settings: LibraryAccessorSettings
) {

    private val restTemplate = RestTemplate()

    fun getBook(id: String): Book? {
        val response = restTemplate.exchange("${settings.url}/books/$id", GET, EMPTY, LibraryBook::class.java)
        return when (response.statusCode) {
            OK -> response.body?.toBook() ?: error("missing body")
            NOT_FOUND -> null
            else -> error("server responded with: $response")
        }
    }

    private data class LibraryBook(
        val isbn: String,
        val title: String,
        val authors: List<String>?
    ) {
        fun toBook() = Book(
            isbn = isbn,
            title = title,
            authors = authors ?: emptyList()
        )
    }

}
