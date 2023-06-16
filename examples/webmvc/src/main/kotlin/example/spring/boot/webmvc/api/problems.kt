package example.spring.boot.webmvc.api

import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.web.ErrorResponseException
import java.net.URI
import java.util.UUID

class BookNotFoundProblem(id: UUID) : ErrorResponseException(NOT_FOUND) {
    init {
        body.type = URI("urn:problem-type:book-not-found")
        body.detail = "Book with ID '$id' was not found!"
        body.setProperty("bookId", id)
    }
}
