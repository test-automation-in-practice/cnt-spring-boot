package example.spring.boot.webflux.api.hateoas

import example.spring.boot.webflux.api.CreateBookRequest
import example.spring.boot.webflux.business.Book
import example.spring.boot.webflux.business.BookCollection
import example.spring.boot.webflux.business.BookRecordNotFoundException
import example.spring.boot.webflux.business.Isbn
import example.spring.boot.webflux.business.Title
import jakarta.validation.Valid
import org.slf4j.LoggerFactory.getLogger
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn
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
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.UUID

@RestController
@RequestMapping("/hateoas-api/books")
class HateoasBookController(
    private val bookCollection: BookCollection,
    private val assembler: BookRepresentationAssembler
) {

    private val log = getLogger(javaClass)

    @PostMapping
    @ResponseStatus(CREATED)
    fun post(exchange: ServerWebExchange, @Valid @RequestBody request: CreateBookRequest): Mono<BookRepresentation> {
        val book = Book(
            title = Title(request.title),
            isbn = Isbn(request.isbn)
        )
        return bookCollection.add(book)
            .flatMap { record -> assembler.toModel(record, exchange) }
    }

    @GetMapping
    fun get(exchange: ServerWebExchange): Mono<CollectionModel<BookRepresentation>> =
        assembler.toCollectionModel(bookCollection.getAll(), exchange)
            .flatMap { collectionModel ->
                linkTo(methodOn(javaClass).get(exchange), exchange).withSelfRel().toMono()
                    .map { selfLink -> collectionModel.add(selfLink) }
            }

    @GetMapping("/{id}")
    fun getById(exchange: ServerWebExchange, @PathVariable id: UUID): Mono<BookRepresentation> =
        bookCollection.get(id)
            .flatMap { record -> assembler.toModel(record, exchange) }

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
