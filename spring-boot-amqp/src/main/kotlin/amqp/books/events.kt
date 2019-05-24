package amqp.books

import java.util.*

sealed class BookEvent {
    abstract val type: String
    abstract val id: UUID
    abstract val title: String
}

data class BookCreated(
    override val id: UUID,
    override val title: String
) : BookEvent() {
    override val type: String = "book-created"
}

data class BookDeleted(
    override val id: UUID,
    override val title: String
) : BookEvent() {
    override val type: String = "book-deleted"
}
