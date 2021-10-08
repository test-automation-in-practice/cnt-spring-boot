package web.business

import java.util.*

object Examples {

    val book_cleanCode = Book(
        isbn = Isbn("9780132350884"),
        title = Title("Clean Code")
    )
    val book_cleanArchitecture = Book(
        title = Title("Clean Architecture"),
        isbn = Isbn("9780134494166")
    )

    val id_cleanCode = UUID.fromString("b3fc0be8-463e-4875-9629-67921a1e00f4")
    val record_cleanCode = BookRecord(
        id = id_cleanCode,
        book = book_cleanCode,
    )

    val id_cleanArchitecture = UUID.fromString("7d823198-2ef3-41a6-b780-29ba6723d8c9")
    val record_cleanArchitecture = BookRecord(
        id = id_cleanArchitecture,
        book = book_cleanArchitecture,
    )

}
