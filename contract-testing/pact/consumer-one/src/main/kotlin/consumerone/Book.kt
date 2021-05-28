package consumerone

data class Book(
    val isbn: String,
    val title: String,
    val authors: List<String>?
)
