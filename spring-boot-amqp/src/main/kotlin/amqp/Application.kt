package amqp

import amqp.books.BookCreated
import amqp.books.BookDeleted
import amqp.books.BookEventDispatcher
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import java.util.*

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

@Component
class ApplicationDemo(
    private val eventDispatcher: BookEventDispatcher
) : CommandLineRunner {

    override fun run(vararg args: String) {
        eventDispatcher.dispatch(BookCreated(UUID.randomUUID(), "abc"))
        eventDispatcher.dispatch(BookCreated(UUID.randomUUID(), "def"))
        eventDispatcher.dispatch(BookDeleted(UUID.randomUUID(), "ghi"))
    }

}