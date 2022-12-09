package example.spring.boot.data.jdbc.persistence

import example.spring.boot.data.jdbc.model.Book
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Embedded
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty.USE_EMPTY
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("books")
data class BookEntity(
    @Id val id: UUID,
    @Embedded(onEmpty = USE_EMPTY) val book: Book,
    @Version var version: Long = 0
)
