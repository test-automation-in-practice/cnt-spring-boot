package webmvc.api.hateoas

import org.slf4j.LoggerFactory
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.HttpStatus.*
import org.springframework.web.bind.annotation.*
import webmvc.api.CreateBookRequest
import webmvc.business.*
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/hateoas-api/books")
class HateoasBookController(
    private val bookCollection: BookCollection,
    private val assembler: BookRepresentationAssembler
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping
    @ResponseStatus(CREATED)
    fun post(@Valid @RequestBody request: CreateBookRequest): BookRepresentation {
        val book = Book(
            title = Title(request.title),
            isbn = Isbn(request.isbn)
        )
        val bookRecord = bookCollection.add(book)
        return assembler.toModel(bookRecord)
    }

    @GetMapping
    fun get(): CollectionModel<BookRepresentation> {
        val books = bookCollection.getAll()
        val representations = assembler.toCollectionModel(books)
        val links = listOf(linkTo(methodOn(javaClass).get()).withSelfRel())
        return CollectionModel.of(representations, links)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): BookRepresentation {
        val bookRecord = bookCollection.get(id)
        return assembler.toModel(bookRecord)
    }

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
