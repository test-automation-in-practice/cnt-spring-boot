package example.spring.boot.rabbitmq.utils

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.TestPropertySourceUtils.addInlinedPropertiesToEnvironment
import org.testcontainers.containers.RabbitMQContainer
import java.net.Authenticator
import java.net.PasswordAuthentication
import java.net.URI.create
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers.noBody
import java.net.http.HttpResponse.BodyHandlers.discarding
import java.util.UUID.randomUUID
import kotlin.annotation.AnnotationTarget.CLASS

@Retention
@Target(CLASS)
@ContextConfiguration(initializers = [RabbitMQInitializer::class])
annotation class InitializeWithContainerizedRabbitMQ

class RabbitMQInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    companion object {
        private val container: RabbitMQContainer by lazy {
            RabbitMQContainer("rabbitmq:3.13-management")
                .apply { start() }
        }
        private val httpClient: HttpClient by lazy {
            HttpClient.newBuilder()
                .authenticator(BasicAuthenticator(container.adminUsername, container.adminPassword))
                .build()
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
        val request = HttpRequest.newBuilder()
            .PUT(noBody())
            .uri(create("http://${container.host}:${container.httpPort}/api/vhosts/$virtualHost"))
            .build()
        httpClient.send(request, discarding())
        return virtualHost
    }

    private class BasicAuthenticator(val username: String, val password: String) : Authenticator() {
        override fun getPasswordAuthentication() = PasswordAuthentication(username, password.toCharArray())
    }
}
