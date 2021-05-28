package provider.books

import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID


@RestController
@RequestMapping("/books")
class BooksController(
    private val library: Library
) {

    @GetMapping("/{id}")
    operator fun get(@PathVariable id: UUID): EntityModel<Book> {
        val record = library.findById(id)
        return toBookResource(record)
    }

    private fun toBookResource(record: BookRecord): EntityModel<Book> {
        val selfLink = linkTo(methodOn(BooksController::class.java).get(record.id)).withSelfRel()
        return EntityModel.of(record.book, selfLink)
    }

}
