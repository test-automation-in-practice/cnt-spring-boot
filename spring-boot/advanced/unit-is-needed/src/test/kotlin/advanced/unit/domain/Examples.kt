package advanced.unit.domain

import java.util.UUID

object Examples {

    val id_bobiverse1 = UUID.fromString("b3fc0be8-463e-4875-9629-67921a1e00f4")
    val label_bobiverse1 = "We Are Legion (We Are Bob)"
    val book_bobiverse1 = Book(
        id = id_bobiverse1,
        label = label_bobiverse1
    )

    val id_bobiverse2 = UUID.fromString("aadfe61a-4bbd-44c4-85ba-6bddbe7d10a2")
    val label_bobiverse2 = "For We Are Many"
    val book_bobiverse2 = Book(
        id = id_bobiverse2,
        label = label_bobiverse2
    )

    val id_bobiverse3 = UUID.fromString("02f33cf8-a8a4-4e94-b44e-21c5c0603dd7")
    val label_bobiverse3 = "All These Worlds"
    val book_bobiverse3 = Book(
        id = id_bobiverse3,
        label = label_bobiverse3
    )

}
