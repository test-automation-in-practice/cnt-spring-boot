package example.spring.boot.advanced.e2e.domain

import java.util.UUID

interface BookRecordRepository {
    fun create(book: Book): BookRecord
    fun getById(id: UUID): BookRecord?
}
