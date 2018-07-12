package amqp.messaging

import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component
import amqp.foo.FooEvent
import amqp.foo.FooEventDispatcher

@Component
class MessagingEventDispatcher(
        private val rabbitTemplate: RabbitTemplate,
        @FooEvents private val exchange: TopicExchange
) : FooEventDispatcher {

    override fun dispatch(event: FooEvent) {
        rabbitTemplate.convertAndSend(exchange.name, event.type, event)
    }

}