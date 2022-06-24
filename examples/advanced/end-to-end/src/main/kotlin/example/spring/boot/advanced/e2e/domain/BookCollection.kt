package example.spring.boot.advanced.e2e.domain

import org.springframework.stereotype.Component
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

@Component
class BookCollection(
    private val catalogue: BookCatalogue,
    private val repository: BookRecordRepository,
    private val mediaCollection: MediaCollection
) {

    // private val executor: Executor = DelegatingSecurityContextExecutor(Executors.newCachedThreadPool())

    fun addBookByIsbn(isbn: String): Result<BookRecord> {
        val book = catalogue.findByIsbn(isbn) ?: return failure(BookDataNotFoundException(isbn))
        val bookRecord = repository.create(book)
        // executor.execute { mediaCollection.register(bookRecord) }
        // runAsync { mediaCollection.register(bookRecord) }
        mediaCollection.register(bookRecord)
        return success(bookRecord)
    }

}
