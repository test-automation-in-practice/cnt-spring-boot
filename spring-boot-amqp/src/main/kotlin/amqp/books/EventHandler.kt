package amqp.books

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class EventHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    fun handleCreatedEvent(event: BookCreated) {
        log.info("Book [${event.title}] was created!")
    }

    fun handleDeletedEvent(event: BookDeleted) {
        log.info("Book [${event.title}] was deleted!")
    }

}