package httpclients.gateways.libraryservice

import java.io.IOException

interface LibraryService {

    @Throws(IOException::class)
    fun addBook(book: Book): CreatedBook

}

data class Book(val title: String, val isbn: String)
data class CreatedBook(val id: String, val title: String, val isbn: String)
