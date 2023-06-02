package example.spring.boot.data.redis.utils

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.TestPropertySourceUtils.addInlinedPropertiesToEnvironment
import org.testcontainers.containers.Container
import org.testcontainers.containers.GenericContainer
import java.util.concurrent.atomic.AtomicInteger
import kotlin.annotation.AnnotationTarget.CLASS

@Retention
@Target(CLASS)
@ContextConfiguration(initializers = [RedisInitializer::class])
annotation class InitializeWithContainerizedRedis

class RedisInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    // This approach will only scale up to 16 test application contexts because Redis only supports 16 databases.
    // If more are needed alternative approaches would be:
    //  - Switch to clearing the whole database before or after each test (class) and rotate indices after 15 back to 0
    //  - Switch to clearing the whole database before or after each test (class) and don't rotate at all
    //  - Switch to initializing a Redis container for each context - like in older implementations of this class
    //    you would likely need to kill those containers using an application listener to prevent too many Redis
    //    containers running at the same time and wasting resources.

    companion object {
        private val nextDatabaseIndex = AtomicInteger(0)
        private val container: Container<*> by lazy {
            GenericContainer("redis:7.0")
                .apply { addExposedPort(6379); start() }
        }
    }

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val databaseIndex = getNextDatabaseIndex()

        val hostProperty = "spring.data.redis.host=${container.host}"
        val portProperty = "spring.data.redis.port=${container.firstMappedPort}"
        val databaseProperty = "spring.data.redis.database=$databaseIndex"

        addInlinedPropertiesToEnvironment(applicationContext, hostProperty, portProperty, databaseProperty)
    }

    private fun getNextDatabaseIndex(): Int {
        val index = nextDatabaseIndex.getAndIncrement()
        check(index in 0..15) { "Invalid Redis database index: $index | too many tests?" }
        return index
    }

}
