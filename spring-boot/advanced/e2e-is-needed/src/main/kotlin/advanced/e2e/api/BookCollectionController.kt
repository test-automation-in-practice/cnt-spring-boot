package advanced.e2e.api

import advanced.e2e.domain.BookCollection
import advanced.e2e.domain.BookDataNotFoundException
import advanced.e2e.domain.BookRecord
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/books")
class BookCollectionController(
    private val collection: BookCollection
) {

    private val log = getLogger(javaClass)

    @ResponseStatus(CREATED)
    @PostMapping("/{isbn}")
    fun addByIsbn(@PathVariable isbn: String): BookRecord =
        collection.addBookByIsbn(isbn).getOrThrow()

    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    fun handle(e: BookDataNotFoundException) {
        log.debug(e.message, e)
    }

}
