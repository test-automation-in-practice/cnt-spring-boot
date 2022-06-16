package example.spring.boot.data.jpa.persistence

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.util.UUID.randomUUID

@DataJpaTest
@ActiveProfiles("test")
internal class BookRecordRepositoryTest(
    @Autowired val cut: BookRecordRepository
) {

    @Test
    fun `entity can be saved`() {
        val entity = bookRecordEntity()
        val savedEntity = cut.save(entity)
        assertThat(savedEntity).isEqualTo(entity)
    }

    @Test
    fun `entity can be found by id`() {
        val savedEntity = cut.save(bookRecordEntity())
        val foundEntity = cut.findById(savedEntity.id)
        assertThat(foundEntity).hasValue(savedEntity)
    }

    @Test
    fun `entity can be found by title`() {
        val e1 = cut.save(bookRecordEntity("Clean Code"))
        val e2 = cut.save(bookRecordEntity("Clean Architecture"))
        val e3 = cut.save(bookRecordEntity("Clean Code"))

        val foundEntities = cut.findByTitle("Clean Code")

        assertThat(foundEntities)
            .contains(e1, e3)
            .doesNotContain(e2)
    }

    private fun bookRecordEntity(title: String = "Clean Code") =
        BookRecordEntity(randomUUID(), title, "9780123456789")

}
