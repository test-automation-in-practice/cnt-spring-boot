package example.spring.boot.graphql.persistence

import example.spring.boot.graphql.business.BookRecord
import example.spring.boot.graphql.business.Page
import example.spring.boot.graphql.business.Pagination
import org.springframework.stereotype.Repository
import java.util.UUID
import kotlin.math.ceil

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

    fun findAll(pagination: Pagination): Page<BookRecord> {
        val content = database.values.sortedBy { it.book.title.value }
            .drop(pagination.index * pagination.size)
            .take(pagination.size)

        return Page(
            content = content,
            index = pagination.index,
            size = pagination.size,
            totalPages = ceil(database.size.toDouble() / pagination.size).toInt(),
            totalElements = database.size
        )
    }

    fun deleteAll() {
        database.clear()
    }
}
