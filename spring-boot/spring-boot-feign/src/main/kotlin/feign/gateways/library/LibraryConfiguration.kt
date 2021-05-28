package feign.gateways.library

import feign.*
import feign.Target
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import feign.slf4j.Slf4jLogger
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import kotlin.reflect.KClass

@Configuration
@ComponentScan
@EnableConfigurationProperties(LibrarySettings::class)
internal class LibraryConfiguration {

    @Bean fun libraryClient(settings: LibrarySettings): LibraryClient {
        val target = DynamicUrlTarget("library", LibraryClient::class) { settings.url }
        return Feign.builder()
            .encoder(JacksonEncoder())
            .decoder(JacksonDecoder())
            .logger(Slf4jLogger("utils.feign.library"))
            .logLevel(settings.logLevel)
            .retryer(Retryer.NEVER_RETRY)
            .target(target)
    }

}

private class DynamicUrlTarget<T : Any>(
    private val name: String,
    private val type: KClass<T>,
    private val urlSupplier: () -> String
) : Target<T> {

    override fun type() = type.java
    override fun name() = name
    override fun url() = urlSupplier()

    override fun apply(input: RequestTemplate): Request {
        input.target(url())
        return input.request()
    }

}