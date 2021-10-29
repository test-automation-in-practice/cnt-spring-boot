package amqp.messaging

import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import java.time.Duration

class RabbitMQExtension : BeforeAllCallback {

    override fun beforeAll(context: ExtensionContext) {
        if (context.container == null) {
            val container = RabbitMqContainer().apply { start() }
            context.container = container
            System.setProperty("RABBIT_MQ_PORT", "${container.getMappedPort(5672)}")
        }
    }

    private var ExtensionContext.container: RabbitMqContainer?
        get() = getStore(GLOBAL).get("RABBIT_MQ_CONTAINER", RabbitMqContainer::class.java)
        set(value) = getStore(GLOBAL).put("RABBIT_MQ_CONTAINER", value)

}

private class RabbitMqContainer : GenericContainer<RabbitMqContainer>("rabbitmq:3.6"), CloseableResource {

    init {
        setWaitStrategy(
            LogMessageWaitStrategy()
                .withRegEx(".*Server startup complete.*\n")
                .withStartupTimeout(Duration.ofSeconds(30))
        )
        addExposedPort(5672)
    }

    override fun close() {
        stop()
    }

}
