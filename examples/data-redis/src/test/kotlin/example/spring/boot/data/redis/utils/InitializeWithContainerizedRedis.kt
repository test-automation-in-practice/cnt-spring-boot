package example.spring.boot.data.redis.utils

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ApplicationListener
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.event.AfterTestClassEvent
import org.springframework.test.context.support.TestPropertySourceUtils.addInlinedPropertiesToEnvironment
import org.testcontainers.containers.GenericContainer
import kotlin.annotation.AnnotationTarget.CLASS

@Retention
@Target(CLASS)
@DirtiesContext(classMode = AFTER_CLASS)
@ContextConfiguration(initializers = [ContainerizedRedisInitializer::class])
annotation class InitializeWithContainerizedRedis

private class ContainerizedRedisInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    private val mappedPort = 6379

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val container = Container().apply {
            addExposedPort(mappedPort)
            start()
        }

        val listener = StopContainerListener(container)
        applicationContext.addApplicationListener(listener)

        val portProperty = "spring.redis.port=${container.getMappedPort(mappedPort)}"
        addInlinedPropertiesToEnvironment(applicationContext, portProperty)
    }

    class Container : GenericContainer<Container>("redis:7.0")

    class StopContainerListener(private val container: Container) : ApplicationListener<AfterTestClassEvent> {
        override fun onApplicationEvent(event: AfterTestClassEvent) = container.stop()
    }

}
