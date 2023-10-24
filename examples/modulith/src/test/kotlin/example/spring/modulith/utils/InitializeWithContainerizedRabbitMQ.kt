package example.spring.modulith.utils

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
@ContextConfiguration(initializers = [RabbitMQInitializer::class])
annotation class InitializeWithContainerizedRabbitMQ

class RabbitMQInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val container: RabbitMQContainer = RabbitMQContainer("rabbitmq:3.11")
            .apply { start() }

        val listener = StopContainerListener(container)
        applicationContext.addApplicationListener(listener)

        val hostProperty = "spring.rabbitmq.host=${container.host}"
        val portProperty = "spring.rabbitmq.port=${container.amqpPort}"

        addInlinedPropertiesToEnvironment(applicationContext, hostProperty, portProperty)
    }

    class StopContainerListener(private val container: RabbitMQContainer) : ApplicationListener<AfterTestClassEvent> {
        override fun onApplicationEvent(event: AfterTestClassEvent) = container.stop()
    }

}
