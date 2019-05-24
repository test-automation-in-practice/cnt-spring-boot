package provider.books

import org.springframework.hateoas.Resource
import org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo
import org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*


@RestController
@RequestMapping("/books")
class BooksController(
    private val library: Library
) {

    @GetMapping("/{id}")
    operator fun get(@PathVariable id: UUID): Resource<Book> {
        val record = library.findById(id)
        return toBookResource(record)
    }

    private fun toBookResource(record: BookRecord): Resource<Book> {
        val selfLink = linkTo(methodOn(BooksController::class.java).get(record.id)).withSelfRel()
        return Resource(record.book, selfLink)
    }

}
