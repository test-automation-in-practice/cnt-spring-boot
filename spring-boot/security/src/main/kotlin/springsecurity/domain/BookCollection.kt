package springsecurity.domain

import org.springframework.security.access.annotation.Secured
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component
import org.springframework.util.IdGenerator
import springsecurity.domain.model.Book
import springsecurity.domain.model.BookRecord
import springsecurity.security.Authorities.ROLE_CURATOR
import springsecurity.security.Roles.CURATOR
import springsecurity.security.Roles.USER
import java.util.*
import javax.annotation.security.RolesAllowed

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

    // @Secured takes the name of one or more authorities which are allowed to invoke the annotated method.

    @Secured(ROLE_CURATOR)
    fun deleteBookById(id: UUID): Boolean {
        return repository.deleteById(id)
    }

}
