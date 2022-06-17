package example.spring.boot.kafka.utils

import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName.parse

class KafkaExtension : BeforeAllCallback {

    override fun beforeAll(context: ExtensionContext) {
        if (context.container == null) {
            val container = CloseableKafkaContainerResource().apply { start() }
            System.setProperty("KAFKA_SERVER", container.bootstrapServers)
            context.container = container
        }
    }

    private var ExtensionContext.container: KafkaContainer?
        get() = getStore(GLOBAL).get("KAFKA_CONTAINER", KafkaContainer::class.java)
        set(value) = getStore(GLOBAL).put("KAFKA_CONTAINER", value)

}

private class CloseableKafkaContainerResource :
    KafkaContainer(parse("confluentinc/cp-kafka:6.2.1")), CloseableResource {
    override fun close() = stop()
}
