package example.spring.boot.http.clients.gateways.libraryservice.okhttp

import example.spring.boot.http.clients.gateways.libraryservice.LibraryService
import example.spring.boot.http.clients.gateways.libraryservice.LibraryServiceProperties
import okhttp3.OkHttpClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.zalando.logbook.Logbook
import org.zalando.logbook.okhttp.GzipInterceptor
import org.zalando.logbook.okhttp.LogbookInterceptor

@Configuration("libraryServiceConfiguration")
@ConditionalOnProperty("client.mode", havingValue = "okhttp")
@EnableConfigurationProperties(LibraryServiceProperties::class)
internal class OkHttpClientConfiguration(
    private val properties: LibraryServiceProperties
) {

    @Bean
    fun libraryService(logbook: Logbook?): LibraryService {
        val client = OkHttpClient.Builder() // https://github.com/zalando/logbook#okhttp-v3x
            .apply { if (logbook != null) addNetworkInterceptor(LogbookInterceptor(logbook)) }
            .apply { if (logbook != null) addNetworkInterceptor(GzipInterceptor()) }
            .build()
        return OkHttpClientBasedLibraryService(client, properties)
    }

}
