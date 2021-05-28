package web.api

import org.slf4j.LoggerFactory
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import web.business.Book
import web.business.BookRecordNotFoundException
import web.business.Isbn
import web.business.Library
import web.business.Title
import java.util.UUID
import javax.validation.Valid

@RestController
@RequestMapping("/api/books")
class BooksController(
    private val library: Library,
    private val resourceAssembler: BookResourceAssembler
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun get(): CollectionModel<BookResource> {
        val resources = resourceAssembler.toCollectionModel(library.getAll())
        val links = listOf(linkTo(methodOn(javaClass).get()).withSelfRel())
        return CollectionModel.of(resources, links)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun post(@Valid @RequestBody request: CreateBookRequest): BookResource {
        val book = Book(
            title = Title(request.title),
            isbn = Isbn(request.isbn)
        )
        val bookRecord = library.add(book)
        return resourceAssembler.toModel(bookRecord)
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getById(@PathVariable id: UUID): BookResource {
        val bookRecord = library.get(id)
        return resourceAssembler.toModel(bookRecord)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteById(@PathVariable id: UUID) {
        library.delete(id)
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(BookRecordNotFoundException::class)
    fun handleNotFoundException(e: BookRecordNotFoundException) {
        log.debug("Could not find Book [${e.id}], responding with '404 Not Found'.")
    }

}
