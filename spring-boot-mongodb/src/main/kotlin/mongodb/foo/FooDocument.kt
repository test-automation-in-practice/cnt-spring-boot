package mongodb.foo

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "foos")
data class FooDocument(
        @Id
        val id: UUID,
        val bar: String,
        val xur: Int
)