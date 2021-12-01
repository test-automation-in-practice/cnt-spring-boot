package rabbitmq

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import rabbitmq.books.Examples
import rabbitmq.books.createdEvent
import rabbitmq.books.deletedEvent
import rabbitmq.events.PublishEventFunction

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
