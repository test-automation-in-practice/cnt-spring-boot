package web.business

import org.springframework.stereotype.Service
import java.util.UUID

@Service
class Library {

    private val database = mutableMapOf<UUID, BookRecord>()

    fun add(book: Book): BookRecord {
        val id = UUID.randomUUID()
        val bookRecord = BookRecord(id, book)
        database[id] = bookRecord
        return bookRecord
    }

    fun delete(id: UUID) {
        database.remove(id) ?: throw BookRecordNotFoundException(id)
    }

    fun get(id: UUID): BookRecord {
        return database[id] ?: throw BookRecordNotFoundException(id)
    }

    fun getAll(): List<BookRecord> {
        return database.values.toList()
    }

}
