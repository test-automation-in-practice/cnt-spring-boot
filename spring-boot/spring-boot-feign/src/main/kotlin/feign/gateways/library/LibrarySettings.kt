package feign.gateways.library

import feign.Logger
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("library-service")
internal class LibrarySettings {
    lateinit var url: String
    lateinit var logLevel: Logger.Level
}