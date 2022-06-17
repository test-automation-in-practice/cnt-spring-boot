package example.spring.boot.advanced.e2e.api

import example.spring.boot.advanced.e2e.domain.BookCollection
import example.spring.boot.advanced.e2e.domain.BookDataNotFoundException
import example.spring.boot.advanced.e2e.domain.BookRecord
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder
import java.util.UUID

@RestController
@RequestMapping("/api/books")
class BookCollectionController(
    private val collection: BookCollection
) {

    private val log = getLogger(javaClass)

    @PostMapping("/{isbn}")
    fun addByIsbn(@PathVariable isbn: String, builder: UriComponentsBuilder): ResponseEntity<BookRecordRepresentation> {
        val body = collection.addBookByIsbn(isbn).getOrThrow().toRepresentation()
        val location = builder.path("/api/books/${body.id}").build().toUri()
        return ResponseEntity.created(location).body(body)
    }

    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    fun handle(e: BookDataNotFoundException) {
        log.debug(e.message, e)
    }

    data class BookRecordRepresentation(
        val id: UUID,
        val isbn: String,
        val title: String
    )

    private fun BookRecord.toRepresentation() =
        BookRecordRepresentation(
            id = id,
            isbn = book.isbn,
            title = book.title
        )

}
