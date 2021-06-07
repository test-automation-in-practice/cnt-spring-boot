package jpa.books

import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity(name = "BookRecord")
@Table(name = "book_records")
data class BookRecordEntity(
    @Id
    val id: UUID,
    val title: String,
    val isbn: String
)
