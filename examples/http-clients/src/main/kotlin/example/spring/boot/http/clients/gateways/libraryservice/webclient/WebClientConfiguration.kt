package example.spring.boot.http.clients.gateways.libraryservice.webclient

import example.spring.boot.http.clients.gateways.libraryservice.LibraryService
import example.spring.boot.http.clients.gateways.libraryservice.LibraryServiceProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import org.zalando.logbook.Logbook
import org.zalando.logbook.netty.LogbookClientHandler
import reactor.netty.http.client.HttpClient

@Configuration("libraryServiceConfiguration")
@ConditionalOnProperty("client.mode", havingValue = "web-client")
@EnableConfigurationProperties(LibraryServiceProperties::class)
internal class WebClientConfiguration(
    private val properties: LibraryServiceProperties
) {

    @Bean
    fun libraryService(logbook: Logbook?): LibraryService {
        val webClient = WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient(logbook)))
            .build()
        return WebClientBasedLibraryService(webClient, properties)
    }

    private fun httpClient(logbook: Logbook?) = HttpClient.create()
        .doOnConnected { connection ->
            if (logbook != null) connection.addHandlerLast(LogbookClientHandler(logbook))
        }

}
