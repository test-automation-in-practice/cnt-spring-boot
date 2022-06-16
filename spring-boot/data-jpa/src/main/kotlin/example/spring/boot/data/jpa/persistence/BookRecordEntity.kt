package example.spring.boot.data.jpa.persistence

import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "book_records")
data class BookRecordEntity(
    @Id
    val id: UUID,
    val title: String,
    val isbn: String
)
