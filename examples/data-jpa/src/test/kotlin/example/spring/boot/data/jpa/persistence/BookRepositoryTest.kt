package example.spring.boot.data.jpa.persistence

import example.spring.boot.data.jpa.model.Book
import example.spring.boot.data.jpa.model.Isbn
import example.spring.boot.data.jpa.model.Title
import example.spring.boot.data.jpa.utils.InitializeWithContainerizedPostgreSQL
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.test.context.ActiveProfiles
import java.util.UUID.randomUUID
import kotlin.random.Random.Default.nextInt

internal class BookRepositoryTest {

    /**
     * Much faster boostrap, but only simulates PostgreSQL behaviour.
     */
    @Nested
    @DataJpaTest
    @ActiveProfiles("test", "in-memory")
    inner class WithH2InMemoryDatabase(
        @Autowired override val cut: BookRecordRepository
    ) : BookRecordRepositoryContract()

    /**
     * Takes longer to boostrap, but also provides real PostgreSQL behaviour.
     */
    @Nested
    @DataJpaTest
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
        // Spring Data JPA only increments version / updates the entity if there are actual changes
        fun `entity version is increased with every change`() {
            val entity = bookRecordEntity()
            val savedEntity1 = cut.save(entity)
            val savedEntity2 = cut.save(savedEntity1.changeTitle())
            val savedEntity3 = cut.save(savedEntity2)
            val savedEntity4 = cut.save(savedEntity3.changeTitle())

            assertThat(savedEntity1.version).isEqualTo(0)
            assertThat(savedEntity2.version).isEqualTo(1)
            assertThat(savedEntity3.version).isEqualTo(1)
            assertThat(savedEntity4.version).isEqualTo(2)
        }

        @Test
        fun `entity can not be saved in lower than current version`() {
            val entity = bookRecordEntity()
            val savedEntity1 = cut.save(entity)
            cut.save(savedEntity1.changeTitle())

            assertThatThrownBy { cut.save(savedEntity1) }
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

            val foundEntities = cut.findByTitle(Title("Clean Code"))

            assertThat(foundEntities)
                .contains(e1, e3)
                .doesNotContain(e2)
        }

        private fun bookRecordEntity(title: String = "Clean Code") =
            BookEntity(randomUUID(), Book(Isbn("9780123456789"), Title(title)))

        private fun BookEntity.changeTitle(): BookEntity =
            apply { book = book.copy(title = Title("Change Title #${nextInt(1_000)}")) }
    }

}
