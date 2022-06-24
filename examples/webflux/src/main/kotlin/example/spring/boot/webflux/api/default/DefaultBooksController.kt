package example.spring.boot.webflux.api.default

import example.spring.boot.webflux.api.CreateBookRequest
import example.spring.boot.webflux.business.Book
import example.spring.boot.webflux.business.BookCollection
import example.spring.boot.webflux.business.BookRecord
import example.spring.boot.webflux.business.BookRecordNotFoundException
import example.spring.boot.webflux.business.Isbn
import example.spring.boot.webflux.business.Title
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID
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
