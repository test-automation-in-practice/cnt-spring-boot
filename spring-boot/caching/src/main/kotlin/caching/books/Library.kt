package caching.books

import org.springframework.stereotype.Service
import java.util.*

@Service
class Library(
    private val openLibrary: OpenLibraryAccessor
) {

    fun addBook(book: Book): BookRecord {
        val numberOfPages = openLibrary.getNumberOfPages(book.isbn)
        val enrichedBook = book.copy(numberOfPages = numberOfPages)
        // TODO: store book in some kind of storage system (e.g. MongoDB)
        return BookRecord(UUID.randomUUID(), enrichedBook)
    }

}

data class Book(val isbn: String, val title: String, val numberOfPages: Int? = null)
data class BookRecord(val id: UUID, val book: Book)
