package provider.books

import java.util.*

data class Book(
    val isbn: String,
    val title: String
)

data class BookCreatedEvent(
    val type: String = "book-created",
    val eventId: UUID,
    val bookId: UUID,
    val book: Book
)
