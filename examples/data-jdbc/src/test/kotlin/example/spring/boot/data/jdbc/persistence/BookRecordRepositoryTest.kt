package example.spring.boot.data.jdbc.persistence

import example.spring.boot.data.jdbc.utils.InitializeWithContainerizedPostgreSQL
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.test.context.ActiveProfiles
import java.util.UUID.randomUUID

internal class BookRecordRepositoryTest {

    /**
     * Much faster boostrap, but only simulates PostgreSQL behaviour.
     */
    @Nested
    @DataJdbcTest
    @ActiveProfiles("test", "in-memory")
    inner class WithH2InMemoryDatabase(
        @Autowired override val cut: BookRecordRepository
    ) : BookRecordRepositoryContract()

    /**
     * Takes longer to boostrap, but also provides real PostgreSQL behaviour.
     */
    @Nested
    @DataJdbcTest
    @ActiveProfiles("test", "docker")
    @InitializeWithContainerizedPostgreSQL
    inner class WithDockerizedDatabase(
        @Autowired override val cut: BookRecordRepository
    ) : BookRecordRepositoryContract()

    abstract class BookRecordRepositoryContract {

        protected abstract val cut: BookRecordRepository

        @Test
        fun `entity can be saved`() {
            val entity = bookRecordEntity()
            val savedEntity = cut.save(entity)
            assertThat(savedEntity).isEqualTo(entity)
        }

        @Test
        fun `entity version is increased with every save`() {
            val entity = bookRecordEntity()

            assertThat(cut.save(entity).version).isEqualTo(1)
            assertThat(cut.save(entity).version).isEqualTo(2)
            assertThat(cut.save(entity).version).isEqualTo(3)

            assertThat(entity.version).isEqualTo(3)
        }

        @Test
        fun `entity can not be saved in lower than current version`() {
            val entity = bookRecordEntity()
                .also(cut::save)
                .also(cut::save)
            val entityWithLowerVersion = entity.copy(version = entity.version - 1)

            assertThatThrownBy { cut.save(entityWithLowerVersion) }
                .isInstanceOf(OptimisticLockingFailureException::class.java)
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

}
