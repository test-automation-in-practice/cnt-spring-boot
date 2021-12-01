package amqp.messaging

import amqp.books.EventHandler
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.listener.MessageListenerContainer
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

private const val EXCHANGE_NAME = "book-events"
private const val CREATED_ROUTING_KEY = "book-created"
private const val DELETED_ROUTING_KEY = "book-deleted"
private const val CREATED_QUEUE_NAME = "$EXCHANGE_NAME.$CREATED_ROUTING_KEY"
private const val DELETED_QUEUE_NAME = "$EXCHANGE_NAME.$DELETED_ROUTING_KEY"

@Qualifier @Retention annotation class BookEvents
@Qualifier @Retention annotation class BookCreated
@Qualifier @Retention annotation class BookDeleted

@Configuration
class MessagingConfiguration {

    @Bean fun jsonMessageConverter(objectMapper: ObjectMapper) = Jackson2JsonMessageConverter(objectMapper)

    @Bean fun rabbitTemplate(connectionFactory: ConnectionFactory, messageConverter: MessageConverter) =
        RabbitTemplate(connectionFactory).also {
            it.messageConverter = messageConverter
        }

    @Bean @BookEvents fun bookEventsExchange() = TopicExchange(EXCHANGE_NAME)
    @Bean @BookCreated fun bookCreatedQueue() = Queue(CREATED_QUEUE_NAME, true)
    @Bean @BookDeleted fun bookDeletedQueue() = Queue(DELETED_QUEUE_NAME, true)

    @Bean fun bookCreatedBinding(@BookCreated queue: Queue, @BookEvents exchange: TopicExchange): Binding =
        BindingBuilder.bind(queue).to(exchange).with(CREATED_ROUTING_KEY)

    @Bean fun bookDeletedBinding(@BookDeleted queue: Queue, @BookEvents exchange: TopicExchange): Binding =
        BindingBuilder.bind(queue).to(exchange).with(DELETED_ROUTING_KEY)

    @Bean fun bookEventMessageContainer(
        connectionFactory: ConnectionFactory,
        eventHandler: EventHandler,
        objectMapper: ObjectMapper
    ): MessageListenerContainer {
        val messageListener = MessageListener { msg ->
            when (msg.messageProperties.consumerQueue) {
                CREATED_QUEUE_NAME -> eventHandler.handleCreatedEvent(
                    objectMapper.readValue(
                        msg.body,
                        amqp.books.BookCreated::class.java
                    )
                )
                DELETED_QUEUE_NAME -> eventHandler.handleDeletedEvent(
                    objectMapper.readValue(
                        msg.body,
                        amqp.books.BookDeleted::class.java
                    )
                )
            }
        }

        val messageListenerContainer = SimpleMessageListenerContainer(connectionFactory)
        messageListenerContainer.setQueueNames(CREATED_QUEUE_NAME, DELETED_QUEUE_NAME)
        messageListenerContainer.setMessageListener(messageListener)
        return messageListenerContainer
    }


}
