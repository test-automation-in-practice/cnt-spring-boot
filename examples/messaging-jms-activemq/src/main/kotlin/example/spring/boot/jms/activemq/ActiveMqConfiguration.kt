package example.spring.boot.jms.activemq

import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.activemq.jms.pool.PooledConnectionFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.jms.ConnectionFactory

@Configuration
@EnableConfigurationProperties(ActiveMqProperties::class)
class ActiveMqConfiguration {

    @Bean
    fun connectionFactory(properties: ActiveMqProperties): ConnectionFactory {
        val connectionFactory = createConnectionFactory(properties.broker)
            .apply { setRedeliveryPolicy(properties.redelivery) }
        return createPooledConnectionFactory(connectionFactory)
    }

    private fun createConnectionFactory(properties: ActiveMqProperties.Broker) =
        ActiveMQConnectionFactory(
            /* userName = */ properties.username,
            /* password = */ properties.password,
            /* brokerURL = */ properties.url
        )

    private fun ActiveMQConnectionFactory.setRedeliveryPolicy(properties: ActiveMqProperties.Redelivery) {
        redeliveryPolicy.maximumRedeliveries = properties.maximumRedeliveries
    }

    private fun createPooledConnectionFactory(delegate: ConnectionFactory) =
        PooledConnectionFactory()
            .apply { connectionFactory = delegate }

}
