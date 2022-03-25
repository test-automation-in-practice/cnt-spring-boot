package rabbitmq.utils

import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import org.testcontainers.utility.DockerImageName.parse
import java.time.Duration

class RabbitMQExtension : BeforeAllCallback {

    override fun beforeAll(context: ExtensionContext) {
        if (context.container == null) {
            val container = CloseableRabbitMqContainerResource().apply { start() }
            System.setProperty("RABBIT_MQ_PORT", "${container.getMappedPort(5672)}")
            context.container = container
        }
    }

    private var ExtensionContext.container: CloseableRabbitMqContainerResource?
        get() = getStore(GLOBAL).get("RABBIT_MQ_CONTAINER", CloseableRabbitMqContainerResource::class.java)
        set(value) = getStore(GLOBAL).put("RABBIT_MQ_CONTAINER", value)

}

private class CloseableRabbitMqContainerResource :
    RabbitMQContainer(parse("rabbitmq:3.8")), CloseableResource {

    init {
        setWaitStrategy(
            LogMessageWaitStrategy()
                .withRegEx(".*Server startup complete.*\n")
                .withStartupTimeout(Duration.ofSeconds(30))
        )
        addExposedPort(5672)
    }

}
