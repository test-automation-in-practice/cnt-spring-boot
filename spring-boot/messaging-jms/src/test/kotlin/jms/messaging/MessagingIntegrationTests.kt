package jms.messaging

import com.ninjasquad.springmockk.SpykBean
import io.mockk.every
import io.mockk.verify
import jms.books.Examples.cleanCode
import jms.books.createdEvent
import jms.books.deletedEvent
import jms.events.EventHandler
import jms.events.PublishEventFunction
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.jms.annotation.EnableJms
import org.springframework.jms.core.JmsTemplate

@SpykBean(EventHandler::class, DeadLetterHandler::class)
@SpringBootTest(classes = [MessagingIntegrationTestsConfiguration::class])
internal class MessagingIntegrationTests(
    @Autowired val eventHandler: EventHandler,
    @Autowired val deadLetterHandler: DeadLetterHandler,
    @Autowired val publishEvent: PublishEventFunction,
    @Autowired val jmsTemplate: JmsTemplate
) {

    val createdEvent = cleanCode.createdEvent()
    val deletedEvent = cleanCode.deletedEvent()

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

    @Test
    fun `dead letter queue is used in case of an exception`() {
        every { eventHandler.handleDeletedEvent(deletedEvent) } throws RuntimeException("oops")
        publishEvent(deletedEvent)
        verify(timeout = 1_000) { eventHandler.handleDeletedEvent(deletedEvent) }
        verify(timeout = 1_000) { deadLetterHandler.handleDeadLetter(any()) }
    }

    @Test
    fun `dead letter queue is used in case a message cannot be read`() {
        manuallySendEventMessage(DummyEvent("bar"))
        verify(timeout = 1_000) { deadLetterHandler.handleDeadLetter(any()) }
    }

    fun manuallySendEventMessage(event: Any) =
        jmsTemplate.convertAndSend("queues.events.book-created", event)

    data class DummyEvent(val foo: String)
}

@EnableJms
@ComponentScan
@ImportAutoConfiguration(ActiveMQAutoConfiguration::class, ArtemisAutoConfiguration::class)
private class MessagingIntegrationTestsConfiguration
