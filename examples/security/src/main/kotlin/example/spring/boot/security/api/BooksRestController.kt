package example.spring.boot.security.api

import example.spring.boot.security.business.Book
import example.spring.boot.security.business.BookCollection
import example.spring.boot.security.business.BookRecord
import example.spring.boot.security.business.Isbn
import example.spring.boot.security.business.Title
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.noContent
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

// Simple RestController used to map HTTP-based request / responses to business function invocations.

@RestController
@RequestMapping("/api/books")
class BooksRestController(
    private val bookCollection: BookCollection
) {

    @PostMapping
    @ResponseStatus(CREATED)
    fun post(@RequestBody book: Book): BookRepresentation {
        return bookCollection.addBook(book).toRepresentation()
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<BookRepresentation> =
        bookCollection.getBookById(id)
            ?.let { ok().body(it.toRepresentation()) }
            ?: noContent().build()

    @GetMapping("/_search")
    fun getByIsbn(@RequestParam isbn: Isbn): List<BookRepresentation> =
        bookCollection.getBooksByIsbn(isbn)
            .map { it.toRepresentation() }

    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/{id}")
    fun deleteById(@PathVariable id: UUID) {
        bookCollection.deleteBookById(id)
    }

    data class BookRepresentation(
        val id: UUID,
        val isbn: Isbn,
        val title: Title
    )

    private fun BookRecord.toRepresentation() =
        BookRepresentation(
            id = id,
            isbn = book.isbn,
            title = book.title
        )

}
