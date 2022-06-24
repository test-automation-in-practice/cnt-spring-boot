package example.spring.boot.webmvc.api.hateoas

import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation

@Relation(value = "book", collectionRelation = "books")
data class BookRepresentation(
    val title: String,
    val isbn: String
) : RepresentationModel<BookRepresentation>()
