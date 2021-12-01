package rabbitmq.messaging

import org.springframework.amqp.core.AmqpTemplate
import org.springframework.stereotype.Component
import rabbitmq.books.BookEvent
import rabbitmq.events.PublishEventFunction

@Component
class PublishEventViaMessagingFunction(
    private val amqpTemplate: AmqpTemplate
) : PublishEventFunction {

    override operator fun invoke(event: BookEvent) {
        amqpTemplate.convertAndSend("exchanges.events", event.type, event)
    }

}
