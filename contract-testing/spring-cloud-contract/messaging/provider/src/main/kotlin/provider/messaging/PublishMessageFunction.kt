package provider.messaging

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import provider.books.Book
import provider.books.BookCreatedEvent
import java.util.UUID.randomUUID

@Service
class PublishMessageFunction(private val template: KafkaTemplate<String, BookCreatedEvent>) {

    operator fun invoke(book: Book) {
        val event = BookCreatedEvent(eventId = randomUUID(), bookId = randomUUID(), book = book)
        template.send("book-created", event)
    }

}
