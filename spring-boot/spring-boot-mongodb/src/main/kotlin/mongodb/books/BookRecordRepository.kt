package mongodb.books

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.util.UUID

interface BookRecordRepository : MongoRepository<BookRecordDocument, UUID> {

    @Query("{ 'title' : ?0 }")
    fun findByTitle(title: String): List<BookRecordDocument>

}
