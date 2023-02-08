package example.spring.boot.jms.events

import example.spring.boot.jms.business.BookCreatedEvent
import example.spring.boot.jms.business.BookDeletedEvent
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component

@Component
class EventHandler {

    private val log = getLogger(javaClass)

    fun handleCreatedEvent(event: BookCreatedEvent) {
        log.info("Book was created: $event")
    }

    fun handleDeletedEvent(event: BookDeletedEvent) {
        log.info("Book was deleted: $event")
    }

}
