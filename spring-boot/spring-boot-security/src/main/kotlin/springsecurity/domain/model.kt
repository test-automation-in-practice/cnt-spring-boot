package springsecurity.domain

import java.util.*

data class Book(
    val isbn: String,
    val title: String
)

data class BookRecord(
    val id: UUID,
    val book: Book
)
