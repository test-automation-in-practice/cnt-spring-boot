package example.spring.boot.http.clients.gateways.libraryservice.restclient

import example.spring.boot.http.clients.gateways.libraryservice.Book
import example.spring.boot.http.clients.gateways.libraryservice.CreatedBook
import example.spring.boot.http.clients.gateways.libraryservice.LibraryService
import example.spring.boot.http.clients.gateways.libraryservice.LibraryServiceException
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.UnknownContentTypeException
import org.springframework.web.service.annotation.PostExchange

internal class DeclarativeWebClientBasedLibraryService(
    private val client: LibraryServiceClient,
) : LibraryService {

    override fun addBook(book: Book): CreatedBook = try {
        client.post(book) ?: throw LibraryServiceException("no response body")
    } catch (e: RestClientResponseException) {
        throw LibraryServiceException(cause = e)
    } catch (e: UnknownContentTypeException) {
        // RestClient works a bit differently than the previous clients
        // 204 No Content without a content type header will throw a UnknownContentTypeException instead of
        // resolving to 'null'
        throw LibraryServiceException(cause = e)
    }

}

internal interface LibraryServiceClient {

    @PostExchange("/api/books", accept = [APPLICATION_JSON_VALUE])
    fun post(@RequestBody book: Book): CreatedBook?

}
