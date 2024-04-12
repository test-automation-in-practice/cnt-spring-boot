package example.spring.boot.rabbitmq.utils

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.TestPropertySourceUtils.addInlinedPropertiesToEnvironment
import org.testcontainers.containers.RabbitMQContainer
import java.util.UUID.randomUUID
import kotlin.annotation.AnnotationTarget.CLASS

@Retention
@Target(CLASS)
@ContextConfiguration(initializers = [RabbitMQInitializer::class])
annotation class InitializeWithContainerizedRabbitMQ

class RabbitMQInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    companion object {
        private val container: RabbitMQContainer by lazy {
            RabbitMQContainer("rabbitmq:3.13")
                .apply { start() }
        }
    }

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val virtualHost = createVirtualHost()

        addInlinedPropertiesToEnvironment(
            applicationContext,
            "spring.rabbitmq.host=${container.host}",
            "spring.rabbitmq.port=${container.amqpPort}",
            "spring.rabbitmq.username=${container.adminUsername}",
            "spring.rabbitmq.password=${container.adminPassword}",
            "spring.rabbitmq.virtual-host=$virtualHost",
        )
    }

    private fun createVirtualHost(): String {
        val virtualHost = "${randomUUID()}"
        with(container) {
            execInContainer("rabbitmqctl", "add_vhost", virtualHost)
            execInContainer("rabbitmqctl", "set_permissions", "--vhost", virtualHost, adminUsername, ".*", ".*", ".*")
        }
        return virtualHost
    }
}
