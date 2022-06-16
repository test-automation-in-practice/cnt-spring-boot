package example.spring.boot.graphql.api

import example.spring.boot.graphql.business.BookRecord
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
