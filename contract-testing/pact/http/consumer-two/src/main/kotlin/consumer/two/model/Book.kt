package consumer.two.model

// This service's internal data model of a book - is currently, but not necessarily, similar to the data structure
// of the provider service.

data class Book(
    val isbn: String,
    val title: String,
    val numberOfPages: Int?
)
