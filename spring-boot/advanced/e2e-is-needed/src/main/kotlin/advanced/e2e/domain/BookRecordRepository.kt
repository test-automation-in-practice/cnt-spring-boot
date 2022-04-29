package advanced.e2e.domain

import java.util.*

interface BookRecordRepository {
    fun create(book: Book): BookRecord
    fun getById(id: UUID): BookRecord?
}
