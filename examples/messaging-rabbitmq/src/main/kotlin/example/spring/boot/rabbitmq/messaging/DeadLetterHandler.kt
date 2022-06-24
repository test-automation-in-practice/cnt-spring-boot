package example.spring.boot.rabbitmq.messaging

import org.slf4j.LoggerFactory.getLogger
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.messaging.Message
import org.springframework.stereotype.Component

@Component
class DeadLetterHandler {

    private val log = getLogger(javaClass)

    @RabbitListener(queues = ["queues.dead-letters"])
    fun handleDeadLetter(message: Message<*>) {
        log.error("Dead letter message arrived: $message")
    }

}
