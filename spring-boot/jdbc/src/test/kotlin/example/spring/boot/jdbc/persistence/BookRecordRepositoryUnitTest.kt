package example.spring.boot.jdbc.persistence

import example.spring.boot.jdbc.business.Book
import example.spring.boot.jdbc.business.BookRecord
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.flywaydb.core.Flyway
import org.h2.jdbcx.JdbcDataSource
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.util.IdGenerator
import java.util.UUID.randomUUID

/**
 * This unit test uses does not rely on any Spring Boot mechanics.
 * An H2 in-memory database is used to simulate an actual PostgreSQL database.
 * Flyway migration is triggered manually.
 *
 * The actual tests are exactly the same as [BookRecordRepositoryTechnologyIntegrationTest].
 */
internal class BookRecordRepositoryUnitTest {

    val dataSource = JdbcDataSource()
        .apply { setUrl("jdbc:h2:mem:${randomUUID()};MODE=PostgreSQL;DB_CLOSE_DELAY=-1") }
        .apply { user = "sa"; password = "sa" }
        .also {
            Flyway.configure()
                .dataSource(it)
                .locations("classpath:db/migration")
                .load()
                .migrate()
        }

    val jdbcTempalte = NamedParameterJdbcTemplate(dataSource)
    val idGenerator: IdGenerator = mockk()
    val cut = BookRecordRepository(jdbcTempalte, idGenerator)

    val cleanCode = Book("Clean Code", "9780132350884")
    val cleanArchitecture = Book("Clean Architecture", "9780134494166")

    val id1 = randomUUID()
    val id2 = randomUUID()

    @BeforeEach
    fun resetMocks() {
        clearMocks(idGenerator)
    }

    @Nested
    inner class Creating {

        @Test
        fun `creating a book returns a book record`() {
            every { idGenerator.generateId() } returns id1
            val bookRecord = cut.create(cleanCode)
            assertThat(bookRecord).isEqualTo(BookRecord(id1, cleanCode))
        }

        @Test
        fun `duplicated keys during creation are handled`() {
            every { idGenerator.generateId() } returnsMany listOf(id1, id1, id2)

            val bookRecord1 = cut.create(cleanArchitecture)
            val bookRecord2 = cut.create(cleanArchitecture)

            assertThat(bookRecord1.id).isEqualTo(id1)
            assertThat(bookRecord2.id).isEqualTo(id2)

            verify(exactly = 3) { idGenerator.generateId() } // there was a retry
        }

    }

    @Nested
    inner class Getting {

        @Test
        fun `existing book records can be found by id`() {
            every { idGenerator.generateId() } returns id1

            val bookRecord = cut.create(cleanCode)
            val foundBookRecord = cut.findBy(bookRecord.id)

            assertThat(foundBookRecord).isEqualTo(bookRecord)
        }

        @Test
        fun `non existing book records are returned as null when trying to find them by id`() {
            assertThat(cut.findBy(id2)).isNull()
        }

    }

    @Nested
    inner class Updating {

        @Test
        fun `updating an existing book record changes all its data except the id`() {
            every { idGenerator.generateId() } returns id1

            val created = cut.create(cleanCode)
            assertThat(cut.findBy(id1)).isEqualTo(created)

            val changed = created.copy(book = cleanArchitecture)
            val wasUpdated = cut.update(changed)
            assertThat(wasUpdated).isTrue()

            assertThat(cut.findBy(id1)).isEqualTo(changed)
        }

        @Test
        fun `updating non existing book returns false`() {
            val bookRecord = BookRecord(id2, cleanCode)
            val wasUpdated = cut.update(bookRecord)
            assertThat(wasUpdated).isFalse()
        }

    }

    @Nested
    inner class Deleting {

        @Test
        fun `existing book records can be deleted by id`() {
            every { idGenerator.generateId() } returns id1

            val bookRecord = cut.create(cleanCode)
            assertThat(cut.findBy(id1)).isEqualTo(bookRecord)

            val wasDeleted = cut.deleteBy(id1)
            assertThat(wasDeleted).isTrue()
            assertThat(cut.findBy(bookRecord.id)).isNull()
        }

        @Test
        fun `deleting non existing book record throws exception`() {
            val wasDeleted = cut.deleteBy(id2)
            assertThat(wasDeleted).isFalse()
        }

    }

}
