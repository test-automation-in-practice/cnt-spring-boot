package starter

import starter.books.core.Book
import starter.books.core.BookRecord
import starter.books.enrichment.BookInformation
import java.time.Instant
import java.util.*

object Examples {

    val book_cleanCode = Book(
        isbn = "9780132350884",
        title = "Clean Code"
    )

    val book_cleanCode_enriched = Book(
        isbn = "9780132350884",
        title = "Clean Code",
        description = "Lorem Ipsum ...",
        authors = listOf("Robert C. Martin", "Dean Wampler"),
        numberOfPages = 464
    )

    val additional_cleanCode = BookInformation(
        description = "Lorem Ipsum ...",
        authors = listOf("Robert C. Martin", "Dean Wampler"),
        numberOfPages = 464
    )

    val id_cleanCode = UUID.fromString("b3fc0be8-463e-4875-9629-67921a1e00f4")

    val record_cleanCode = BookRecord(
        id = id_cleanCode,
        book = book_cleanCode,
        timestamp = Instant.parse("2021-06-25T12:34:56.789Z")
    )

    val record_cleanCode_enriched = BookRecord(
        id = id_cleanCode,
        book = book_cleanCode_enriched,
        timestamp = Instant.parse("2021-06-25T12:34:56.789Z")
    )

    val id_cleanArchitecture = UUID.fromString("aadfe61a-4bbd-44c4-85ba-6bddbe7d10a2")

}
