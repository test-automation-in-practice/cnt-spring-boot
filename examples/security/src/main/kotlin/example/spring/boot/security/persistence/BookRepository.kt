package example.spring.boot.security.persistence

import example.spring.boot.security.business.BookRecord
import example.spring.boot.security.business.Isbn
import java.util.UUID

// We don't need an actual implementation for this showcase.
// Instances needed for testing will be mocked.

interface BookRepository {
    fun save(record: BookRecord): BookRecord
    fun findById(id: UUID): BookRecord?
    fun findByIsbn(isbn: Isbn): List<BookRecord>
    fun deleteById(id: UUID): Boolean
}
