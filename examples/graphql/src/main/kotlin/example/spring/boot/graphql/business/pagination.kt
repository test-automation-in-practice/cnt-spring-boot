package example.spring.boot.graphql.business

data class Pagination(val index: Int, val size: Int)

val pageIndexRange = 0..10_000
val pageSizeRange = 1..250

data class Page<T>(
    val content: List<T>,
    val index: Int,
    val size: Int,
    val totalPages: Int,
    val totalElements: Int
) {
    fun <R> map(block: (T) -> R): Page<R> =
        Page(
            content = content.map(block),
            index = index,
            size = size,
            totalPages = totalPages,
            totalElements = totalElements
        )
}

fun <T> pageOf(
    vararg content: T,
    index: Int = 0,
    size: Int = 25,
    totalPages: Int = 1,
    totalElements: Int = content.size
) = Page(
    content = content.toList(),
    index = index,
    size = size,
    totalPages = totalPages,
    totalElements = totalElements
)
