package example.spring.boot.graphql.business

import java.util.UUID

private val isbnPattern = Regex("""(\d{3}-?)(\d){10}""")

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
    init {
        require(value matches isbnPattern) { "ISBN [$value] does not match $isbnPattern" }
    }

    override fun toString() = value
}
