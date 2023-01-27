package example.spring.boot.jms.messaging

import jakarta.jms.Message
import org.slf4j.LoggerFactory.getLogger
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

@Component
class DeadLetterHandler {

    private val log = getLogger(javaClass)

    @JmsListener(destination = "DLQ") // can only be configured on the broker - using default
    fun handleDeadLetter(message: Message) {
        log.error("Dead letter message arrived: $message")
    }

}
