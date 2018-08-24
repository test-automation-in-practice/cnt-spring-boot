package jdbc.books

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.*
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@JdbcTest
@ExtendWith(SpringExtension::class)
internal class BookRepositoryTest {

    companion object {
        val cleanCode = Book("Clean Code", "9780132350884")
        val cleanArchitecture = Book("Clean Architecture", "9780134494166")
    }

    @MockBean lateinit var idGenerator: IdGenerator
    @SpyBean lateinit var cut: BooksRepository

    @Test fun `creating a book returns a book record`() {
        val fixedId = UUID.randomUUID()

        given(idGenerator.generateId()).willReturn(fixedId)

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

        given(idGenerator.generateId()).willReturn(fixedId1, fixedId1, fixedId2)

        with(cut.create(cleanCode)) {
            assertThat(id).isEqualTo(fixedId1)
        }

        with(cut.create(cleanArchitecture)) {
            assertThat(id).isEqualTo(fixedId2)
        }

        verify(idGenerator, times(3)).generateId()
    }

    @Test fun `updating an existing book record changes all its data except the id`() {
        val fixedId = UUID.randomUUID()
        given(idGenerator.generateId()).willReturn(fixedId)

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
        given(idGenerator.generateId()).willReturn(UUID.randomUUID())

        val bookRecord = cut.create(cleanCode)
        val foundBookRecord = cut.findBy(bookRecord.id)

        assertThat(foundBookRecord).isEqualTo(bookRecord)
    }

    @Test fun `non existing book records are returned as null when trying to find them by id`() {
        assertThat(cut.findBy(UUID.randomUUID())).isNull()
    }

    @Test fun `existing book records can be deleted by id`() {
        given(idGenerator.generateId()).willReturn(UUID.randomUUID())

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