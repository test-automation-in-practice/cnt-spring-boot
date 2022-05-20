package jdbc.books

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.data.relational.core.conversion.DbActionExecutionException
import org.springframework.test.context.ActiveProfiles
import java.util.UUID.randomUUID

@ActiveProfiles("test")
@DataJdbcTest
internal class BookRecordRepositoryTest(
    @Autowired val cut: BookRecordRepository
) {

    @Test
    fun `entity can be saved`() {
        val entity = BookRecord(randomUUID(), "Clean Code", "9780132350884")
        val savedEntity = cut.save(entity)
        assertThat(savedEntity).isEqualTo(entity)
    }

    @Test
    fun `entity can not be saved in lower version`() {
        val savedEntity = BookRecord(randomUUID(), "Clean Code", "9780132350884")
            .also {
                cut.save(it)
                cut.save(it)
                it.apply { it.version = 0 }
            }
        assertThatThrownBy { cut.save(savedEntity) }
            .isInstanceOf(DbActionExecutionException::class.java)
            .hasStackTraceContaining("can not be saved in lower version")
    }

    @Test
    fun `entity can not be saved in same version`() {
        val savedEntity = BookRecord(randomUUID(), "Clean Code", "9780132350884")
            .also {
                cut.save(it)
                it.apply { it.version -= 1 }
            }

        assertThatThrownBy { cut.save(savedEntity) }
            .isInstanceOf(DbActionExecutionException::class.java)
            .hasStackTraceContaining("can not be saved in same version")
    }

    @Test
    fun `entity can be found by id`() {
        val id = randomUUID()
        val savedEntity = cut.save(BookRecord(id, "Clean Code", "9780132350884"))
        val foundEntity = cut.findById(id)
        assertThat(foundEntity).hasValue(savedEntity)
    }

    @Test
    fun `entity can be found by title`() {
        val e1 = cut.save(BookRecord(randomUUID(), "Clean Code", "9780132350884"))
        val e2 = cut.save(BookRecord(randomUUID(), "Clean Architecture", "9780134494166"))
        val e3 = cut.save(BookRecord(randomUUID(), "Clean Code", "9780132350884"))
        val foundEntities = cut.findByTitle("Clean Code")
        assertThat(foundEntities)
            .contains(e1, e3)
            .doesNotContain(e2)
    }
}
