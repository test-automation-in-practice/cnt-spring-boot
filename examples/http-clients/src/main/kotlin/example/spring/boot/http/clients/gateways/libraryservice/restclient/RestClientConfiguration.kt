package example.spring.boot.http.clients.gateways.libraryservice.restclient

import example.spring.boot.http.clients.gateways.libraryservice.LibraryService
import example.spring.boot.http.clients.gateways.libraryservice.LibraryServiceProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient
import org.zalando.logbook.Logbook
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor

@Configuration("libraryServiceConfiguration")
@ConditionalOnProperty("client.mode", havingValue = "rest-client")
@EnableConfigurationProperties(LibraryServiceProperties::class)
internal class RestClientConfiguration(
    private val properties: LibraryServiceProperties
) {

    @Bean
    fun libraryService(logbook: Logbook?): LibraryService {
        val restClient = RestClient.builder()
            .apply {
                if (logbook != null) {
                    it.requestInterceptor(LogbookClientHttpRequestInterceptor(logbook))
                }
            }
            .build()
        return RestClientBasedLibraryService(restClient, properties)
    }
}
