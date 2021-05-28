package web.api

import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation

@Relation(value = "book", collectionRelation = "books")
data class BookResource(
    val title: String,
    val isbn: String
) : RepresentationModel<BookResource>()
