package example.spring.boot.data.mongodb.utils

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.TestPropertySourceUtils.addInlinedPropertiesToEnvironment
import org.testcontainers.containers.MongoDBContainer
import java.util.UUID
import kotlin.annotation.AnnotationTarget.CLASS

@Retention
@Target(CLASS)
@ContextConfiguration(initializers = [MongoDBInitializer::class])
annotation class InitializeWithContainerizedMongoDB

class MongoDBInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    companion object {
        private val container: MongoDBContainer by lazy {
            MongoDBContainer("mongo:6.0.6")
                .apply { start() }
        }
    }

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val database = randomDatabaseName()

        val hostProperty = "spring.data.mongodb.host=${container.host}"
        val portProperty = "spring.data.mongodb.port=${container.firstMappedPort}"
        val databaseProperty = "spring.data.mongodb.database=$database"

        addInlinedPropertiesToEnvironment(applicationContext, hostProperty, portProperty, databaseProperty)
    }

    private fun randomDatabaseName() = "test_${UUID.randomUUID()}".replace("-", "")

}
