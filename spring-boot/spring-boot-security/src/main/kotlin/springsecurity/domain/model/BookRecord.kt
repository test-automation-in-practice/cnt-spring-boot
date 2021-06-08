package springsecurity.domain.model

import java.util.UUID

data class BookRecord(
    val id: UUID,
    val book: Book
)
