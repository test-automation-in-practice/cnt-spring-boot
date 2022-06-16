package example.spring.boot.data.jdbc.persistence

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("book_records")
data class BookRecordEntity(
    @Id
    val id: UUID,
    val title: String,
    val isbn: String,
    @Version
    var version: Long = 0
)
