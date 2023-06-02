package example.spring.boot.jdbc.utils

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.TestPropertySourceUtils.addInlinedPropertiesToEnvironment
import org.testcontainers.containers.PostgreSQLContainer
import java.util.UUID.randomUUID
import kotlin.annotation.AnnotationTarget.CLASS

@Retention
@Target(CLASS)
@ContextConfiguration(initializers = [PostgreSQLInitializer::class])
annotation class InitializeWithContainerizedPostgreSQL

/**
 * Initializes a new PostgreSQL container for the first test that needs it.
 * All subsequent tests will use the same container.
 *
 * In order to prevent cross pollution between tests, a new random database is created and used for each
 * application context. So each test class should get its own database.
 */
class PostgreSQLInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    companion object {
        private val container: PostgreSQLContainer<*> by lazy {
            PostgreSQLContainer("postgres:15.3-alpine")
                .apply { start() }
        }
    }

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val database = randomDatabaseName()
        val jdbcUrl = "jdbc:postgresql://${container.host}:${container.firstMappedPort}/$database"

        initializeDatabase(database)

        val urlProperty = "spring.datasource.url=$jdbcUrl"
        val usernameProperty = "spring.datasource.username=${container.username}"
        val passwordProperty = "spring.datasource.password=${container.password}"

        addInlinedPropertiesToEnvironment(applicationContext, urlProperty, usernameProperty, passwordProperty)
    }

    private fun initializeDatabase(database: String) {
        container.createConnection("")
            .use { connection ->
                connection.createStatement()
                    .use { statement ->
                        statement.execute("CREATE DATABASE $database")
                    }
            }
    }

    private fun randomDatabaseName() = "test_${randomUUID()}".replace("-", "")

}
