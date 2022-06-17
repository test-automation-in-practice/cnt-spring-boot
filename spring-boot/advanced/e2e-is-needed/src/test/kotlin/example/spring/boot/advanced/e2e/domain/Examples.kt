package example.spring.boot.advanced.e2e.domain

import java.util.UUID

object Examples {

    val id_bobiverse1 = UUID.fromString("b3fc0be8-463e-4875-9629-67921a1e00f4")
    val isbn_bobiverse1 = "9781680680584"
    val title_bobiverse1 = "We Are Legion (We Are Bob)"
    val book_bobiverse1 = Book(
        isbn = isbn_bobiverse1,
        title = title_bobiverse1
    )
    val record_bobiverse1 = BookRecord(
        id = id_bobiverse1,
        book = book_bobiverse1
    )

    val id_bobiverse2 = UUID.fromString("aadfe61a-4bbd-44c4-85ba-6bddbe7d10a2")
    val isbn_bobiverse2 = "9781680680591"
    val book_bobiverse2 = Book(
        isbn = isbn_bobiverse2,
        title = "For We Are Many"
    )
    val record_bobiverse2 = BookRecord(
        id = id_bobiverse2,
        book = book_bobiverse2
    )

    val id_bobiverse3 = UUID.fromString("02f33cf8-a8a4-4e94-b44e-21c5c0603dd7")
    val isbn_bobiverse3 = "9781680680607"
    val book_bobiverse3 = Book(
        isbn = isbn_bobiverse3,
        title = "All These Worlds"
    )
    val record_bobiverse3 = BookRecord(
        id = id_bobiverse3,
        book = book_bobiverse3
    )

}
