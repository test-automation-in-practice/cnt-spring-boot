package example.spring.boot.security.business

import example.spring.boot.security.persistence.BookRepository
import example.spring.boot.security.security.Authorities.ROLE_CURATOR
import example.spring.boot.security.security.Roles.CURATOR
import example.spring.boot.security.security.Roles.USER
import jakarta.annotation.security.RolesAllowed
import org.springframework.security.access.annotation.Secured
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component
import org.springframework.util.IdGenerator
import java.util.UUID

// Security access rules are defined on the main business classes. Doing so will enforce the same rules
// regardless of what kind of API is used to talk to the overall application.

@Component
class BookCollection(
    private val repository: BookRepository,
    private val idGenerator: IdGenerator
) {

    // The following functions are annotated with different kinds of annotations supported by Spring Security
    // to setup method-level security rules.

    // - - -

    // @RolesAllowed takes the name of one or more roles which are allowed to invoke the annotated method.
    // Whatever value is used will be be prefixed with ROLE_ and compared to the user's granted authorities.

    @RolesAllowed(CURATOR)
    fun addBook(book: Book): BookRecord {
        val record = BookRecord(
            id = idGenerator.generateId(),
            book = book
        )
        return repository.save(record)
    }

    // @PreAuthorize uses an 'access-control expression' which must evaluate to `true` in order to allow
    // invocation the annotated method. This simple expression uses `hasAnyRole` in order to limit the access
    // to authenticated users with either the USER or CURATOR role.
    // More complex expressions can also include the method's parameters and their properties.

    @PreAuthorize("hasAnyRole('$USER', '$CURATOR')")
    fun getBookById(id: UUID): BookRecord? {
        return repository.findById(id)
    }

    @PreAuthorize("hasAnyRole('$USER', '$CURATOR')")
    fun getBooksByIsbn(isbn: Isbn): List<BookRecord> {
        return repository.findByIsbn(isbn)
    }

    // @Secured takes the name of one or more authorities which are allowed to invoke the annotated method.

    @Secured(ROLE_CURATOR)
    fun deleteBookById(id: UUID): Boolean {
        return repository.deleteById(id)
    }

}
