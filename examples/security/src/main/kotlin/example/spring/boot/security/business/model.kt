package example.spring.boot.security.business

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import java.util.UUID

private val isbnPattern = Regex("""(\d{3}-?)\d{10}""")
private val titleMaxLength = 100

data class Book(
    val isbn: Isbn,
    val title: Title
)

data class BookRecord(
    val id: UUID,
    val book: Book
)

data class Isbn @JsonCreator constructor(@JsonValue val value: String) {
    init {
        require(value matches isbnPattern) { "[$value] does not match required pattern [$isbnPattern]" }
    }
}

data class Title @JsonCreator constructor(@JsonValue val value: String) {
    init {
        require(value.length <= titleMaxLength) { "[$value] is longer than the max allowed length of $titleMaxLength" }
    }
}
