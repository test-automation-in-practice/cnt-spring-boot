package mongodb.books

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "book_records")
data class BookRecordDocument(
    @Id
    val id: UUID,
    val title: String,
    val isbn: String
)
