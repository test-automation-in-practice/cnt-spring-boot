package example.spring.boot.graphql.api

import example.spring.boot.graphql.business.BookRecord
import example.spring.boot.graphql.business.Isbn
import example.spring.boot.graphql.business.PATTERN_ISBN
import example.spring.boot.graphql.business.PATTERN_TITLE
import example.spring.boot.graphql.business.Query
import example.spring.boot.graphql.business.Title
import jakarta.validation.constraints.Pattern
import java.util.UUID

data class BookRepresentation(
    val id: UUID,
    val title: String,
    val isbn: String
)

fun BookRecord.toRepresentation() =
    BookRepresentation(
        id = id,
        title = book.title.toString(),
        isbn = book.isbn.toString()
    )

/*
 Annotation-based validation needs to be qualified with '@field:' because otherwise the annotation is not found:
 https://stackoverflow.com/questions/35847763/kotlin-data-class-bean-validation-jsr-303
 */

data class QueryInput(
    @field:Pattern(regexp = PATTERN_TITLE)
    val title: String? = null,
    @field:Pattern(regexp = PATTERN_ISBN)
    val isbn: String? = null
) {
    fun toInternal() = Query(
        title = title?.let(::Title),
        isbn = isbn?.let(::Isbn)
    )
}
