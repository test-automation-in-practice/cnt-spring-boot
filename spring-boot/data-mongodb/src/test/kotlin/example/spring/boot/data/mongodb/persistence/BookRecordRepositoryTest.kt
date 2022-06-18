package example.spring.boot.data.mongodb.persistence

import example.spring.boot.data.mongodb.utils.RunWithDockerizedMongoDB
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ActiveProfiles
import java.util.UUID.randomUUID

internal class BookRecordRepositoryTest {

    /**
     * Much faster boostrap after first download, but pollutes local file system.
     */
    @Nested
    @DataMongoTest
    @ActiveProfiles("test", "embedded")
    inner class WithH2InMemoryDatabase(
        @Autowired override val cut: BookRecordRepository
    ) : BookRecordRepositoryContract()

    /**
     * Takes longer to boostrap, but also provides real MongoDB behaviour.
     */
    @Nested
    @RunWithDockerizedMongoDB
    @DataMongoTest(
        excludeAutoConfiguration = [
            EmbeddedMongoAutoConfiguration::class // only needed to have both options in one codebase
        ]
    )
    @ActiveProfiles("test", "docker")
    inner class WithDockerizedDatabase(
        @Autowired override val cut: BookRecordRepository
    ) : BookRecordRepositoryContract()

    abstract class BookRecordRepositoryContract {

        protected abstract val cut: BookRecordRepository

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
            BookRecordDocument(randomUUID(), title, "9780123456789")

    }

}
