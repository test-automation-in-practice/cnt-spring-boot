package web.api

import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.core.Relation

@Relation(
    value = "book",
    collectionRelation = "books"
)
data class BookResource(
    val title: String,
    val isbn: String
) : ResourceSupport()