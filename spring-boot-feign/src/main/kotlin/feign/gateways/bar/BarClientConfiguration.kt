package feign.gateways.bar

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import feign.Feign
import feign.Request
import feign.RequestTemplate
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
@EnableConfigurationProperties(BarSettings::class)
internal class BarClientConfiguration {

    @Bean fun barClient(settings: BarSettings): BarClient {
        val target = DynamicUrlTarget("bar", BarClient::class) { settings.url }
        val objectMapper = ObjectMapper().apply {
            registerModule(KotlinModule())
        }
        return Feign.builder()
                .encoder(JacksonEncoder())
                .decoder(JacksonDecoder(objectMapper))
                .logger(Slf4jLogger("utils.feign.bar"))
                .logLevel(settings.logLevel)
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
        input.insert(0, url())
        return input.request()
    }

}