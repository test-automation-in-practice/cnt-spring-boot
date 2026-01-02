package example.spring.boot.jms.messaging

import jakarta.jms.ConnectionFactory
import jakarta.jms.Session
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jms.annotation.EnableJms
import org.springframework.jms.config.DefaultJmsListenerContainerFactory
import org.springframework.jms.core.JmsTemplate
import org.springframework.jms.support.converter.JacksonJsonMessageConverter
import org.springframework.jms.support.converter.MessageConverter
import tools.jackson.module.kotlin.jacksonMapperBuilder

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
            setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE)
        }

    @Bean
    fun messageConverter(): MessageConverter =
        JacksonJsonMessageConverter(jacksonMapperBuilder())
            .apply { setTypeIdPropertyName("_type") }

}
