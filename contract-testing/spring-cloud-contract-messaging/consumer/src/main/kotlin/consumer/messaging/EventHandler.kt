package consumer.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import consumer.books.Library
import consumer.books.BookCreatedEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.DltHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.messaging.Message
import org.springframework.stereotype.Component

@Component
class EventHandler(
    private val objectMapper: ObjectMapper,
    private val library: Library,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @RetryableTopic(attempts = "1")
    @KafkaListener(id = "book-created-listener", topics = ["book-created"])
    fun handleCreatedEvent(message: Message<String>) {
        val event = objectMapper.readValue<BookCreatedEvent>(message.payload)
        library.add(event.book)

        log.info("”Book was created: $event")
    }

    @DltHandler
    fun handleDeadLetterEvent(message: Message<String>) {
        log.error("Dead letter message arrived: $message")
    }
}
