package web.api.default

import web.business.BookRecord
import java.util.*

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
