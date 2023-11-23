package example.spring.boot.http.clients.gateways.libraryservice.webclient

import example.spring.boot.http.clients.gateways.libraryservice.LibraryService
import example.spring.boot.http.clients.gateways.libraryservice.LibraryServiceProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import org.zalando.logbook.Logbook
import org.zalando.logbook.netty.LogbookClientHandler
import reactor.netty.http.client.HttpClient

@Configuration("libraryServiceConfiguration")
@ConditionalOnProperty("client.mode", havingValue = "web-client-declarative")
@EnableConfigurationProperties(LibraryServiceProperties::class)
internal class DeclarativeWebClientConfiguration(
    private val properties: LibraryServiceProperties
) {

    @Bean
    fun libraryService(logbook: Logbook?): LibraryService {
        val webClient = WebClient.builder()
            .baseUrl(properties.baseUrl)
            .clientConnector(ReactorClientHttpConnector(httpClient(logbook)))
            .build()
        val proxyFactory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(webClient)).build()
        val client = proxyFactory.createClient(LibraryServiceClient::class.java)

        return DeclarativeWebClientBasedLibraryService(client)
    }

    private fun httpClient(logbook: Logbook?) = HttpClient.create()
        .doOnConnected { connection ->
            if (logbook != null) connection.addHandlerLast(LogbookClientHandler(logbook))
        }

}
