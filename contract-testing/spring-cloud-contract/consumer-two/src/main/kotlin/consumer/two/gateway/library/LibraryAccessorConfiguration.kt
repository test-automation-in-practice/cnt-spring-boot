package consumer.two.gateway.library

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(LibraryAccessorSettings::class)
class LibraryAccessorConfiguration
