package springsecurity.domain

import springsecurity.security.CURATOR
import javax.annotation.security.RolesAllowed

/**
 * Example using [RolesAllowed] annotation.
 *
 * `@RolesAllowed` takes the name of one or more roles which are allowed to
 * access / invoke the annotated method. Note that the used role name is _NOT_
 * prefixed with `ROLE_` in contrast to some of the other available annotations.
 */
@Usecase
class AddBookUsecase(
    private val repository: BookRepository,
    private val idGenerator: IdGenerator
) {

    @RolesAllowed(CURATOR)
    operator fun invoke(book: Book): BookRecord {
        val record = BookRecord(
            id = idGenerator.generate(),
            book = book
        )
        return repository.create(record)
    }

}
