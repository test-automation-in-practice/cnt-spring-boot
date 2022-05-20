package advanced.e2e.gateways.mediacollection

import advanced.e2e.domain.BookRecord
import advanced.e2e.domain.MediaCollection
import advanced.e2e.gateways.common.defaultHttpClient
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class MediaCollectionClient(
    private val properties: MediaCollectionServiceProperties
) : MediaCollection {

    private val jsonMediaType = APPLICATION_JSON_VALUE.toMediaType()

    private val client = defaultHttpClient()
    private val objectMapper = jacksonObjectMapper()

    override fun register(record: BookRecord) {
        val registration = MediaRegistration(type = "BOOK", id = record.id.toString(), label = record.book.title)

        val request = Request.Builder()
            .url(properties.url("/api/media"))
            .post(jsonRequestBody(registration))
            .build()

        client.newCall(request).execute()
            .use { response ->
                if (!response.isSuccessful) {
                    val body = response.body?.string() ?: ""
                    throw IOException("Failed call [status=${response.code}]: $body")
                }
            }
    }

    private fun jsonRequestBody(registration: MediaRegistration) =
        objectMapper.writeValueAsString(registration).toRequestBody(jsonMediaType)

    data class MediaRegistration(val type: String, val id: String, val label: String)
}
