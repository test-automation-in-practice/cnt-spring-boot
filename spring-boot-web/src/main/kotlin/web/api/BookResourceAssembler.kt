package web.api

import org.springframework.hateoas.mvc.ResourceAssemblerSupport
import org.springframework.stereotype.Component
import web.business.BookRecord

@Component
class BookResourceAssembler
    : ResourceAssemblerSupport<BookRecord, BookResource>(BooksController::class.java, BookResource::class.java) {

    override fun toResource(bookRecord: BookRecord): BookResource = createResourceWithId(bookRecord.id, bookRecord)

    override fun instantiateResource(bookRecord: BookRecord) = BookResource(
        title = bookRecord.book.title.toString(),
        isbn = bookRecord.book.isbn.toString()
    )

}