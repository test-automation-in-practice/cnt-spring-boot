package amqp.messaging

import com.nhaarman.mockitokotlin2.timeout
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import amqp.foo.EventHandler
import amqp.foo.FooCreated
import amqp.foo.FooDeleted
import java.time.Duration
import java.util.*

@ComponentScan
@ImportAutoConfiguration(
        RabbitAutoConfiguration::class,
        JacksonAutoConfiguration::class
)
class MessagingEventDispatcherTestConfiguration

@ExtendWith(RabbitMQExtension::class, SpringExtension::class)
@SpringBootTest(
        classes = [MessagingEventDispatcherTestConfiguration::class],
        properties = ["spring.rabbitmq.port=\${RABBIT_MQ_PORT}"]
)
internal class MessagingEventDispatcherTest {

    @SpyBean lateinit var eventHandler: EventHandler
    @Autowired lateinit var cut: MessagingEventDispatcher

    @Test fun `foo created events are dispatched and received`() {
        val event = FooCreated(UUID.randomUUID())
        cut.dispatch(event)
        verify(eventHandler, timeout(1_000)).handleCreatedEvent(event)
    }

    @Test fun `foo deleted events are dispatched and received`() {
        val event = FooDeleted(UUID.randomUUID())
        cut.dispatch(event)
        verify(eventHandler, timeout(1_000)).handleDeletedEvent(event)
    }

}

class RabbitMQExtension : BeforeAllCallback, AfterAllCallback {

    private val container = RabbitMqContainer()

    override fun beforeAll(context: ExtensionContext) {
        if (isTopClassContext(context) && !container.isRunning) {
            container.start()
            System.setProperty("RABBIT_MQ_PORT", "${container.getMappedPort(5672)}")
        }
    }

    override fun afterAll(context: ExtensionContext) {
        if (isTopClassContext(context) && container.isRunning) {
            container.stop()
        }
    }

    private fun isTopClassContext(context: ExtensionContext) = context.parent.orElse(null) == context.root

}

class RabbitMqContainer : GenericContainer<RabbitMqContainer>("rabbitmq:3.6") {

    init {
        setWaitStrategy(LogMessageWaitStrategy()
                .withRegEx(".*Server startup complete.*\n")
                .withStartupTimeout(Duration.ofSeconds(30)))
        addExposedPort(5672)
    }

}