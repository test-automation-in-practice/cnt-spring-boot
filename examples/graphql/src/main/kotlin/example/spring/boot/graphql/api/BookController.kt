package example.spring.boot.graphql.api

import example.spring.boot.graphql.business.Book
import example.spring.boot.graphql.business.BookCollection
import example.spring.boot.graphql.business.BookRecord
import example.spring.boot.graphql.business.Isbn
import example.spring.boot.graphql.business.Page
import example.spring.boot.graphql.business.Pagination
import example.spring.boot.graphql.business.Title
import jakarta.validation.Valid
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import java.util.UUID

@Controller
class BookController(
    private val collection: BookCollection
) {

    /*
     Validation of these parameters is done using value type wrappers. These classes cannot
     be initialized without the wrapped values being 'valid'.

     Invalid values will lead to a 'BindException' which needs to be handled with a custom
     error handler (see 'GraphQLConfiguration') in order to blame the caller ("validation error").

     The wrapping works for single arguments out of the box, but does not work for input composite
     types.
     */
    @MutationMapping
    fun addBook(@Argument title: Title, @Argument isbn: Isbn): BookRepresentation =
        collection.add(Book(title, isbn)).toRepresentation()

    /*
     Validation of the query parameters is done using 'jakarta.validation.constraints' annotations
     on simple Java types (e.g. String). The validation is triggered because of the '@Valid'
     annotation of the parameter.

     Invalid values will lead to a 'ConstraintViolationException' which needs to be handled with a custom
     error handler (see 'GraphQLConfiguration') in order to blame the caller ("validation error").
     */
    @QueryMapping
    fun findBooks(@Valid @Argument query: QueryInput): List<BookRepresentation> =
        collection.find(query.toInternal()).map(BookRecord::toRepresentation)

    /*
     Validation of the pagination parameters is done by the GraphQL schema using custom
     scalar types 'PageIndex' and 'PageSize'.

     The usage of custom scalar types involves custom mapping 'Coercing' classes, but the exception
     handing and blaming of the caller ("validation error") works out of the box.
     */
    @QueryMapping
    fun getAllBooks(@Argument pagination: Pagination): Page<BookRepresentation> =
        collection.getAll(pagination).map(BookRecord::toRepresentation)

    /* UUID type is supported out of the box */
    @QueryMapping
    fun getBookById(@Argument id: UUID): BookRepresentation? =
        collection.get(id)?.toRepresentation()

    /* UUID type is supported out of the box */
    @MutationMapping
    fun deleteBookById(@Argument id: UUID): Boolean =
        collection.delete(id)
}
