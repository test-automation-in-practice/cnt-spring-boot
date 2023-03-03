package example.spring.boot.graphql.business

import java.util.UUID

const val PATTERN_TITLE = """(?U)\w[\w -]*""" // unicode word characters
const val PATTERN_ISBN = """(\d){10}|(\d){13}"""

private val titleRegex = Regex(PATTERN_TITLE)
private val isbnRegex = Regex(PATTERN_ISBN)

data class BookRecord(
    val id: UUID,
    val book: Book
)

data class Book(
    val title: Title,
    val isbn: Isbn
)

data class Title(val value: String) {
    init {
        require(value matches titleRegex) { "Title [$value] does not match $titleRegex" }
    }

    override fun toString() = value
}

data class Isbn(val value: String) {
    init {
        require(value matches isbnRegex) { "ISBN [$value] does not match $isbnRegex" }
    }

    override fun toString() = value
}

data class Query(
    val title: Title? = null,
    val isbn: Isbn? = null
)
