package caching.books

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/books")
class BooksController(
    private val library: Library
) {

    @PostMapping
    fun post(@RequestBody request: PostBookRequest): PostBookResponse {
        val book = Book(isbn = request.isbn, title = request.title)
        val bookRecord = library.addBook(book)
        return PostBookResponse(
            id = bookRecord.id,
            isbn = bookRecord.book.isbn,
            title = bookRecord.book.title,
            numberOfPages = bookRecord.book.numberOfPages
        )
    }

    data class PostBookRequest(val isbn: String, val title: String)
    data class PostBookResponse(val id: UUID, val isbn: String, val title: String, val numberOfPages: Int?)

}
