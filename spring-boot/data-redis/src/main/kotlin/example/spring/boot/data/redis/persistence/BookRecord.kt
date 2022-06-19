package example.spring.boot.data.redis.persistence

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed
import java.util.UUID

@RedisHash("BookRecord")
data class BookRecord(
    @Id
    val id: UUID,
    @Indexed
    val title: String,
    val isbn: String
)
