package webmvc.api.default

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.*
import org.springframework.web.bind.annotation.*
import webmvc.api.CreateBookRequest
import webmvc.business.*
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/default-api/books")
class DefaultBooksController(
    private val bookCollection: BookCollection
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping
    @ResponseStatus(CREATED)
    fun post(@Valid @RequestBody request: CreateBookRequest): BookRepresentation {
        val book = Book(
            title = Title(request.title),
            isbn = Isbn(request.isbn)
        )
        val record = bookCollection.add(book)
        return record.toRepresentation()
    }

    @GetMapping
    fun get(): List<BookRepresentation> =
        bookCollection.getAll().map(BookRecord::toRepresentation)

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): BookRepresentation =
        bookCollection.get(id).toRepresentation()

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    fun deleteById(@PathVariable id: UUID) {
        bookCollection.delete(id)
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(BookRecordNotFoundException::class)
    fun handleNotFoundException(e: BookRecordNotFoundException) {
        log.debug("Could not find Book [${e.id}], responding with '404 Not Found'.")
    }

}
