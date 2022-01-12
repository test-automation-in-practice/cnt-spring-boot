package kafka

import kafka.books.Examples
import kafka.books.createdEvent
import kafka.books.deletedEvent
import kafka.events.PublishEventFunction
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

@Component
class ApplicationDemo(
    private val publishEvent: PublishEventFunction
) : CommandLineRunner {

    override fun run(vararg args: String) {
        publishEvent(Examples.cleanCode.createdEvent())
        publishEvent(Examples.cleanCode.deletedEvent())
    }

}