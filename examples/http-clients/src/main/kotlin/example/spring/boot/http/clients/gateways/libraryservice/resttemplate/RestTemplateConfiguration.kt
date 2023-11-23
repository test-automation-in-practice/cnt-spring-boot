package example.spring.boot.http.clients.gateways.libraryservice.resttemplate

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import example.spring.boot.http.clients.gateways.libraryservice.LibraryService
import example.spring.boot.http.clients.gateways.libraryservice.LibraryServiceProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate
import org.zalando.logbook.Logbook
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor

@Configuration("libraryServiceConfiguration")
@ConditionalOnProperty("client.mode", havingValue = "rest-template")
@EnableConfigurationProperties(LibraryServiceProperties::class)
internal class RestTemplateConfiguration(
    private val properties: LibraryServiceProperties
) {

    @Bean
    fun libraryService(logbook: Logbook?): LibraryService {
        val restTemplate = RestTemplate()
        restTemplate.requestFactory = JdkClientHttpRequestFactory()
        restTemplate.messageConverters = listOf(MappingJackson2HttpMessageConverter(objectMapper()))
        restTemplate.interceptors.add(LogbookClientHttpRequestInterceptor(logbook))

        return RestTemplateBasedLibraryService(restTemplate, properties)
    }

    private fun objectMapper(): ObjectMapper =
        jacksonObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false)

}
