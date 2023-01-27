package example.spring.boot.data.jpa.persistence

import example.spring.boot.data.jpa.model.Book
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.util.UUID

@Entity
@Table(name = "books")
class BookEntity(
    @Id val id: UUID,
    @Embedded var book: Book,
    @Version val version: Int = 0
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BookEntity

        if (id != other.id) return false
        if (book != other.book) return false
        if (version != other.version) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + book.hashCode()
        result = 31 * result + version.hashCode()
        return result
    }
}
