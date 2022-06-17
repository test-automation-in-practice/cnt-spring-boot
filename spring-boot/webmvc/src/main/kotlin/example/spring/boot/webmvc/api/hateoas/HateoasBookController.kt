package example.spring.boot.webmvc.api.hateoas

import example.spring.boot.webmvc.api.CreateBookRequest
import example.spring.boot.webmvc.business.Book
import example.spring.boot.webmvc.business.BookCollection
import example.spring.boot.webmvc.business.BookRecordNotFoundException
import example.spring.boot.webmvc.business.Isbn
import example.spring.boot.webmvc.business.Title
import org.slf4j.LoggerFactory
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
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
import java.util.UUID
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
