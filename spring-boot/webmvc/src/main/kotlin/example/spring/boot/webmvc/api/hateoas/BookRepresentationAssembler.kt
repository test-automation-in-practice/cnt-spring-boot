package example.spring.boot.webmvc.api.hateoas

import example.spring.boot.webmvc.business.BookRecord
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport
import org.springframework.stereotype.Component

@Component
class BookRepresentationAssembler : RepresentationModelAssemblerSupport<BookRecord, BookRepresentation>(
    HateoasBookController::class.java,
    BookRepresentation::class.java
) {
    override fun toModel(entity: BookRecord) = createModelWithId(entity.id, entity)
    override fun instantiateModel(entity: BookRecord) =
        BookRepresentation(title = entity.book.title.toString(), isbn = entity.book.isbn.toString())
}
