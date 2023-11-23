package example.spring.boot.http.clients.gateways.libraryservice.restclient

import example.spring.boot.http.clients.gateways.libraryservice.LibraryService
import example.spring.boot.http.clients.gateways.libraryservice.LibraryServiceProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import org.zalando.logbook.Logbook
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor

@Configuration("libraryServiceConfiguration")
@ConditionalOnProperty("client.mode", havingValue = "rest-client-declarative")
@EnableConfigurationProperties(LibraryServiceProperties::class)
internal class DeclarativeRestClientConfiguration(
    private val properties: LibraryServiceProperties
) {

    @Bean
    fun libraryService(logbook: Logbook?): LibraryService {
        val restClient = RestClient.builder()
            .baseUrl(properties.baseUrl)
            .apply {
                if (logbook != null) {
                    it.requestInterceptor(LogbookClientHttpRequestInterceptor(logbook))
                }
            }
            .build()

        val proxyFactory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build()
        val client = proxyFactory.createClient(LibraryServiceClient::class.java)

        return DeclarativeWebClientBasedLibraryService(client)
    }

}
