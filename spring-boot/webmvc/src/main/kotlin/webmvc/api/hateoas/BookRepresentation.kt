package webmvc.api.hateoas

import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport
import org.springframework.stereotype.Component
import webmvc.business.BookRecord

@Relation(value = "book", collectionRelation = "books")
data class BookRepresentation(
    val title: String,
    val isbn: String
) : RepresentationModel<BookRepresentation>()

@Component
class BookRepresentationAssembler : RepresentationModelAssemblerSupport<BookRecord, BookRepresentation>(
    HateoasBookController::class.java,
    BookRepresentation::class.java
) {
    override fun toModel(entity: BookRecord) = createModelWithId(entity.id, entity)
    override fun instantiateModel(entity: BookRecord) =
        BookRepresentation(title = entity.book.title.toString(), isbn = entity.book.isbn.toString())
}
