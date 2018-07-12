package feign.gateways.bar

import feign.Logger
import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties("bar-service")
internal class BarSettings {
    lateinit var url: String
    lateinit var logLevel: Logger.Level
}