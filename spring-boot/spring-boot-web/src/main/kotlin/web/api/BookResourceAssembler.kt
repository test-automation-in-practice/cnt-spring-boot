package web.api

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport
import org.springframework.stereotype.Component
import web.business.BookRecord

@Component
class BookResourceAssembler : RepresentationModelAssemblerSupport<BookRecord, BookResource>(
    BooksController::class.java,
    BookResource::class.java
) {

    override fun toModel(entity: BookRecord): BookResource =
        createModelWithId(entity.id, entity)

    override fun instantiateModel(entity: BookRecord): BookResource =
        BookResource(
            title = entity.book.title.toString(),
            isbn = entity.book.isbn.toString()
        )
}
