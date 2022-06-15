package example.graphql.api

import example.graphql.business.Book
import example.graphql.business.BookCollection
import example.graphql.business.BookRecord
import example.graphql.business.Isbn
import example.graphql.business.Title
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import java.util.UUID

@Controller
class BookController(
    private val collection: BookCollection
) {

    @MutationMapping
    fun addBook(@Argument title: Title, @Argument isbn: Isbn): BookRepresentation =
        collection.add(Book(title, isbn)).toRepresentation()

    @QueryMapping
    fun getAllBooks(): List<BookRepresentation> =
        collection.getAll().map(BookRecord::toRepresentation)

    @QueryMapping
    fun getBookById(@Argument id: UUID): BookRepresentation? =
        collection.get(id)?.toRepresentation()

    @MutationMapping
    fun deleteBookById(@Argument id: UUID): Boolean =
        collection.delete(id)
}
