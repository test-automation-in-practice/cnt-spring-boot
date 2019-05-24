package consumertwo

import org.springframework.hateoas.ResourceSupport


data class Book(
    val isbn: String,
    val title: String,
    val numberOfPages: Int?
) : ResourceSupport()