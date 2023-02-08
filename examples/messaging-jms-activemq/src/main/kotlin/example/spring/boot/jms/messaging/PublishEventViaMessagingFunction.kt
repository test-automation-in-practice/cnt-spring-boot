package example.spring.boot.jms.messaging

import example.spring.boot.jms.business.BookCreatedEvent
import example.spring.boot.jms.business.BookDeletedEvent
import example.spring.boot.jms.business.BookEvent
import example.spring.boot.jms.events.PublishEventFunction
import example.spring.boot.jms.replacements.JmsTemplate
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
