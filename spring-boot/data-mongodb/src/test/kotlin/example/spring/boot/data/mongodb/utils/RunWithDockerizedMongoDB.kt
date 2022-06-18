package example.spring.boot.data.mongodb.utils

import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource
import org.testcontainers.containers.MongoDBContainer

@Retention
@Target(AnnotationTarget.CLASS)
@ExtendWith(MongoDBExtension::class)
annotation class RunWithDockerizedMongoDB

private class MongoDBExtension : BeforeAllCallback {

    override fun beforeAll(context: ExtensionContext) {
        if (context.container == null) {
            val container = CloseableMongoDBContainerResource().apply { start() }
            System.setProperty("MONGODB_PORT", "${container.getMappedPort(27017)}")
            context.container = container
        }
    }

    private var ExtensionContext.container: CloseableMongoDBContainerResource?
        get() = getStore(GLOBAL).get("MONGODB_CONTAINER", CloseableMongoDBContainerResource::class.java)
        set(value) = getStore(GLOBAL).put("MONGODB_CONTAINER", value)

}

private class CloseableMongoDBContainerResource : MongoDBContainer("mongo:4.0.10"), CloseableResource {
    init {
        addExposedPort(27017)
    }

    override fun close() = stop()
}
