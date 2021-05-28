package consumertwo

import org.springframework.hateoas.RepresentationModel

data class Book(
    val isbn: String,
    val title: String,
    val numberOfPages: Int?
) : RepresentationModel<Book>()
