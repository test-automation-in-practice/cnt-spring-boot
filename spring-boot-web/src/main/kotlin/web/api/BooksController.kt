package web.api

import org.slf4j.LoggerFactory
import org.springframework.hateoas.Resources
import org.springframework.hateoas.mvc.ControllerLinkBuilder.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import web.business.*
import java.util.*
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
    fun get(): Resources<BookResource> {
        val resources = resourceAssembler.toResources(library.getAll())
        val links = listOf(linkTo(methodOn(javaClass).get()).withSelfRel())
        return Resources(resources, links)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun post(@Valid @RequestBody request: CreateBookRequest): BookResource {
        val book = Book(
                title = Title(request.title),
                isbn = Isbn(request.isbn)
        )
        val bookRecord = library.add(book)
        return resourceAssembler.toResource(bookRecord)
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getById(@PathVariable id: UUID): BookResource {
        val bookRecord = library.get(id)
        return resourceAssembler.toResource(bookRecord)
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