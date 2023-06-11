package example.spring.boot.security.business

import java.util.UUID

object Examples {
    val book_refactoring = Book(
        isbn = Isbn("978-0134757599"),
        title = Title("Refactoring: Improving the Design of Existing Code")
    )
    val id_refactoring: UUID = UUID.fromString("cd690768-74d4-48a8-8443-664975dd46b5")
    val record_refactoring = BookRecord(id = id_refactoring, book = book_refactoring)
}
