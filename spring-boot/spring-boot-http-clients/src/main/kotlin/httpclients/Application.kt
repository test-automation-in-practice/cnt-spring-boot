package httpclients

import httpclients.gateways.libraryservice.LibraryServiceProperties
import okhttp3.OkHttpClient
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
@EnableConfigurationProperties(LibraryServiceProperties::class)
class Application {

    @Bean
    fun okHttpClient(): OkHttpClient = OkHttpClient()

}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
