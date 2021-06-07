package jdbc.books

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.util.UUID

private class BookRepositoryTestConfiguration {
    @Bean fun idGenerator(): IdGenerator = mockk()
    @Bean fun repository(jdbcTemplate: NamedParameterJdbcTemplate, idGenerator: IdGenerator) =
        BooksRepository(jdbcTemplate, idGenerator)
}

@JdbcTest
@Import(BookRepositoryTestConfiguration::class)
internal class BookRepositoryTest(
    @Autowired val idGenerator: IdGenerator,
    @Autowired val cut: BooksRepository
) {

    companion object {
        val cleanCode = Book("Clean Code", "9780132350884")
        val cleanArchitecture = Book("Clean Architecture", "9780134494166")
    }

    @BeforeEach fun resetMocks() = clearAllMocks()

    @Test fun `creating a book returns a book record`() {
        val fixedId = UUID.randomUUID()
        every { idGenerator.generateId() } returns fixedId

        val bookRecord = cut.create(cleanCode)
        with(bookRecord) {
            assertThat(id).isEqualTo(fixedId)
            assertThat(book.title).isEqualTo("Clean Code")
            assertThat(book.isbn).isEqualTo("9780132350884")
        }
    }

    @Test fun `duplicated keys during creation are handled`() {
        val fixedId1 = UUID.randomUUID()
        val fixedId2 = UUID.randomUUID()

        every { idGenerator.generateId() } returnsMany listOf(fixedId1, fixedId1, fixedId2)

        with(cut.create(cleanCode)) {
            assertThat(id).isEqualTo(fixedId1)
        }

        with(cut.create(cleanArchitecture)) {
            assertThat(id).isEqualTo(fixedId2)
        }

        verify(exactly = 3) { idGenerator.generateId() }
    }

    @Test fun `updating an existing book record changes all its data except the id`() {
        val fixedId = UUID.randomUUID()
        every { idGenerator.generateId() } returns fixedId

        val bookRecord = cut.create(cleanCode)

        with(cut.findBy(bookRecord.id)!!) {
            assertThat(id).isEqualTo(fixedId)
            assertThat(book.title).isEqualTo("Clean Code")
            assertThat(book.isbn).isEqualTo("9780132350884")
        }

        cut.update(bookRecord.copy(book = cleanArchitecture))

        with(cut.findBy(bookRecord.id)!!) {
            assertThat(id).isEqualTo(fixedId)
            assertThat(book.title).isEqualTo("Clean Architecture")
            assertThat(book.isbn).isEqualTo("9780134494166")
        }
    }

    @Test fun `updating non existing book record throws exception`() {
        val bookRecord = BookRecord(UUID.randomUUID(), cleanCode)

        assertThrows<BookRecordNotFoundException> {
            cut.update(bookRecord)
        }
    }

    @Test fun `existing book records can be found by id`() {
        every { idGenerator.generateId() } returns UUID.randomUUID()

        val bookRecord = cut.create(cleanCode)
        val foundBookRecord = cut.findBy(bookRecord.id)

        assertThat(foundBookRecord).isEqualTo(bookRecord)
    }

    @Test fun `non existing book records are returned as null when trying to find them by id`() {
        assertThat(cut.findBy(UUID.randomUUID())).isNull()
    }

    @Test fun `existing book records can be deleted by id`() {
        every { idGenerator.generateId() } returns UUID.randomUUID()

        val bookRecord = cut.create(cleanCode)
        assertThat(cut.findBy(bookRecord.id)).isNotNull()

        cut.deleteBy(bookRecord.id)
        assertThat(cut.findBy(bookRecord.id)).isNull()
    }

    @Test fun `deleting non existing book record throws exception`() {
        assertThrows<BookRecordNotFoundException> {
            cut.deleteBy(UUID.randomUUID())
        }
    }

}
