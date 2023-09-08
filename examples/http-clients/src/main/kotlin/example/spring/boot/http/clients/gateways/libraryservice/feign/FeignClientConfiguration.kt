package example.spring.boot.http.clients.gateways.libraryservice.feign

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import example.spring.boot.http.clients.gateways.libraryservice.LibraryService
import example.spring.boot.http.clients.gateways.libraryservice.LibraryServiceProperties
import feign.Feign
import feign.Logger.Level.BASIC
import feign.Retryer.NEVER_RETRY
import feign.Target.HardCodedTarget
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import feign.okhttp.OkHttpClient
import feign.slf4j.Slf4jLogger
import okhttp3.OkHttpClient.Builder
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.zalando.logbook.Logbook
import org.zalando.logbook.okhttp.GzipInterceptor
import org.zalando.logbook.okhttp.LogbookInterceptor

@Configuration("libraryServiceConfiguration")
@ConditionalOnProperty("client.mode", havingValue = "feign")
@EnableConfigurationProperties(LibraryServiceProperties::class)
internal class FeignClientConfiguration(
    private val properties: LibraryServiceProperties
) {

    @Bean
    fun libraryService(logbook: Logbook?): LibraryService {
        val httpClient = Builder() // https://github.com/zalando/logbook#okhttp-v3x
            .apply { if (logbook != null) addNetworkInterceptor(LogbookInterceptor(logbook)) }
            .apply { if (logbook != null) addNetworkInterceptor(GzipInterceptor()) }
            .build()
        val objectMapper = jacksonObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val client: LibraryServiceClient = Feign.builder()
            .client(OkHttpClient(httpClient))
            .encoder(JacksonEncoder(objectMapper))
            .decoder(JacksonDecoder(objectMapper))
            .logger(Slf4jLogger(javaClass))
            .logLevel(BASIC)
            .retryer(NEVER_RETRY)
            .target(HardCodedTarget(LibraryServiceClient::class.java, properties.baseUrl))

        return FeignClientBasedLibraryService(client)
    }

}
