package example.spring.boot.rabbitmq.messaging

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.QueueBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DeadLetterRoutingConfiguration {

    // exchanges

    @Bean
    fun deadLetterExchange(): FanoutExchange = FanoutExchange("exchanges.dead-letters")

    // queues

    @Bean
    fun deadLetterQueue(): Queue = QueueBuilder.durable("queues.dead-letters").build()

    // bindings

    @Bean
    fun deadLetterQueueBinding(): Binding = BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange())

}
