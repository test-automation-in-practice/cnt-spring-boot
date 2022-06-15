package example.graphql.api

import example.graphql.business.Book
import example.graphql.business.BookRecord
import example.graphql.business.Isbn
import example.graphql.business.Title
import java.util.UUID

data class BookInput(
    val title: String,
    val isbn: String
)

data class BookRepresentation(
    val id: UUID,
    val title: String,
    val isbn: String
)

fun BookInput.toBook(): Book =
    Book(Title(title), Isbn(isbn))

fun BookRecord.toRepresentation() =
    BookRepresentation(
        id = id,
        title = book.title.toString(),
        isbn = book.isbn.toString()
    )
