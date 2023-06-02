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
@ContextConfiguration(initializers = [RabbitMQInitializer::class])
annotation class InitializeWithContainerizedRabbitMQ

class RabbitMQInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    // Unlike other initializers of this kind (e.g. our PostgreSQL and MongoDB examples) RabbitMQ does not have
    // anything like separated databases, namespaces or other easy to access / configure mechanisms for isolating
    // test (classes) from each other.

    // That is why we are using a new container for each test application context.

    // To safe on resources the test application contexts should be stopped after each test class in order for the
    // running container to be stopped as soon as possible. (see @DirtiesContext for how to do that)

    // Alternatives to this approach might be:
    //  - Use a single container like in the other examples and make sure that each test uses new random topics and
    //    queues to manually isolate the test from each other.
    //  - Find some way to drop all queues and topics of the broker programmatically after each test (class).

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
