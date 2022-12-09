package example.spring.boot.data.mongodb.persistence

import example.spring.boot.data.mongodb.model.Book
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document(collection = "books")
data class BookDocument(
    @Id val id: UUID,
    val book: Book,
    @Version val version: Long = 0
)
