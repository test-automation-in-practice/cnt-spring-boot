package springsecurity.domain

import springsecurity.domain.model.BookRecord
import java.util.*

// We don't need an actual implementation for this showcase.
// Instances needed for testing will be mocks.

interface BookRepository {
    fun save(record: BookRecord): BookRecord
    fun findById(id: UUID): BookRecord?
    fun deleteById(id: UUID): Boolean
}
