package example.spring.boot.data.mongodb.persistence

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.util.UUID

interface BookRepository : MongoRepository<BookDocument, UUID> {

    @Query("{ 'book.title' : ?0 }")
    fun findByTitle(title: String): List<BookDocument>

}
