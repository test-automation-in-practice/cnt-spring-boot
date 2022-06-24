package example.spring.boot.http.clients.gateways.libraryservice

import java.io.IOException

interface LibraryService {

    @Throws(IOException::class)
    fun addBook(book: Book): CreatedBook

}
