package springsecurity.api

import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import springsecurity.domain.AddBookUsecase
import springsecurity.domain.Book
import springsecurity.domain.BookRecord

@RestController
class AddBookRestController(
    private val addBook: AddBookUsecase
) {

    @ResponseStatus(CREATED)
    @PostMapping("/api/books")
    fun postBook(@RequestBody book: Book): BookRecord {
        return addBook(book)
    }

}
