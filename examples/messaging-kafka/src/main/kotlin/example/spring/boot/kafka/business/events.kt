package example.spring.boot.kafka.business

import java.util.UUID

sealed class BookEvent {
    abstract val type: String
    abstract val eventId: UUID
    abstract val bookId: UUID
}

data class BookCreatedEvent(
    override val eventId: UUID,
    override val bookId: UUID,
    val book: Book
) : BookEvent() {
    override val type: String = "book-created"
}

data class BookDeletedEvent(
    override val eventId: UUID,
    override val bookId: UUID
) : BookEvent() {
    override val type: String = "book-deleted"
}

fun BookRecord.createdEvent() =
    BookCreatedEvent(
        eventId = UUID.randomUUID(),
        bookId = this.id,
        book = this.book
    )

fun BookRecord.deletedEvent() =
    BookDeletedEvent(
        eventId = UUID.randomUUID(),
        bookId = this.id
    )
