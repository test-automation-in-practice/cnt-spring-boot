package jms.events

import jms.books.BookCreatedEvent
import jms.books.BookDeletedEvent
import org.slf4j.LoggerFactory.getLogger
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

@Component
class EventHandler {

    private val log = getLogger(javaClass)

    @JmsListener(destination = "queues.events.book-created")
    fun handleCreatedEvent(event: BookCreatedEvent) {
        log.info("Book was created: $event")
    }

    @JmsListener(destination = "queues.events.book-deleted")
    fun handleDeletedEvent(event: BookDeletedEvent) {
        log.info("Book was deleted: $event")
    }

}
