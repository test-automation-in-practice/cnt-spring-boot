package example.spring.boot.data.jdbc.persistence

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface BookRecordRepository : CrudRepository<BookRecordEntity, UUID> {

    @Query("SELECT * FROM book_records br WHERE br.title = :title")
    fun findByTitle(title: String): List<BookRecordEntity>

}
