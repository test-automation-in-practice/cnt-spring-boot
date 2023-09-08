package example.spring.boot.http.clients.gateways.libraryservice.feign

import example.spring.boot.http.clients.gateways.libraryservice.Book
import example.spring.boot.http.clients.gateways.libraryservice.CreatedBook
import example.spring.boot.http.clients.gateways.libraryservice.LibraryService
import example.spring.boot.http.clients.gateways.libraryservice.LibraryServiceException
import feign.FeignException
import feign.Headers
import feign.RequestLine

internal class FeignClientBasedLibraryService(
    private val client: LibraryServiceClient
) : LibraryService {

    override fun addBook(book: Book): CreatedBook = try {
        client.post(book) ?: throw LibraryServiceException("no response body")
    } catch (e: FeignException) {
        throw LibraryServiceException(cause = e)
    }

}

@Headers(
    "Content-Type: application/json",
    "Accept: application/json"
)
internal interface LibraryServiceClient {

    @RequestLine("POST /api/books")
    fun post(book: Book): CreatedBook?

}
