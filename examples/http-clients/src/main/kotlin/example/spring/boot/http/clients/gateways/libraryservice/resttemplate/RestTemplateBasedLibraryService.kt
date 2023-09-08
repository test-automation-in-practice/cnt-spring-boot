package example.spring.boot.http.clients.gateways.libraryservice.resttemplate

import example.spring.boot.http.clients.gateways.libraryservice.Book
import example.spring.boot.http.clients.gateways.libraryservice.CreatedBook
import example.spring.boot.http.clients.gateways.libraryservice.LibraryService
import example.spring.boot.http.clients.gateways.libraryservice.LibraryServiceException
import example.spring.boot.http.clients.gateways.libraryservice.LibraryServiceProperties
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity
import java.net.URI

internal class RestTemplateBasedLibraryService(
    private val template: RestTemplate,
    private val properties: LibraryServiceProperties
) : LibraryService {

    override fun addBook(book: Book): CreatedBook = try {
        val response = template.postForEntity<CreatedBook>(booksUri(), book)
        when (response.statusCode) {
            OK, CREATED -> response.body!!
            else -> throw exception(response)
        }
    } catch (e: RestClientResponseException) {
        throw LibraryServiceException(cause = e)
    }

    private fun booksUri() = URI(properties.baseUrl + "/api/books")

    private fun exception(response: ResponseEntity<*>) =
        LibraryServiceException("Received bad response: $response")
}
