package example.spring.boot.http.clients.gateways.libraryservice.webclient

import example.spring.boot.http.clients.gateways.libraryservice.Book
import example.spring.boot.http.clients.gateways.libraryservice.CreatedBook
import example.spring.boot.http.clients.gateways.libraryservice.LibraryService
import example.spring.boot.http.clients.gateways.libraryservice.LibraryServiceException
import example.spring.boot.http.clients.gateways.libraryservice.LibraryServiceProperties
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.util.UriComponentsBuilder.fromHttpUrl

internal class WebClientBasedLibraryService(
    private val client: WebClient,
    private val properties: LibraryServiceProperties
) : LibraryService {

    override fun addBook(book: Book): CreatedBook =
        try {
            client.post()
                .uri(booksUri())
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .bodyValue(book)
                .retrieve()
                .bodyToMono<CreatedBook>()
                .block() ?: throw LibraryServiceException("Unexpected 'null' (most likely HTTP 204) response.")
        } catch (e: WebClientResponseException) {
            throw LibraryServiceException(cause = e)
        }

    private fun booksUri() = fromHttpUrl(properties.baseUrl)
        .pathSegment("api", "books")
        .toUriString()
}
