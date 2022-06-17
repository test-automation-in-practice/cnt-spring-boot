package example.spring.boot.basics.books.enrichment

import example.spring.boot.basics.books.core.Book
import example.spring.boot.basics.books.core.BookRecordCreatedEvent
import example.spring.boot.basics.books.core.BookRepository
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

// This component's job relies on being invoked using Spring Framework mechanisms.

@Component
class BookEnricher(
    private val bookInformationSource: BookInformationSource,
    private val bookRepository: BookRepository
) {

    @Async
    @EventListener
    fun handle(event: BookRecordCreatedEvent) {
        val record = event.bookRecord
        val book = record.book
        val data = bookInformationSource.getBookInformation(book.isbn)

        if (data != null) {
            val enrichedBookRecord = record.copy(book = enrich(book, data))
            bookRepository.save(enrichedBookRecord)
        }
    }

    private fun enrich(originalBook: Book, enrichmentData: BookInformation) =
        originalBook.copy(
            description = enrichmentData.description ?: originalBook.description,
            authors = enrichmentData.authors.takeIf { it.isNotEmpty() } ?: originalBook.authors,
            numberOfPages = enrichmentData.numberOfPages ?: originalBook.numberOfPages
        )

}
