package provider.books

import java.util.UUID

// The data of a book, includes properties which are not necessarily relevant to any consumers.

data class Book(
    val isbn: String,
    val title: String,
    val description: String? = null,
    val authors: List<String> = emptyList(),
    val numberOfPages: Int? = null
)

// An entity (has identity) representing an instance of a book in the library.
// The same logical book (same data) could have multiple instances ("copies" within the library)

data class BookRecord(val id: UUID, val book: Book)
