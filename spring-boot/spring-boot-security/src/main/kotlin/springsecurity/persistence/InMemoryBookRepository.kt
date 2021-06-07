package springsecurity.persistence

import org.springframework.stereotype.Component
import springsecurity.domain.BookRecord
import springsecurity.domain.BookRepository
import java.util.UUID

@Component
class InMemoryBookRepository : BookRepository {

    private val database: MutableMap<UUID, BookRecord> = mutableMapOf()

    override fun create(record: BookRecord): BookRecord {
        val id = record.id
        if (database.containsKey(id)) {
            error("record $id already exits")
        }
        database[id] = record
        return record
    }

    override fun findById(id: UUID): BookRecord? {
        return database[id]
    }

    override fun delete(id: UUID): Boolean {
        return database.remove(id) != null
    }

}
