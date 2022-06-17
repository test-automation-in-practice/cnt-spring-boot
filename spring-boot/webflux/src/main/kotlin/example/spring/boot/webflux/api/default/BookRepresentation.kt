package example.spring.boot.webflux.api.default

import example.spring.boot.webflux.business.BookRecord
import java.util.UUID

data class BookRepresentation(
    val id: UUID,
    val title: String,
    val isbn: String
)

fun BookRecord.toRepresentation() =
    BookRepresentation(
        id = id,
        title = book.title.toString(),
        isbn = book.isbn.toString()
    )
