package example.spring.boot.jdbc.utils

import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource
import org.testcontainers.containers.PostgreSQLContainer
import kotlin.annotation.AnnotationTarget.CLASS

@Retention
@Target(CLASS)
@ExtendWith(PostgreSQLExtension::class)
annotation class RunWithDockerizedPostgreSQL

private class PostgreSQLExtension : BeforeAllCallback {

    override fun beforeAll(context: ExtensionContext) {
        if (context.container == null) {
            val container = CloseableRedisContainerResource().apply { start() }
            System.setProperty("POSTGRESQL_URL", container.jdbcUrl)
            System.setProperty("POSTGRESQL_USERNAME", container.username)
            System.setProperty("POSTGRESQL_PASSWORD", container.password)
            context.container = container
        }
    }

    private var ExtensionContext.container: CloseableRedisContainerResource?
        get() = getStore(GLOBAL).get("POSTGRESQL_CONTAINER", CloseableRedisContainerResource::class.java)
        set(value) = getStore(GLOBAL).put("POSTGRESQL_CONTAINER", value)

}

private class CloseableRedisContainerResource :
    PostgreSQLContainer<CloseableRedisContainerResource>("postgres:14.3"), CloseableResource {
    override fun close() = stop()
}
