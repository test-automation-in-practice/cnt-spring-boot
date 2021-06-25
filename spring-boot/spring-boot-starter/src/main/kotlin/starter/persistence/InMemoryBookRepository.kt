package starter.persistence

import org.springframework.stereotype.Component
import starter.books.core.BookRecord
import starter.books.core.BookRepository
import java.time.Clock
import java.util.UUID

@Component
class InMemoryBookRepository(
    private val clock: Clock
) : BookRepository {

    private val database = mutableMapOf<UUID, BookRecord>()

    override fun save(record: BookRecord): BookRecord {
        val modifiedRecord = record.copy(timestamp = clock.instant())
        database[record.id] = modifiedRecord
        return modifiedRecord
    }

    override fun findById(id: UUID): BookRecord? {
        return database[id]
    }

    override fun deleteById(id: UUID): Boolean {
        return database.remove(id) != null
    }

}
