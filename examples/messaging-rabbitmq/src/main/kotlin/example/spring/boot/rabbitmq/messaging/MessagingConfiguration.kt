package example.spring.boot.rabbitmq.messaging

import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.module.kotlin.jacksonMapperBuilder

@Configuration
class MessagingConfiguration {

    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate =
        RabbitTemplate(connectionFactory).apply { messageConverter = messageConverter() } // for sending

    @Bean
    fun jacksonJsonMessageConverter(): MessageConverter = messageConverter() // for receiving

    private fun messageConverter() = JacksonJsonMessageConverter(jacksonMapperBuilder().build())
}
