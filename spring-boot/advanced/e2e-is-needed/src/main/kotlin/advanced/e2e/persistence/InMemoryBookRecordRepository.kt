package advanced.e2e.persistence

import advanced.e2e.domain.Book
import advanced.e2e.domain.BookRecord
import advanced.e2e.domain.BookRecordRepository
import org.springframework.stereotype.Component
import org.springframework.util.IdGenerator
import java.util.*

@Component
class InMemoryBookRecordRepository(
    private val idGenerator: IdGenerator
) : BookRecordRepository {

    private val database = mutableMapOf<UUID, BookRecord>()

    override fun create(book: Book): BookRecord {
        val id = idGenerator.generateId()
        val record = BookRecord(id, book)
        database[id] = record
        return record
    }

    override fun getById(id: UUID): BookRecord? = database[id]

}
