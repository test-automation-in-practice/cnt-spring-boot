package example.spring.boot.jms.messaging

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.jms.ConnectionFactory
import jakarta.jms.Session.CLIENT_ACKNOWLEDGE
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jms.annotation.EnableJms
import org.springframework.jms.config.DefaultJmsListenerContainerFactory
import org.springframework.jms.core.JmsTemplate
import org.springframework.jms.support.converter.MappingJackson2MessageConverter
import org.springframework.jms.support.converter.MessageConverter

@EnableJms
@Configuration
class MessagingConfiguration {

    @Bean
    fun jmsTemplate(
        connectionFactory: ConnectionFactory,
        messageConverter: MessageConverter
    ) = JmsTemplate()
        .apply {
            setMessageConverter(messageConverter)
            setConnectionFactory(connectionFactory)
        }

    @Bean
    fun jmsListenerContainerFactory(
        connectionFactory: ConnectionFactory,
        messageConverter: MessageConverter
    ) = DefaultJmsListenerContainerFactory()
        .apply {
            setMessageConverter(messageConverter)
            setConnectionFactory(connectionFactory)
            setSessionAcknowledgeMode(CLIENT_ACKNOWLEDGE)
        }

    @Bean
    fun messageConverter(): MessageConverter =
        MappingJackson2MessageConverter().apply {
            setTypeIdPropertyName("_type")
            setObjectMapper(jacksonObjectMapper())
        }

}
