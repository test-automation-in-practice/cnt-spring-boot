package amqp.messaging

import amqp.books.BookCreated
import amqp.books.BookDeleted
import amqp.books.EventHandler
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import java.util.UUID

@ComponentScan
@ImportAutoConfiguration(
    RabbitAutoConfiguration::class,
    JacksonAutoConfiguration::class
)
private class MessagingEventDispatcherTestConfiguration {
    @Bean fun eventHandler(): EventHandler = mockk()
}

@ExtendWith(RabbitMQExtension::class)
@SpringBootTest(
    classes = [MessagingEventDispatcherTestConfiguration::class],
    properties = ["spring.rabbitmq.port=\${RABBIT_MQ_PORT}"]
)
internal class MessagingEventDispatcherTest(
    @Autowired val eventHandler: EventHandler,
    @Autowired val cut: MessagingEventDispatcher
) {

    @Test fun `book created events are dispatched and received`() {
        val event = BookCreated(UUID.randomUUID(), "Clean Code")
        cut.dispatch(event)
        verify(timeout = 1_000) { eventHandler.handleCreatedEvent(event) }
    }

    @Test fun `book deleted events are dispatched and received`() {
        val event = BookDeleted(UUID.randomUUID(), "Clean Architecture")
        cut.dispatch(event)
        verify(timeout = 1_000) { eventHandler.handleDeletedEvent(event) }
    }

}
