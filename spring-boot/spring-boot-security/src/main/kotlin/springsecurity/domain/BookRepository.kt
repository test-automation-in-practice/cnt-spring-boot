package springsecurity.domain

import java.util.UUID

interface BookRepository {
    fun create(record: BookRecord): BookRecord
    fun findById(id: UUID): BookRecord?
    fun delete(id: UUID): Boolean
}
