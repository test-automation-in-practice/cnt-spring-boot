package example.spring.boot.data.jdbc.persistence

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface BookRepository : CrudRepository<BookEntity, UUID> {

    @Query("SELECT * FROM books b WHERE b.title = :title")
    fun findByTitle(title: String): List<BookEntity>

}
