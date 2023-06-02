package example.spring.boot.kafka.utils

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ApplicationListener
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.event.AfterTestClassEvent
import org.springframework.test.context.support.TestPropertySourceUtils.addInlinedPropertiesToEnvironment
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName.parse
import kotlin.annotation.AnnotationTarget.CLASS

@Retention
@Target(CLASS)
@DirtiesContext(classMode = AFTER_CLASS)
@ContextConfiguration(initializers = [KafkaInitializer::class])
annotation class InitializeWithContainerizedKafka

class KafkaInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    // Unlike other initializers of this kind (e.g. our PostgreSQL and MongoDB examples) Kafka does not have
    // anything like separated databases, namespaces or other easy to access / configure mechanisms for isolating
    // test (classes) from each other.

    // That is why we are using a new container for each test application context.

    // To safe on resources the test application contexts should be stopped after each test class in order for the
    // running container to be stopped as soon as possible. (see @DirtiesContext for how to do that)

    // Alternatives to this approach might be:
    //  - Use a single container like in the other examples and make sure that each test uses new random topics and
    //    consumers to manually isolate the test from each other.
    //  - Find some way to drop all consumers, topics etc. of the broker programmatically after each test (class).

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val container: KafkaContainer = KafkaContainer(parse("confluentinc/cp-kafka:7.4.0"))
            .apply { start() }

        val listener = StopContainerListener(container)
        applicationContext.addApplicationListener(listener)

        val serversProperty = "spring.kafka.bootstrap-servers=${container.bootstrapServers}"
        addInlinedPropertiesToEnvironment(applicationContext, serversProperty)
    }

    class StopContainerListener(private val container: KafkaContainer) : ApplicationListener<AfterTestClassEvent> {
        override fun onApplicationEvent(event: AfterTestClassEvent) = container.stop()
    }

}
