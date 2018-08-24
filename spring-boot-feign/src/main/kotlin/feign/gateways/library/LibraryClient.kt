package feign.gateways.library

import feign.Headers
import feign.RequestLine


@Headers("Content-Type: application/json")
internal interface LibraryClient {

    @RequestLine("POST /api/books")
    fun post(book: Book)

}

data class Book(val title: String, val isbn: String)