package springsecurity.domain

import org.springframework.security.access.prepost.PostAuthorize
import org.springframework.security.access.prepost.PreAuthorize
import springsecurity.security.USER
import java.util.UUID

/**
 * Example using [PreAuthorize] annotation.
 *
 * `@PreAuthorize` uses an 'access-control expression' which must evaluate to
 * `true` in order to allow access / invoke the annotated method. The simples
 * expression uses `hasRole` in order to limit the access to authenticated
 * users with a given role. Note that the role name for this expression is _NOT_
 * prefixed with `ROLE_` in contrast to some of the other available annotations.
 *
 * More complex expressions can also include the method's parameters and their
 * properties.
 *
 * @see PostAuthorize
 */
@Usecase
class GetBookByIdUsecase(
    private val repository: BookRepository
) {

    @PreAuthorize("hasRole('$USER')")
    operator fun invoke(id: UUID): BookRecord? {
        return repository.findById(id)
    }

}
