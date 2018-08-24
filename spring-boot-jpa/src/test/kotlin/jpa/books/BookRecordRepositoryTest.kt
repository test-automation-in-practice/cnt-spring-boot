package jpa.books

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@DataJpaTest
@ExtendWith(SpringExtension::class)
internal class BookRecordRepositoryTest {

    @Autowired lateinit var cut: BookRecordRepository

    @Test fun `entity can be saved`() {
        val id = UUID.randomUUID()
        val entity = BookRecordEntity(id, "Clean Code", "9780132350884")
        val savedEntity = cut.save(entity)
        assertThat(savedEntity).isEqualTo(entity)
    }

    @Test fun `entity can be found by id`() {
        val id = UUID.randomUUID()
        val savedEntity = cut.save(BookRecordEntity(id, "Clean Code", "9780132350884"))
        val foundEntity = cut.findById(id)
        assertThat(foundEntity).hasValue(savedEntity)
    }

    @Test fun `entity can be found by title`() {
        val e1 = cut.save(BookRecordEntity(UUID.randomUUID(), "Clean Code", "9780132350884"))
        val e2 = cut.save(BookRecordEntity(UUID.randomUUID(), "Clean Architecture", "9780134494166"))
        val e3 = cut.save(BookRecordEntity(UUID.randomUUID(), "Clean Code", "9780132350884"))
        val foundEntities = cut.findByTitle("Clean Code")
        assertThat(foundEntities)
                .contains(e1, e3)
                .doesNotContain(e2)
    }

}