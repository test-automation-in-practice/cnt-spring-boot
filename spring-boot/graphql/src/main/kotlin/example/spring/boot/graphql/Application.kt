package example.spring.boot.graphql

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.util.IdGenerator
import org.springframework.util.JdkIdGenerator

@SpringBootApplication
class Application {
    @Bean
    fun idGenerator(): IdGenerator = JdkIdGenerator()
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
