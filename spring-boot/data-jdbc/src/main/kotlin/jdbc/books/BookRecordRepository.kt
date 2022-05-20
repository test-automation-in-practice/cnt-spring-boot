package jdbc.books

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

interface BookRecordRepository : CrudRepository<BookRecord, UUID> {

    @Query("SELECT * FROM BOOK_RECORDS br WHERE br.title = :title")
    fun findByTitle(title: String): List<BookRecord>

}
