package kafka.events

import kafka.books.BookCreatedEvent
import kafka.books.BookDeletedEvent
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.DltHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.stereotype.Component

@Component
class EventHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    @RetryableTopic(attempts = "1")
    @KafkaListener(id = "book-created-listener", topics = ["book-created"])
    fun handleCreatedEvent(event: BookCreatedEvent) {
        log.info("”Book was created: $event")
    }

    @RetryableTopic(attempts = "1")
    @KafkaListener(id = "book-deleted-listener", topics = ["book-deleted"])
    fun handleDeletedEvent(event: BookDeletedEvent) {
        log.info("Book was deleted: $event")
    }

    @DltHandler
    fun handleDeadLetterEvent(event: ConsumerRecord<*, *>) {
        log.error("Dead letter message arrived: $event")
    }

}