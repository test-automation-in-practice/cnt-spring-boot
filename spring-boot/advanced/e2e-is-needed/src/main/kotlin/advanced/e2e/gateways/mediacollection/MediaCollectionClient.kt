package advanced.e2e.gateways.mediacollection

import advanced.e2e.domain.BookRecord
import advanced.e2e.domain.MediaCollection
import advanced.e2e.gateways.common.defaultHttpClient
import okhttp3.MediaType.get
import okhttp3.Request
import okhttp3.RequestBody.create
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class MediaCollectionClient(
    private val properties: MediaCollectionServiceProperties
) : MediaCollection {

    private val client = defaultHttpClient()

    override fun register(record: BookRecord) {
        val requestBody = """{ "type": "BOOK", "id": "${record.id}" }"""
        val request = Request.Builder()
            .url(properties.url("/api/media"))
            .post(jsonRequestBody(requestBody))
            .build()

        client.newCall(request).execute()
            .use { response ->
                if (!response.isSuccessful) {
                    val body = response.body()?.string() ?: ""
                    throw IOException("Failed call [status=${response.code()}]: $body")
                }
            }
    }

    private fun jsonRequestBody(requestBody: String) = create(get(APPLICATION_JSON_VALUE), requestBody)

}
