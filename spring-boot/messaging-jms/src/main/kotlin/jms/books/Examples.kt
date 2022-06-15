package jms.books

import java.util.UUID.fromString

object Examples {

    val cleanCode = BookRecord(
        id = fromString("b3fc0be8-463e-4875-9629-67921a1e00f4"),
        book = Book(
            isbn = "9780132350884",
            title = "Clean Code"
        )
    )

}
