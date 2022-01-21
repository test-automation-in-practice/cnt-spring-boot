package consumer.one.gateway.library

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("services.library")
data class LibraryAccessorSettings(
    var url: String // needs to be var in order to be overrideable in tests - generally a good idea
)
