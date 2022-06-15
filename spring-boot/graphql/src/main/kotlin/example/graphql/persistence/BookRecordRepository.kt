package example.graphql.persistence

import example.graphql.business.BookRecord
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class BookRecordRepository {

    private val database = mutableMapOf<UUID, BookRecord>()

    fun save(bookRecord: BookRecord): BookRecord {
        database[bookRecord.id] = bookRecord
        return bookRecord
    }

    fun deleteById(id: UUID): Boolean =
        database.remove(id) != null

    fun getById(id: UUID): BookRecord? =
        database[id]

    fun findAll(): List<BookRecord> =
        database.values.toList()

    fun deleteAll() {
        database.clear()
    }

}
