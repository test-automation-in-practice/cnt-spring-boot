package amqp.messaging

import org.springframework.amqp.core.TopicExchange
import org.springframework.stereotype.Component
import amqp.foo.FooEvent
import amqp.foo.FooEventDispatcher
import org.springframework.amqp.core.AmqpTemplate

@Component
class MessagingEventDispatcher(
        private val amqpTemplate: AmqpTemplate,
        @FooEvents private val exchange: TopicExchange
) : FooEventDispatcher {

    override fun dispatch(event: FooEvent) {
        amqpTemplate.convertAndSend(exchange.name, event.type, event)
    }

}