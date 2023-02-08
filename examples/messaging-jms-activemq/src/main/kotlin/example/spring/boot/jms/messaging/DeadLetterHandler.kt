package example.spring.boot.jms.messaging

import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component
import javax.jms.Message
import javax.jms.MessageListener

@Component
class DeadLetterHandler : MessageListener {

    private val log = getLogger(javaClass)

    override fun onMessage(message: Message) {
        log.error("Dead letter message arrived: $message")
    }

}
