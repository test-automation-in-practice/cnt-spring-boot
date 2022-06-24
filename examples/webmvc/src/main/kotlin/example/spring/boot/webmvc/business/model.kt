package example.spring.boot.webmvc.business

import java.util.UUID

data class BookRecord(
    val id: UUID,
    val book: Book
)

data class Book(
    val title: Title,
    val isbn: Isbn
)

data class Title(val value: String) {
    override fun toString() = value
}

data class Isbn(val value: String) {
    override fun toString() = value
}
