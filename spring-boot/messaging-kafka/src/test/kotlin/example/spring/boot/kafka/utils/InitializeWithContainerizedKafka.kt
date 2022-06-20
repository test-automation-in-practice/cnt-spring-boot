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
import org.testcontainers.utility.DockerImageName
import kotlin.annotation.AnnotationTarget.CLASS

@Retention
@Target(CLASS)
@DirtiesContext(classMode = AFTER_CLASS)
@ContextConfiguration(initializers = [ContainerizedKafkaInitializer::class])
annotation class InitializeWithContainerizedKafka

private class ContainerizedKafkaInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val container = Container().apply {
            start()
        }

        val listener = StopContainerListener(container)
        applicationContext.addApplicationListener(listener)

        val serversProperty = "spring.kafka.bootstrap-servers=${container.bootstrapServers}"
        addInlinedPropertiesToEnvironment(applicationContext, serversProperty)
    }

    class Container : KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"))

    class StopContainerListener(private val container: Container) : ApplicationListener<AfterTestClassEvent> {
        override fun onApplicationEvent(event: AfterTestClassEvent) = container.stop()
    }

}
