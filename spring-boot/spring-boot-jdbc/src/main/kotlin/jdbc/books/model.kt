package jdbc.books

import java.util.UUID

data class Book(val title: String, val isbn: String)
data class BookRecord(val id: UUID, val book: Book)
