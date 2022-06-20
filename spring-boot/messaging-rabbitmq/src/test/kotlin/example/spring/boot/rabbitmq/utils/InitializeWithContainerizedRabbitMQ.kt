package example.spring.boot.rabbitmq.utils

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ApplicationListener
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.event.AfterTestClassEvent
import org.springframework.test.context.support.TestPropertySourceUtils.addInlinedPropertiesToEnvironment
import org.testcontainers.containers.RabbitMQContainer
import kotlin.annotation.AnnotationTarget.CLASS

@Retention
@Target(CLASS)
@DirtiesContext(classMode = AFTER_CLASS)
@ContextConfiguration(initializers = [ContainerizedRabbitMQInitializer::class])
annotation class InitializeWithContainerizedRabbitMQ

private class ContainerizedRabbitMQInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val container = Container().apply {
            start()
        }

        val listener = StopContainerListener(container)
        applicationContext.addApplicationListener(listener)

        val portProperty = "spring.rabbitmq.port=${container.amqpPort}"
        addInlinedPropertiesToEnvironment(applicationContext, portProperty)
    }

    class Container : RabbitMQContainer("rabbitmq:3.8")

    class StopContainerListener(private val container: Container) : ApplicationListener<AfterTestClassEvent> {
        override fun onApplicationEvent(event: AfterTestClassEvent) = container.stop()
    }

}
