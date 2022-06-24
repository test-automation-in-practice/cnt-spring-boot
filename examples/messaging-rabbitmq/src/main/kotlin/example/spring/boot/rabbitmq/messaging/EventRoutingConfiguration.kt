package example.spring.boot.rabbitmq.messaging

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.QueueBuilder
import org.springframework.amqp.core.TopicExchange
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EventRoutingConfiguration {

    // exchanges

    @Bean
    fun eventExchange() = TopicExchange("exchanges.events")

    // queues

    @Bean
    fun bookCreatedEventQueue() = durableQueue("queues.events.book-created")

    @Bean
    fun bookDeletedEventQueue() = durableQueue("queues.events.book-deleted")

    // bindings

    // Exchange(book-events) --- [if: type = book-created] ---> Queue(book-created-events)
    @Bean
    fun bookCreatedEventBinding() = eventBinding("queues.events.book-created", "book-created")

    // Exchange(book-events) --- [if: type = book-deleted] ---> Queue(book-deleted-events)
    @Bean
    fun bookDeletedEventBinding() = eventBinding("queues.events.book-deleted", "book-deleted")

    // factory methods

    fun durableQueue(name: String): Queue = QueueBuilder.durable(name)
        .withArgument("x-dead-letter-exchange", "exchanges.dead-letters")
        .build()

    fun eventBinding(queueName: String, routingKey: String): Binding =
        Binding(queueName, Binding.DestinationType.QUEUE, "exchanges.events", routingKey, emptyMap())

}
