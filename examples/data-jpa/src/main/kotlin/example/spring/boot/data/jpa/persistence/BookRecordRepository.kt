package example.spring.boot.data.jpa.persistence

import example.spring.boot.data.jpa.model.Title
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface BookRecordRepository : JpaRepository<BookEntity, UUID> {

    @Query("SELECT b FROM BookEntity b WHERE b.book.title = :title")
    fun findByTitle(title: Title): List<BookEntity>

}
