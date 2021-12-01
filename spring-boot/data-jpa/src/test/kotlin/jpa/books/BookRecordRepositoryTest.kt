package jpa.books

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.util.UUID.randomUUID

@DataJpaTest
internal class BookRecordRepositoryTest(
    @Autowired val cut: BookRecordRepository
) {

    @Test
    fun `entity can be saved`() {
        val entity = BookRecordEntity(randomUUID(), "Clean Code", "9780132350884")
        val savedEntity = cut.save(entity)
        assertThat(savedEntity).isEqualTo(entity)
    }

    @Test
    fun `entity can be found by id`() {
        val id = randomUUID()
        val savedEntity = cut.save(BookRecordEntity(id, "Clean Code", "9780132350884"))
        val foundEntity = cut.findById(id)
        assertThat(foundEntity).hasValue(savedEntity)
    }

    @Test
    fun `entity can be found by title`() {
        val e1 = cut.save(BookRecordEntity(randomUUID(), "Clean Code", "9780132350884"))
        val e2 = cut.save(BookRecordEntity(randomUUID(), "Clean Architecture", "9780134494166"))
        val e3 = cut.save(BookRecordEntity(randomUUID(), "Clean Code", "9780132350884"))
        val foundEntities = cut.findByTitle("Clean Code")
        assertThat(foundEntities)
            .contains(e1, e3)
            .doesNotContain(e2)
    }

}
