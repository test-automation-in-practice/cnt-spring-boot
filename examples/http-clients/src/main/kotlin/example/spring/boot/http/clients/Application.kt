package example.spring.boot.http.clients

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.zalando.logbook.HttpLogFormatter
import org.zalando.logbook.logstash.LogstashLogbackSink

@SpringBootApplication
class Application {

    @Bean
    @Profile("json-logging")
    fun jsonLoggingSinkForLogbook(formatter: HttpLogFormatter) =
        LogstashLogbackSink(formatter)
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
