package example.spring.boot.webmvc.api.hateoas

import example.spring.boot.webmvc.api.BookNotFoundProblem
import example.spring.boot.webmvc.api.CreateBookRequest
import example.spring.boot.webmvc.business.Book
import example.spring.boot.webmvc.business.BookCollection
import example.spring.boot.webmvc.business.Isbn
import example.spring.boot.webmvc.business.Title
import jakarta.validation.Valid
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/hateoas-api/books")
class HateoasBookController(
    private val bookCollection: BookCollection,
    private val assembler: BookRepresentationAssembler
) {

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
    fun getById(@PathVariable id: UUID): BookRepresentation =
        bookCollection.get(id)?.let(assembler::toModel)
            ?: throw BookNotFoundProblem(id) // throw to trigger correct extended error-handling+

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    fun deleteById(@PathVariable id: UUID) {
        bookCollection.delete(id)
    }

}
