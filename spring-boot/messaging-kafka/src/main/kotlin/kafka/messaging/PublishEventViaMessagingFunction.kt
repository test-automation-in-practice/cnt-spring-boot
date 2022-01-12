package kafka.messaging

import kafka.books.BookEvent
import kafka.events.PublishEventFunction
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