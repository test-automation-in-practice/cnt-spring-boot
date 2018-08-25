package amqp.messaging

import org.springframework.amqp.core.TopicExchange
import org.springframework.stereotype.Component
import amqp.books.BookEvent
import amqp.books.BookEventDispatcher
import org.springframework.amqp.core.AmqpTemplate

@Component
class MessagingEventDispatcher(
        private val amqpTemplate: AmqpTemplate,
        @BookEvents private val exchange: TopicExchange
) : BookEventDispatcher {

    override fun dispatch(event: BookEvent) {
        amqpTemplate.convertAndSend(exchange.name, event.type, event)
    }

}