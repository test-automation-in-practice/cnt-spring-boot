package example.spring.boot.jms.activemq

import org.apache.activemq.broker.BrokerFactory
import org.apache.activemq.broker.BrokerService
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ApplicationListener
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.event.AfterTestClassEvent
import org.springframework.test.context.support.TestPropertySourceUtils.addInlinedPropertiesToEnvironment
import java.net.ServerSocket
import kotlin.annotation.AnnotationTarget.CLASS

@Retention
@Target(CLASS)
@DirtiesContext(classMode = AFTER_CLASS)
@ContextConfiguration(initializers = [EmbeddedActiveMqInitializer::class])
annotation class InitializeWithEmbeddedActiveMq

private class EmbeddedActiveMqInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val port = randomPort()
        val url = "tcp://localhost:$port"
        val broker = BrokerFactory.createBroker("broker:($url)", true)

        val listener = StopBrokerListener(broker)
        applicationContext.addApplicationListener(listener)

        val urlProperty = "activemq.broker.url=$url"
        addInlinedPropertiesToEnvironment(applicationContext, urlProperty)

        applicationContext.beanFactory.registerSingleton("embeddedBroker", broker)
    }

    private fun randomPort(): Int =
        ServerSocket(0).use { it.localPort }

    class StopBrokerListener(private val broker: BrokerService) : ApplicationListener<AfterTestClassEvent> {
        override fun onApplicationEvent(event: AfterTestClassEvent) = broker.stop()
    }

}