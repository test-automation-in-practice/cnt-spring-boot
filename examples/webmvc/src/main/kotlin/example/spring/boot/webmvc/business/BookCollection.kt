package example.spring.boot.webmvc.business

import org.springframework.stereotype.Service
import java.util.UUID

@Service
class BookCollection {

    private val database = mutableMapOf<UUID, BookRecord>()

    fun add(book: Book): BookRecord {
        val id = UUID.randomUUID()
        val bookRecord = BookRecord(id, book)
        database[id] = bookRecord
        return bookRecord
    }

    fun delete(id: UUID) {
        database.remove(id)
    }

    fun get(id: UUID): BookRecord? = database[id]
    fun getAll(): List<BookRecord> = database.values.toList()

}
