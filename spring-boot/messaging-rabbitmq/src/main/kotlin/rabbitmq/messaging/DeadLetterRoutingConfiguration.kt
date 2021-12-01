package rabbitmq.messaging

import org.springframework.amqp.core.*
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
