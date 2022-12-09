package example.spring.boot.data.jpa.model

@NoArgConstructor
data class Book(
    val isbn: Isbn,
    val title: Title
)
