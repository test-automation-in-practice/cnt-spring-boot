package example.spring.boot.http.clients.gateways.libraryservice.webclient

import example.spring.boot.http.clients.gateways.libraryservice.Book
import example.spring.boot.http.clients.gateways.libraryservice.CreatedBook
import example.spring.boot.http.clients.gateways.libraryservice.LibraryService
import example.spring.boot.http.clients.gateways.libraryservice.LibraryServiceException
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.service.annotation.PostExchange

internal class DeclarativeWebClientBasedLibraryService(
    private val client: LibraryServiceClient,
) : LibraryService {

    override fun addBook(book: Book): CreatedBook = try {
        client.post(book) ?: throw LibraryServiceException("no response body")
    } catch (e: WebClientResponseException) {
        throw LibraryServiceException(cause = e)
    }

}

internal interface LibraryServiceClient {

    @PostExchange("/api/books", accept = [APPLICATION_JSON_VALUE])
    fun post(@RequestBody book: Book): CreatedBook?

}
