package webflux.api.default

import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpStatus.*
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import webflux.api.CreateBookRequest
import webflux.business.*
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/default-api/books")
class DefaultBooksController(
    private val bookCollection: BookCollection
) {

    private val log = getLogger(javaClass)

    @PostMapping
    @ResponseStatus(CREATED)
    fun post(@Valid @RequestBody request: CreateBookRequest): Mono<BookRepresentation> {
        val book = Book(
            title = Title(request.title),
            isbn = Isbn(request.isbn)
        )
        return bookCollection.add(book)
            .map(BookRecord::toRepresentation)
    }

    @GetMapping
    fun get(): Flux<BookRepresentation> =
        bookCollection.getAll().map(BookRecord::toRepresentation)

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): Mono<BookRepresentation> =
        bookCollection.get(id).map(BookRecord::toRepresentation)

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    fun deleteById(@PathVariable id: UUID): Mono<Unit> =
        bookCollection.delete(id)

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(BookRecordNotFoundException::class)
    fun handleNotFoundException(e: BookRecordNotFoundException) {
        log.debug("Could not find Book [${e.id}], responding with '404 Not Found'.")
    }

}
