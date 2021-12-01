package httpclients.gateways.libraryservice.feign

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import feign.*
import feign.Logger.Level
import feign.Target.HardCodedTarget
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import feign.slf4j.Slf4jLogger
import httpclients.gateways.libraryservice.Book
import httpclients.gateways.libraryservice.CreatedBook
import httpclients.gateways.libraryservice.LibraryService
import httpclients.gateways.libraryservice.LibraryServiceProperties
import okhttp3.OkHttpClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import java.io.IOException

@Service
@ConditionalOnProperty("client.mode", havingValue = "feign")
internal class FeignBasedLibraryServiceAccessor(
    httpClient: OkHttpClient,
    properties: LibraryServiceProperties
) : LibraryService {

    private val objectMapper = jacksonObjectMapper()
        .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)

    private val client: FeignClient = Feign.builder()
        .client(feign.okhttp.OkHttpClient(httpClient))
        .encoder(JacksonEncoder(objectMapper))
        .decoder(JacksonDecoder(objectMapper))
        .logger(Slf4jLogger(javaClass))
        .logLevel(Level.BASIC)
        .retryer(Retryer.NEVER_RETRY)
        .target(HardCodedTarget(FeignClient::class.java, properties.baseUrl))

    override fun addBook(book: Book): CreatedBook = try {
        client.post(book) ?: throw IOException("no response body")
    } catch (e: FeignException) {
        throw IOException(e)
    }

    @Headers("Content-Type: application/json")
    private interface FeignClient {

        @RequestLine("POST /api/books")
        fun post(book: Book): CreatedBook?

    }

}
