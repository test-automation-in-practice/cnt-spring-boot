package jdbc.books

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.util.*

abstract class VersionedEntity {
    @Version
    var version: Long = 0
}

@Table("BOOK_RECORDS")
data class BookRecord(
    @Id
    val id: UUID,
    val title: String,
    val isbn: String,
) : VersionedEntity()


