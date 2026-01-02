package example.spring.boot.http.clients.gateways.libraryservice.resttemplate

import example.spring.boot.http.clients.gateways.libraryservice.LibraryService
import example.spring.boot.http.clients.gateways.libraryservice.LibraryServiceProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter
import org.springframework.web.client.RestTemplate
import org.zalando.logbook.Logbook
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor
import tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.jacksonMapperBuilder

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
        restTemplate.messageConverters = listOf(JacksonJsonHttpMessageConverter(mapperBuilder()))
        restTemplate.interceptors.add(LogbookClientHttpRequestInterceptor(logbook))

        return RestTemplateBasedLibraryService(restTemplate, properties)
    }

    private fun mapperBuilder(): JsonMapper.Builder =
        jacksonMapperBuilder().configure(FAIL_ON_UNKNOWN_PROPERTIES, false)

}
