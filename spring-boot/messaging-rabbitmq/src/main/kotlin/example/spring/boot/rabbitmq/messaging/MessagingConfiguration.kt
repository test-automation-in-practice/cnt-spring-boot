package example.spring.boot.rabbitmq.messaging

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MessagingConfiguration {

    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate =
        RabbitTemplate(connectionFactory).apply { messageConverter = messageConverter() } // for sending

    @Bean
    fun jackson2JsonMessageConverter(): MessageConverter = messageConverter() // for receiving

    private fun messageConverter() = Jackson2JsonMessageConverter(jacksonObjectMapper())
}
