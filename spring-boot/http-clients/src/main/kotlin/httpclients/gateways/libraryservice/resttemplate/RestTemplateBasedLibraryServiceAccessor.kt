package httpclients.gateways.libraryservice.resttemplate

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import httpclients.gateways.libraryservice.Book
import httpclients.gateways.libraryservice.CreatedBook
import httpclients.gateways.libraryservice.LibraryService
import httpclients.gateways.libraryservice.LibraryServiceProperties
import okhttp3.OkHttpClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity
import java.io.IOException
import java.net.URI

@Service
@ConditionalOnProperty("client.mode", havingValue = "rest-template")
internal class RestTemplateBasedLibraryServiceAccessor(
    private val httpClient: OkHttpClient,
    private val properties: LibraryServiceProperties
) : LibraryService {

    private val restTemplate = RestTemplate()
        .apply { requestFactory = OkHttp3ClientHttpRequestFactory(httpClient) }
        .apply {
            val objectMapper = jacksonObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
            messageConverters = listOf(MappingJackson2HttpMessageConverter(objectMapper))
        }

    override fun addBook(book: Book): CreatedBook = try {
        val response = restTemplate.postForEntity<CreatedBook>(booksUri(), book)
        when (response.statusCode) {
            OK, CREATED -> response.body!!
            else -> throw exception(response)
        }
    } catch (e: RestClientResponseException) {
        throw IOException(e)
    }

    private fun booksUri() = URI(properties.baseUrl + "/api/books")

    private fun exception(response: ResponseEntity<*>) =
        IOException("Recieved bad response: $response")
}
