package example.spring.boot.jms.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import example.spring.boot.jms.activemq.ActiveMqConfiguration
import example.spring.boot.jms.business.BookCreatedEvent
import example.spring.boot.jms.business.BookDeletedEvent
import example.spring.boot.jms.events.EventHandler
import example.spring.boot.jms.replacements.JmsTemplate
import example.spring.boot.jms.replacements.JsonMessageListener
import example.spring.boot.jms.replacements.MessageListenerContainer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import javax.jms.ConnectionFactory
import javax.jms.Session.AUTO_ACKNOWLEDGE

@Configuration
@Import(ActiveMqConfiguration::class)
class MessagingConfiguration {

    @Bean
    fun messageSender(
        connectionFactory: ConnectionFactory,
        objectMapper: ObjectMapper
    ) = JmsTemplate(connectionFactory, objectMapper)

    @Bean
    fun bookCreatedEventMessageConsumer(
        connectionFactory: ConnectionFactory,
        objectMapper: ObjectMapper,
        eventHandler: EventHandler
    ) = MessageListenerContainer(
        connectionFactory = connectionFactory,
        messageListener = JsonMessageListener(
            objectMapper = objectMapper,
            payloadType = BookCreatedEvent::class,
            handler = eventHandler::handleCreatedEvent
        ),
        queueName = "queues.events.book-created",
        sessionAcknowledgeMode = AUTO_ACKNOWLEDGE
    )

    @Bean
    fun bookDeletedEventMessageConsumer(
        connectionFactory: ConnectionFactory,
        objectMapper: ObjectMapper,
        handler: EventHandler
    ) = MessageListenerContainer(
        connectionFactory = connectionFactory,
        messageListener = JsonMessageListener(
            objectMapper = objectMapper,
            payloadType = BookDeletedEvent::class,
            handler = handler::handleDeletedEvent
        ),
        queueName = "queues.events.book-deleted",
        sessionAcknowledgeMode = AUTO_ACKNOWLEDGE
    )

    @Bean
    fun deadLetterMessageConsumer(
        connectionFactory: ConnectionFactory,
        objectMapper: ObjectMapper,
        handler: DeadLetterHandler
    ) = MessageListenerContainer(
        connectionFactory = connectionFactory,
        messageListener = handler,
        queueName = "ActiveMQ.DLQ",
        sessionAcknowledgeMode = AUTO_ACKNOWLEDGE
    )

}
