package advanced.e2e.persistence

import advanced.e2e.domain.BookRecord
import advanced.e2e.domain.Examples.book_bobiverse1
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.util.IdGenerator
import java.util.UUID.randomUUID

internal class InMemoryBookRecordRepositoryTests {

    val id = randomUUID()

    val idGenerator: IdGenerator = mockk { every { generateId() } returns id }
    val cut = InMemoryBookRecordRepository(idGenerator)

    @Test
    fun `creating a book record stores book with generated ID`() {
        val actual = cut.create(book_bobiverse1)
        val expected = BookRecord(id, book_bobiverse1)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `getting book record by ID returns it if found`() {
        val record = cut.create(book_bobiverse1)
        val actual = cut.getById(record.id)
        assertThat(actual).isEqualTo(record)
    }

    @Test
    fun `getting book record by ID returns null if not found`() {
        val actual = cut.getById(id)
        assertThat(actual).isNull()
    }
}
