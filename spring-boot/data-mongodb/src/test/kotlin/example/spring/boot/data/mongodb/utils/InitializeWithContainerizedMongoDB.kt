package example.spring.boot.data.mongodb.utils

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ApplicationListener
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.event.AfterTestClassEvent
import org.springframework.test.context.support.TestPropertySourceUtils.addInlinedPropertiesToEnvironment
import org.testcontainers.containers.MongoDBContainer
import kotlin.annotation.AnnotationTarget.CLASS

@Retention
@Target(CLASS)
@DirtiesContext(classMode = AFTER_CLASS)
@ContextConfiguration(initializers = [ContainerizedMongoDBInitializer::class])
annotation class InitializeWithContainerizedMongoDB

private class ContainerizedMongoDBInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    private val mappedPort = 27017

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val container = Container().apply {
            addExposedPort(mappedPort)
            start()
        }

        val listener = StopContainerListener(container)
        applicationContext.addApplicationListener(listener)

        val hostProperty = "spring.data.mongodb.host=localhost"
        val portProperty = "spring.data.mongodb.port=${container.getMappedPort(mappedPort)}"
        addInlinedPropertiesToEnvironment(applicationContext, hostProperty, portProperty)
    }

    class Container : MongoDBContainer("mongo:4.0.10")

    class StopContainerListener(private val container: Container) : ApplicationListener<AfterTestClassEvent> {
        override fun onApplicationEvent(event: AfterTestClassEvent) = container.stop()
    }

}
