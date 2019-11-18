package springsecurity.domain

import org.springframework.security.access.annotation.Secured
import springsecurity.security.ROLE_CURATOR
import java.util.UUID

/**
 * Example using [Secured] annotation.
 *
 * `@Secured` takes the name of one or more roles which are allowed to
 * access / invoke the annotated method. Note that the used role name _MUST BE_
 * prefixed with `ROLE_` in contrast to some of the other available annotations.
 */
@Usecase
class DeleteBookUsecase(
    private val repository: BookRepository
) {

    @Secured(ROLE_CURATOR)
    operator fun invoke(id: UUID): Boolean {
        return repository.delete(id)
    }

}
