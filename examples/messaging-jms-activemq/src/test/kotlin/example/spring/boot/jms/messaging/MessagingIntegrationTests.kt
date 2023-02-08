package example.spring.boot.jms.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import example.spring.boot.jms.activemq.InitializeWithEmbeddedActiveMq
import example.spring.boot.jms.business.Examples.cleanCode
import example.spring.boot.jms.business.createdEvent
import example.spring.boot.jms.business.deletedEvent
import example.spring.boot.jms.events.EventHandler
import example.spring.boot.jms.events.PublishEventFunction
import example.spring.boot.jms.replacements.JmsTemplate
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@InitializeWithEmbeddedActiveMq
@MockkBean(EventHandler::class, DeadLetterHandler::class, relaxed = true)
@SpringBootTest(classes = [MessagingIntegrationTestsConfiguration::class])
internal class MessagingIntegrationTests(
    @Autowired val objectMapper: ObjectMapper,
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
        verify(timeout = 1_000) { deadLetterHandler.onMessage(any()) }
    }

    @Test
    fun `dead letter queue is used in case a message cannot be read`() {
        manuallySendEventMessage(DummyEvent("bar"))
        verify(timeout = 1_000) { deadLetterHandler.onMessage(any()) }
    }

    fun manuallySendEventMessage(event: Any) =
        jmsTemplate.convertAndSend("queues.events.book-created", event)

    data class DummyEvent(val foo: String)
}

@ComponentScan
@ImportAutoConfiguration(JacksonAutoConfiguration::class)
private class MessagingIntegrationTestsConfiguration
