package example.spring.boot.http.clients.gateways.libraryservice

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("library-service")
internal class LibraryServiceProperties(
    var baseUrl: String
)
