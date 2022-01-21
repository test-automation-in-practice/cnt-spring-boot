package consumer.books

data class Book(
    val isbn: String,
    val title: String
)

data class BookCreatedEvent(
    val type: String,
    val book: Book
)
