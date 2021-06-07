package jpa.books

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface BookRecordRepository : JpaRepository<BookRecordEntity, UUID> {

    @Query("SELECT br FROM BookRecord br WHERE br.title = :title")
    fun findByTitle(title: String): List<BookRecordEntity>

}
