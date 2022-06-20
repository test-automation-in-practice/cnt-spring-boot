package example.spring.boot.data.jdbc.utils

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ApplicationListener
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.event.AfterTestClassEvent
import org.springframework.test.context.support.TestPropertySourceUtils.addInlinedPropertiesToEnvironment
import org.testcontainers.containers.PostgreSQLContainer
import kotlin.annotation.AnnotationTarget.CLASS

@Retention
@Target(CLASS)
@DirtiesContext(classMode = AFTER_CLASS)
@ContextConfiguration(initializers = [ContainerizedPostgreSQLInitializer::class])
annotation class InitializeWithContainerizedPostgreSQL

private class ContainerizedPostgreSQLInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val container = Container().apply {
            start()
        }

        val listener = StopContainerListener(container)
        applicationContext.addApplicationListener(listener)

        val urlProperty = "spring.datasource.url=${container.jdbcUrl}"
        val usernameProperty = "spring.datasource.username=${container.username}"
        val passwordProperty = "spring.datasource.password=${container.password}"
        addInlinedPropertiesToEnvironment(applicationContext, urlProperty, usernameProperty, passwordProperty)
    }

    class Container : PostgreSQLContainer<Container>("postgres:14.3")

    class StopContainerListener(private val container: Container) : ApplicationListener<AfterTestClassEvent> {
        override fun onApplicationEvent(event: AfterTestClassEvent) = container.stop()
    }

}
