package springbootamqp.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.listener.MessageListenerContainer
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springbootamqp.foo.EventHandler

private const val EXCHANGE_NAME = "foo-events"
private const val CREATED_ROUTING_KEY = "created"
private const val DELETED_ROUTING_KEY = "deleted"
private const val CREATED_QUEUE_NAME = "$EXCHANGE_NAME.$CREATED_ROUTING_KEY"
private const val DELETED_QUEUE_NAME = "$EXCHANGE_NAME.$DELETED_ROUTING_KEY"

@Qualifier @Retention annotation class FooEvents
@Qualifier @Retention annotation class FooCreated
@Qualifier @Retention annotation class FooDeleted

@Configuration
class MessagingConfiguration {

    @Bean fun jsonMessageConverter(objectMapper: ObjectMapper) = Jackson2JsonMessageConverter(objectMapper)

    @Bean fun rabbitTemplate(connectionFactory: ConnectionFactory, messageConverter: MessageConverter) =
            RabbitTemplate(connectionFactory).also {
                it.messageConverter = messageConverter
            }

    @Bean @FooEvents fun fooEventsExchange() = TopicExchange(EXCHANGE_NAME)
    @Bean @FooCreated fun fooCreatedQueue() = Queue(CREATED_QUEUE_NAME, true)
    @Bean @FooDeleted fun fooDeletedQueue() = Queue(DELETED_QUEUE_NAME, true)

    @Bean fun fooCreatedBinding(@FooCreated queue: Queue, @FooEvents exchange: TopicExchange): Binding =
            BindingBuilder.bind(queue).to(exchange).with(CREATED_ROUTING_KEY)

    @Bean fun fooDeletedBinding(@FooDeleted queue: Queue, @FooEvents exchange: TopicExchange): Binding =
            BindingBuilder.bind(queue).to(exchange).with(DELETED_ROUTING_KEY)

    @Bean fun fooCreatedMessageContainer(
            connectionFactory: ConnectionFactory,
            eventHandler: EventHandler,
            messageConverter: MessageConverter
    ): MessageListenerContainer {
        val messageListener = MessageListenerAdapter(eventHandler, messageConverter)
        messageListener.addQueueOrTagToMethodName(CREATED_QUEUE_NAME, "handleCreatedEvent")
        messageListener.addQueueOrTagToMethodName(DELETED_QUEUE_NAME, "handleDeletedEvent")

        val messageListenerContainer = SimpleMessageListenerContainer(connectionFactory)
        messageListenerContainer.setQueueNames(CREATED_QUEUE_NAME, DELETED_QUEUE_NAME)
        messageListenerContainer.setMessageListener(messageListener)
        return messageListenerContainer
    }


}