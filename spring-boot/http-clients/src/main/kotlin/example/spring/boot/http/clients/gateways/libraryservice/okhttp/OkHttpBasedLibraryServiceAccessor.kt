package example.spring.boot.http.clients.gateways.libraryservice.okhttp

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import example.spring.boot.http.clients.gateways.libraryservice.Book
import example.spring.boot.http.clients.gateways.libraryservice.CreatedBook
import example.spring.boot.http.clients.gateways.libraryservice.LibraryService
import example.spring.boot.http.clients.gateways.libraryservice.LibraryServiceProperties
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.stereotype.Service
import java.io.IOException

@Service
@ConditionalOnProperty("client.mode", havingValue = "okhttp")
internal class OkHttpBasedLibraryServiceAccessor(
    private val httpClient: OkHttpClient,
    private val properties: LibraryServiceProperties
) : LibraryService {

    private val jsonMediaType = APPLICATION_JSON_VALUE.toMediaType()

    private val objectMapper = jacksonObjectMapper()
        .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)

    override fun addBook(book: Book): CreatedBook {

        val request = Request.Builder()
            .url(properties.baseUrl + "/api/books")
            .post(jsonRequestBody(book))
            .build()

        return httpClient.newCall(request).execute()
            .use { response ->
                val responseBody = response.body?.string() ?: ""
                when (val status = response.code) {
                    200, 201 -> objectMapper.readValue(responseBody)
                    else -> throw exception(status, responseBody)
                }
            }
    }

    private fun jsonRequestBody(book: Book) =
        objectMapper.writeValueAsString(book).toRequestBody(jsonMediaType)

    private fun exception(status: Int, responseBody: String) =
        IOException("Recieved bad response status $status: $responseBody")
}
