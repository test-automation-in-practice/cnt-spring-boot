package example.spring.boot.basics.books.core

import java.time.Instant
import java.util.UUID

// The data of a book.

data class Book(
    val isbn: String,
    val title: String,
    val description: String? = null,
    val authors: List<String> = emptyList(),
    val numberOfPages: Int? = null
)

// An entity (has identity) representing an instance of a book in the library.
// The same logical book (same data) could have multiple instances ("copies" within the library)

data class BookRecord(
    val id: UUID,
    val book: Book,
    val timestamp: Instant? = null
)

interface BookEvent
data class BookRecordCreatedEvent(val bookRecord: BookRecord) : BookEvent
data class BookRecordDeletedEvent(val bookRecordId: UUID) : BookEvent
