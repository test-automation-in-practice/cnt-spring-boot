package example.spring.boot.jdbc.persistence

import example.spring.boot.jdbc.business.Book
import example.spring.boot.jdbc.business.BookRecord
import org.slf4j.LoggerFactory.getLogger
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.IncorrectResultSizeDataAccessException
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import org.springframework.util.IdGenerator
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Repository
class ClientBasedBookRecordRepository(
    private val client: JdbcClient,
    private val idGenerator: IdGenerator
) {

    private val log = getLogger(javaClass)

    fun create(book: Book): BookRecord {
        val id = idGenerator.generateId()
        return try {
            client.sql("INSERT INTO book_records (id, title, isbn) VALUES (:id, :title, :isbn)")
                .param("id", id)
                .param("title", book.title)
                .param("isbn", book.isbn)
                .update()
            BookRecord(id, book)
        } catch (e: DuplicateKeyException) {
            log.warn("ID collision occurred for ID [{}] - retrying with new ID", id)
            create(book)
        }
    }

    fun update(bookRecord: BookRecord): Boolean =
        client.sql("UPDATE book_records SET title = :title, isbn = :isbn WHERE id = :id")
            .param("id", bookRecord.id)
            .param("title", bookRecord.book.title)
            .param("isbn", bookRecord.book.isbn)
            .update() != 0

    fun findBy(id: UUID): BookRecord? =
        try {
            client.sql("SELECT * FROM book_records WHERE id = :id")
                .param("id", id)
                .query { rs, _ ->
                    val title = rs.getString("title")!!
                    val isbn = rs.getString("isbn")
                    BookRecord(id, Book(title, isbn))
                }
                .optional()
                .getOrNull()
        } catch (e: IncorrectResultSizeDataAccessException) {
            null
        }

    fun deleteBy(id: UUID): Boolean =
        client.sql("DELETE FROM book_records WHERE id = :id")
            .param("id", id)
            .update() != 0

}
