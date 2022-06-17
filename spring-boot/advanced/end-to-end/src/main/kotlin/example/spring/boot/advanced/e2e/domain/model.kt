package example.spring.boot.advanced.e2e.domain

import java.util.UUID

data class Book(
    val isbn: String,
    val title: String
)

data class BookRecord(
    val id: UUID,
    val book: Book
)

class BookDataNotFoundException(isbn: String) : RuntimeException("Could not find data for book with ISBN $isbn")
