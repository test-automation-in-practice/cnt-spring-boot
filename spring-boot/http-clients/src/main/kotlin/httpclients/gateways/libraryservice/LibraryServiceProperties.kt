package httpclients.gateways.libraryservice

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("library-service")
internal class LibraryServiceProperties(
    var baseUrl: String
)
