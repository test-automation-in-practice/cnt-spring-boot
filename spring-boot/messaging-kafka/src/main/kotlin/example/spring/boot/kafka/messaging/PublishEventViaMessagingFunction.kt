package example.spring.boot.kafka.messaging

import example.spring.boot.kafka.business.BookEvent
import example.spring.boot.kafka.events.PublishEventFunction
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class PublishEventViaMessagingFunction(
    private val kafkaTemplate: KafkaTemplate<String, BookEvent>
) : PublishEventFunction {

    override fun invoke(event: BookEvent) {
        kafkaTemplate.send(event.type, event)
    }
}
