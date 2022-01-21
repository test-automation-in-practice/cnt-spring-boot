package consumer.books

import java.util.*

data class Book(
    val isbn: String,
    val title: String
)

data class BookCreatedEvent(
    val eventId: UUID,
    val bookId: UUID,
    val book: Book
)
