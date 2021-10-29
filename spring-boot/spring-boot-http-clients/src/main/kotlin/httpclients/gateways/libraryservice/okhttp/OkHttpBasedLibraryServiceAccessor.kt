package httpclients.gateways.libraryservice.okhttp

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import httpclients.gateways.libraryservice.Book
import httpclients.gateways.libraryservice.CreatedBook
import httpclients.gateways.libraryservice.LibraryService
import httpclients.gateways.libraryservice.LibraryServiceProperties
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.create
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

    private val jsonMediaType = MediaType.get(APPLICATION_JSON_VALUE)
    private val objectMapper = jacksonObjectMapper()
        .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)

    override fun addBook(book: Book): CreatedBook {
        val jsonPayload = objectMapper.writeValueAsString(book)

        val request = Request.Builder()
            .url(HttpUrl.get(properties.baseUrl + "/api/books"))
            .post(create(jsonMediaType, jsonPayload))
            .build()

        return httpClient.newCall(request).execute()
            .use { response ->
                val responseBody = response.body()?.string() ?: ""
                when (val status = response.code()) {
                    200, 201 -> objectMapper.readValue(responseBody)
                    else -> throw exception(status, responseBody)
                }
            }
    }

    private fun exception(status: Int, responseBody: String) =
        IOException("Recieved bad response status $status: $responseBody")
}
