package example.spring.boot.kafka.messaging

import com.ninjasquad.springmockk.SpykBean
import example.spring.boot.kafka.business.BookEvent
import example.spring.boot.kafka.business.Examples
import example.spring.boot.kafka.business.createdEvent
import example.spring.boot.kafka.business.deletedEvent
import example.spring.boot.kafka.events.EventHandler
import example.spring.boot.kafka.events.PublishEventFunction
import example.spring.boot.kafka.utils.KafkaExtension
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.kafka.core.KafkaTemplate

@SpykBean(EventHandler::class)
@ExtendWith(KafkaExtension::class)
@SpringBootTest(
    classes = [MessagingIntegrationTestsConfiguration::class],
    properties = ["spring.kafka.bootstrap-servers=\${KAFKA_SERVER}"]
)
class MessagingIntegrationTests(
    @Autowired val eventHandler: EventHandler,
    @Autowired val publishEvent: PublishEventFunction,
    @Autowired val kafkaTemplate: KafkaTemplate<String, BookEvent>
) {

    val createdEvent = Examples.cleanCode.createdEvent()
    val deletedEvent = Examples.cleanCode.deletedEvent()

    @Test
    fun `book created events are dispatched and received`() {
        publishEvent(createdEvent)
        verify(timeout = 1_000) { eventHandler.handleCreatedEvent(createdEvent) }
    }

    @Test
    fun `book deleted events are dispatched and received`() {
        publishEvent(deletedEvent)
        verify(timeout = 1_000) { eventHandler.handleDeletedEvent(deletedEvent) }
    }

    @Test // works by default
    fun `dead letter queue is used in case of an exception`() {
        every { eventHandler.handleDeletedEvent(deletedEvent) } throws RuntimeException("oops")
        publishEvent(deletedEvent)
        verify(timeout = 1_000) { eventHandler.handleDeletedEvent(deletedEvent) }
        verify(timeout = 1_000) { eventHandler.handleDeadLetterEvent(any()) }
    }

    @Test // requires an ErrorHandlingDeserializer
    fun `dead letter queue is used in case a message cannot be read`() {
        manuallySendEventMessage("book-created", DummyEvent("bar"))
        verify(timeout = 1_000) { eventHandler.handleDeadLetterEvent(any()) }
    }

    fun manuallySendEventMessage(topic: String, event: DummyEvent) {
        @Suppress("UNCHECKED_CAST")
        (kafkaTemplate as KafkaTemplate<String, Any>).send(topic, event)
    }

    data class DummyEvent(val foo: String)

}

@ComponentScan
@ImportAutoConfiguration(KafkaAutoConfiguration::class)
private class MessagingIntegrationTestsConfiguration
