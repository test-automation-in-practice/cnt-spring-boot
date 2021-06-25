package starter.books.enrichment

data class BookInformation(
    val description: String? = null,
    val authors: List<String> = emptyList(),
    val numberOfPages: Int? = null
)
