package example.spring.boot.data.mongodb.persistence

import example.spring.boot.data.mongodb.utils.InitializeWithContainerizedMongoDB
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.test.context.ActiveProfiles
import java.util.UUID.randomUUID

internal class BookRecordRepositoryTest {

    /**
     * Much faster boostrap after first download, but pollutes local file system.
     */
    @Nested
    @DataMongoTest
    @ActiveProfiles("test", "embedded")
    inner class WithEmbeddedDatabase(
        @Autowired override val cut: BookRecordRepository
    ) : BookRecordRepositoryContract()

    /**
     * Takes longer to boostrap, but also provides real MongoDB behaviour.
     */
    @Nested
    @DataMongoTest
    @ActiveProfiles("test", "docker")
    @InitializeWithContainerizedMongoDB
    inner class WithDockerizedDatabase(
        @Autowired override val cut: BookRecordRepository
    ) : BookRecordRepositoryContract()

    abstract class BookRecordRepositoryContract {

        protected abstract val cut: BookRecordRepository

        @Test
        fun `document can be saved`() {
            val document = bookRecordDocument()
            val savedDocument = cut.save(document)
            assertThat(savedDocument).isEqualTo(document.copy(version = 1))
        }

        @Test
        fun `document version is increased with every save`() {
            val document = bookRecordDocument()
            val savedDocument1 = cut.save(document)
            val savedDocument2 = cut.save(savedDocument1)
            val savedDocument3 = cut.save(savedDocument2)

            assertThat(savedDocument1.version).isEqualTo(1)
            assertThat(savedDocument2.version).isEqualTo(2)
            assertThat(savedDocument3.version).isEqualTo(3)
        }

        @Test
        fun `document can not be saved in lower than current version`() {
            val document = bookRecordDocument()
                .let(cut::save)
                .let(cut::save)
            val documentWithLowerVersion = document.copy(version = document.version - 1)

            assertThatThrownBy { cut.save(documentWithLowerVersion) }
                .isInstanceOf(OptimisticLockingFailureException::class.java)
        }

        @Test
        fun `document can be found by id`() {
            val savedDocument = cut.save(bookRecordDocument())
            val foundDocument = cut.findById(savedDocument.id)
            assertThat(foundDocument).hasValue(savedDocument)
        }

        @Test
        fun `document can be found by title`() {
            val d1 = cut.save(bookRecordDocument("Clean Code"))
            val d2 = cut.save(bookRecordDocument("Clean Architecture"))
            val d3 = cut.save(bookRecordDocument("Clean Code"))

            val foundEntities = cut.findByTitle("Clean Code")

            assertThat(foundEntities)
                .contains(d1, d3)
                .doesNotContain(d2)
        }

        private fun bookRecordDocument(title: String = "Clean Code") =
            BookRecordDocument(randomUUID(), title, "9780123456789")

    }

}
