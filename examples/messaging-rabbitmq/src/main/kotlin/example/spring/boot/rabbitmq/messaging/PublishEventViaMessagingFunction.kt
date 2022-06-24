package example.spring.boot.rabbitmq.messaging

import example.spring.boot.rabbitmq.business.BookEvent
import example.spring.boot.rabbitmq.events.PublishEventFunction
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.stereotype.Component

@Component
class PublishEventViaMessagingFunction(
    private val amqpTemplate: AmqpTemplate
) : PublishEventFunction {

    override operator fun invoke(event: BookEvent) {
        amqpTemplate.convertAndSend("exchanges.events", event.type, event)
    }

}
