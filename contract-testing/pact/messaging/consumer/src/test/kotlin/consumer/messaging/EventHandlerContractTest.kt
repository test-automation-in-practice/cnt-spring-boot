package consumer.messaging

import au.com.dius.pact.consumer.MessagePactBuilder
import au.com.dius.pact.consumer.dsl.newJsonObject
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.consumer.junit5.ProviderType
import au.com.dius.pact.core.model.PactSpecVersion.V3
import au.com.dius.pact.core.model.annotations.Pact
import au.com.dius.pact.core.model.messaging.Message
import au.com.dius.pact.core.model.messaging.MessagePact
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import consumer.books.Book
import consumer.books.BookCreatedEvent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID.fromString

@ExtendWith(PactConsumerTestExt::class)
@PactTestFor(providerName = "provider", providerType = ProviderType.ASYNCH)
class EventHandlerContractTest {

    private val objectMapper = jacksonObjectMapper()
    private val event = BookCreatedEvent(
        eventId = fromString("28691b90-17b4-4a10-9321-72f2c0f11242"),
        bookId = fromString("28691b90-17b4-4a10-9321-72f2c0f11242"),
        book = Book(isbn = "9780132350884", title = "Clean Code")
    )

    @ExperimentalUnsignedTypes
    @Pact(consumer = "consumer", provider = "provider")
    fun receiveBookCreatedEventPact(builder: MessagePactBuilder): MessagePact =
        builder
            .hasPactWith("provider")
            .expectsToReceive("a book-created event")
            .withContent(
                newJsonObject {
                    uuid("eventId", event.eventId)
                    uuid("bookId", event.bookId)
                    `object`("book") { book ->
                        book.stringType("isbn", event.book.isbn)
                        book.stringType("title", event.book.title)
                    }
                }
            ).toPact()

    @Test
    @PactTestFor(pactMethod = "receiveBookCreatedEventPact", pactVersion = V3)
    fun `get single existing book interaction`(messages: List<Message>) {
        assertThat(messages).hasSize(1)

        val receivedEvent = objectMapper.readValue<BookCreatedEvent>(messages.first().contentsAsBytes())

        assertThat(receivedEvent).isEqualTo(event)
    }
}
