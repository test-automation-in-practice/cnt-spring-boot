package example.spring.boot.data.redis.persistence

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface BookRecordRepository : CrudRepository<BookRecord, UUID> {
    fun findByTitle(title: String): List<BookRecord>
}
