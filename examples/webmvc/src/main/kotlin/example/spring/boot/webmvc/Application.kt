package example.spring.boot.webmvc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.hateoas.config.EnableHypermediaSupport
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType.HAL
import org.zalando.logbook.HttpLogFormatter
import org.zalando.logbook.logstash.LogstashLogbackSink

@SpringBootApplication
@EnableHypermediaSupport(type = [HAL])
class Application {

    @Bean
    @Profile("json-logging")
    fun jsonLoggingSinkForLogbook(formatter: HttpLogFormatter) =
        LogstashLogbackSink(formatter, "http")
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
