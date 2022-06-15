package jms.messaging

import jms.books.BookCreatedEvent
import jms.books.BookDeletedEvent
import jms.books.BookEvent
import jms.events.PublishEventFunction
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Component

@Component
class PublishEventViaMessagingFunction(
    private val jmsTemplate: JmsTemplate
) : PublishEventFunction {

    override operator fun invoke(event: BookEvent) {
        val destination = when (event) {
            is BookCreatedEvent -> "queues.events.book-created"
            is BookDeletedEvent -> "queues.events.book-deleted"
        }
        jmsTemplate.convertAndSend(destination, event)
    }

}
