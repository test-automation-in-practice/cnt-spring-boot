package example.spring.boot.data.redis.persistence

import example.spring.boot.data.redis.utils.RunWithDockerizedRedis
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import org.springframework.test.context.ActiveProfiles
import java.util.UUID.randomUUID

@RunWithDockerizedRedis
@DataRedisTest
@ActiveProfiles("test")
internal class BookRecordRepositoryTest(
    @Autowired val cut: BookRecordRepository
) {

    @Test
    fun `document can be saved`() {
        val entity = bookRecordDocument()
        val savedEntity = cut.save(entity)
        assertThat(savedEntity).isEqualTo(entity)
    }

    @Test
    fun `document can be found by id`() {
        val savedEntity = cut.save(bookRecordDocument())
        val foundEntity = cut.findById(savedEntity.id)
        assertThat(foundEntity).hasValue(savedEntity)
    }

    @Test
    fun `document can be found by title`() {
        val e1 = cut.save(bookRecordDocument("Clean Code"))
        val e2 = cut.save(bookRecordDocument("Clean Architecture"))
        val e3 = cut.save(bookRecordDocument("Clean Code"))

        val foundEntities = cut.findByTitle("Clean Code")

        assertThat(foundEntities)
            .contains(e1, e3)
            .doesNotContain(e2)
    }

    private fun bookRecordDocument(title: String = "Clean Code") =
        BookRecord(randomUUID(), title, "9780123456789")

}
