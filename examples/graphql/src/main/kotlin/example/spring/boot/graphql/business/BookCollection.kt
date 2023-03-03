package example.spring.boot.graphql.business

import example.spring.boot.graphql.persistence.BookRecordRepository
import org.springframework.stereotype.Service
import org.springframework.util.IdGenerator
import java.util.UUID

@Service
class BookCollection(
    private val idGenerator: IdGenerator,
    private val repository: BookRecordRepository
) {

    fun add(book: Book): BookRecord {
        val id = idGenerator.generateId()
        val bookRecord = BookRecord(id, book)

        return repository.save(bookRecord)
    }

    fun delete(id: UUID): Boolean =
        repository.deleteById(id)

    fun get(id: UUID): BookRecord? =
        repository.getById(id)

    fun getAll(pagination: Pagination): Page<BookRecord> =
        repository.findAll(pagination)

    fun find(query: Query): List<BookRecord> =
        repository.find(query)

}
