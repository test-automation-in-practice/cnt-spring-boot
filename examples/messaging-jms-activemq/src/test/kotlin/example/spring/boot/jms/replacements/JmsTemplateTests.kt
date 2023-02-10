package example.spring.boot.jms.replacements

import example.spring.boot.jms.activemq.ActiveMqConfiguration
import example.spring.boot.jms.activemq.InitializeWithEmbeddedActiveMq
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.time.Duration.ofSeconds
import java.util.UUID.randomUUID
import javax.jms.Message
import javax.jms.TextMessage

@ActiveProfiles("test")
@InitializeWithEmbeddedActiveMq
@SpringBootTest(classes = [JmsTemplateTestsConfiguration::class])
class JmsTemplateTests(
    @Autowired val jmsTemplate: JmsTemplate
) {

    @Test
    fun `messages can be send to and received from a queue`() {
        val queueName = randomQueueName()
        val body = "message-body"

        jmsTemplate.send(queueName) { createTextMessage(body) }
        val receivedMessage = jmsTemplate.receive(queueName, timeout = ofSeconds(2))

        assertThat(getTextBody(receivedMessage)).isEqualTo(body)
    }

    @Test
    fun `convert and send sends a JSON text messages`() {
        val queueName = randomQueueName()
        val body = mapOf("foo" to 42)

        jmsTemplate.convertAndSend(queueName, body)
        val receivedMessage = jmsTemplate.receive(queueName, timeout = ofSeconds(2))

        assertThat(getTextBody(receivedMessage)).isEqualToIgnoringWhitespace("""{ "foo": 42 }""")
    }

    fun randomQueueName() = "test-queue-${randomUUID()}"
    fun getTextBody(receivedMessage: Message?) = (receivedMessage as? TextMessage)?.text

}

@Import(ActiveMqConfiguration::class, JmsTemplate::class)
@ImportAutoConfiguration(JacksonAutoConfiguration::class)
private class JmsTemplateTestsConfiguration
