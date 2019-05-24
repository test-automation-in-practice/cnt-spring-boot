package amqp.messaging

import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import java.time.Duration

class RabbitMQExtension : BeforeAllCallback,
    AfterAllCallback {

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

private class RabbitMqContainer : GenericContainer<RabbitMqContainer>("rabbitmq:3.6") {

    init {
        setWaitStrategy(
            LogMessageWaitStrategy()
                .withRegEx(".*Server startup complete.*\n")
                .withStartupTimeout(Duration.ofSeconds(30))
        )
        addExposedPort(5672)
    }

}