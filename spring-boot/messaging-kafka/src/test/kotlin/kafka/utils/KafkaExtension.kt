package kafka.utils

import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import org.testcontainers.utility.DockerImageName
import java.time.Duration

class KafkaExtension : BeforeAllCallback {

    override fun beforeAll(context: ExtensionContext) {
        if (context.container == null) {
            val container = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1")).apply {
                setWaitStrategy(
                    LogMessageWaitStrategy()
                        .withRegEx(".*Startup complete..*\n")
                        .withStartupTimeout(Duration.ofSeconds(180))
                )
                start()
            }
            System.setProperty("KAFKA_SERVER", container.bootstrapServers)
            context.container = container
        }
    }

    private var ExtensionContext.container: KafkaContainer?
        get() = getStore(GLOBAL).get("KAFKA_CONTAINER", KafkaContainer::class.java)
        set(value) = getStore(GLOBAL).put("KAFKA_CONTAINER", value)

}