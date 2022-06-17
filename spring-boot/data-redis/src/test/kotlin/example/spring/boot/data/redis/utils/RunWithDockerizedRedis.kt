package example.spring.boot.data.redis.utils

import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource
import org.testcontainers.containers.GenericContainer
import kotlin.annotation.AnnotationTarget.CLASS

@Retention
@Target(CLASS)
@ExtendWith(RedisExtension::class)
annotation class RunWithDockerizedRedis

private class RedisExtension : BeforeAllCallback {

    override fun beforeAll(context: ExtensionContext) {
        if (context.container == null) {
            val container = CloseableRedisContainerResource().apply { start() }
            System.setProperty("REDIS_PORT", "${container.getMappedPort(6379)}")
            context.container = container
        }
    }

    private var ExtensionContext.container: CloseableRedisContainerResource?
        get() = getStore(GLOBAL).get("REDIS_CONTAINER", CloseableRedisContainerResource::class.java)
        set(value) = getStore(GLOBAL).put("REDIS_CONTAINER", value)

}

private class CloseableRedisContainerResource : RedisContainer(), CloseableResource {
    init {
        addExposedPort(6379)
    }

    override fun close() = stop()
}

private open class RedisContainer(version: String = "7.0") : GenericContainer<RedisContainer>("redis:$version")
