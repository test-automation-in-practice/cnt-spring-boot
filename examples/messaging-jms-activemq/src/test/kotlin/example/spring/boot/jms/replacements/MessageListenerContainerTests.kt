package example.spring.boot.jms.replacements

import example.spring.boot.jms.activemq.ActiveMqConfiguration
import example.spring.boot.jms.activemq.InitializeWithEmbeddedActiveMq
import org.apache.activemq.broker.BrokerService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.lang.Thread.sleep
import javax.jms.ConnectionFactory
import javax.jms.Message
import javax.jms.MessageListener
import javax.jms.TextMessage

private const val QUEUE_NAME = "test-queue"

@ActiveProfiles("test")
@InitializeWithEmbeddedActiveMq
@SpringBootTest(classes = [ResilienceTestsConfiguration::class])
internal class MessageListenerContainerTests(
    @Autowired val messageListener: TestMessageListener,
    @Autowired val jmsTemplate: JmsTemplate,
    @Autowired val broker: BrokerService
) {

    @Test
    fun `dropped connections will be re-established`() {
        val initial = mutableListOf<String>()
        val beforeRestart = mutableListOf<String>()
        val afterRestart = mutableListOf<String>()

        messageListener.collector = initial

        repeat(250) { number ->
            jmsTemplate.send(QUEUE_NAME) { it.createTextMessage("${10_000 + number}") }
        }
        stopBroker()
        sleep(1_000)
        messageListener.collector = beforeRestart
        startBroker()
        messageListener.collector = afterRestart
        sleep(5_000)

        assertThat(initial).`as`("initial").isNotEmpty()
        assertThat(beforeRestart).`as`("beforeRestart").isEmpty()
        assertThat(afterRestart).`as`("afterRestart").isNotEmpty()
    }

    fun stopBroker() {
        println("stopping broker")
        broker.stop()
        println("broker stopped")
    }

    fun startBroker() {
        println("starting broker")
        broker.start(true)
        println("broker started")
    }

}

@Import(ActiveMqConfiguration::class, JmsTemplate::class)
@ImportAutoConfiguration(JacksonAutoConfiguration::class)
private class ResilienceTestsConfiguration {

    @Bean
    fun testMessageListenerContainer(
        connectionFactory: ConnectionFactory,
        messageListener: TestMessageListener
    ) = MessageListenerContainer(
        connectionFactory = connectionFactory,
        messageListener = messageListener,
        queueName = QUEUE_NAME
    )

    @Bean
    fun testMessageListener() = TestMessageListener()
}

internal class TestMessageListener : MessageListener {

    var collector: MutableList<String>? = null

    override fun onMessage(message: Message) {
        collector?.add((message as TextMessage).text)
        sleep(100)
    }

}
