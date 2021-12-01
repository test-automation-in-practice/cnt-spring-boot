package rabbitmq.events

import org.slf4j.LoggerFactory.getLogger
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import rabbitmq.books.BookCreatedEvent
import rabbitmq.books.BookDeletedEvent

@Component
class EventHandler {

    private val log = getLogger(javaClass)

    @RabbitListener(queues = ["queues.events.book-created"])
    fun handleCreatedEvent(event: BookCreatedEvent) {
        log.info("Book was created: ${event}")
    }

    @RabbitListener(queues = ["queues.events.book-deleted"])
    fun handleDeletedEvent(event: BookDeletedEvent) {
        log.info("Book was deleted: ${event}")
    }

}
