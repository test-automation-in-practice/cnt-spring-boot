package provider.books

import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.noContent
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

// This controller provides an HTTP endpoint for getting book data by a book-record ID.
// It is responsible for everything that might be relevant for a contract (data + format).

@RestController
@RequestMapping("/books")
class BooksController(
    private val library: Library // provided as mock in tests
) {

    @GetMapping("/{id}")
    operator fun get(@PathVariable id: UUID): ResponseEntity<BookRepresentation> =
        when (val bookRecord = library.findById(id)) {
            null -> noContent().build()
            else -> ok(bookRecord.toRepresentation())
        }

    data class BookRepresentation(
        val id: UUID,
        val isbn: String,
        val title: String,
        val description: String?,
        val authors: List<String>,
        val numberOfPages: Int?
    )

    private fun BookRecord.toRepresentation() = BookRepresentation(
        id = id,
        isbn = book.isbn,
        title = book.title,
        description = book.description,
        authors = book.authors,
        numberOfPages = book.numberOfPages
    )

}
