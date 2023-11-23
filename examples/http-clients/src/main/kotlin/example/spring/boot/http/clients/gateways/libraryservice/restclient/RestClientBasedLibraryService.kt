package example.spring.boot.http.clients.gateways.libraryservice.restclient

import example.spring.boot.http.clients.gateways.libraryservice.Book
import example.spring.boot.http.clients.gateways.libraryservice.CreatedBook
import example.spring.boot.http.clients.gateways.libraryservice.LibraryService
import example.spring.boot.http.clients.gateways.libraryservice.LibraryServiceException
import example.spring.boot.http.clients.gateways.libraryservice.LibraryServiceProperties
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.UnknownContentTypeException
import org.springframework.web.client.body
import org.springframework.web.util.UriComponentsBuilder.fromHttpUrl

internal class RestClientBasedLibraryService(
    private val client: RestClient,
    private val properties: LibraryServiceProperties
) : LibraryService {

    override fun addBook(book: Book): CreatedBook =
        try {
            client.post()
                .uri(booksUri())
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(book)
                .retrieve()
                .body<CreatedBook>()
                ?: throw LibraryServiceException("Unexpected 'null' response.")
        } catch (e: RestClientResponseException) {
            throw LibraryServiceException(cause = e)
        } catch (e: UnknownContentTypeException) {
            // RestClient works a bit differently than the previous clients
            // 204 No Content without a content type header will throw a UnknownContentTypeException instead of
            // resolving to 'null'
            throw LibraryServiceException(cause = e)
        }

    private fun booksUri() = fromHttpUrl(properties.baseUrl)
        .pathSegment("api", "books")
        .toUriString()
}
