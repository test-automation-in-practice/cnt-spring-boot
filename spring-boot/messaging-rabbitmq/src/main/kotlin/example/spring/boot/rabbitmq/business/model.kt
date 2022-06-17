package example.spring.boot.rabbitmq.business

import java.util.UUID

data class Book(
    val isbn: String,
    val title: String
)

data class BookRecord(
    val id: UUID,
    val book: Book
)
