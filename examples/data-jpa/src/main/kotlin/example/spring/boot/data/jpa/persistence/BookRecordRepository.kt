package example.spring.boot.data.jpa.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface BookRecordRepository : JpaRepository<BookRecordEntity, UUID> {

    @Query("SELECT bre FROM BookRecordEntity bre WHERE bre.title = :title")
    fun findByTitle(title: String): List<BookRecordEntity>

}
