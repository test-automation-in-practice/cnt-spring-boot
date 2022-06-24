package example.spring.boot.basics.books.core

import example.spring.boot.basics.Examples.book_cleanCode
import example.spring.boot.basics.Examples.id_cleanArchitecture
import example.spring.boot.basics.Examples.id_cleanCode
import example.spring.boot.basics.Examples.record_cleanCode
import io.github.logrecorder.api.LogRecord
import io.github.logrecorder.assertion.LogRecordAssertion.Companion.assertThat
import io.github.logrecorder.assertion.containsExactly
import io.github.logrecorder.logback.junit5.RecordLoggers
import io.mockk.called
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.util.IdGenerator
import java.time.Instant
import java.util.UUID.randomUUID

// This is a unit-test for the BookCollection class.
// It uses MockK to simulate BookCollection's dependencies.

internal class BookCollectionTest {

    // Mocks used for simulate and record behaviour
    val idGenerator: IdGenerator = mockk()
    val repository: BookRepository = mockk()
    val eventPublisher: BookEventPublisher = mockk(relaxUnitFun = true)

    // The 'Class Under Test' (CUT)
    val cut = BookCollection(idGenerator, repository, eventPublisher)

    @BeforeEach
    fun resetMocks() {
        clearAllMocks()
    }

    @Nested
    @DisplayName("adding a book")
    inner class AddBook {

        // Testdata for all tests, but generated everytime the tests are executed.
        // Using dynamic test data like this reduces the number of tests one would normally write.

        val generatedId = randomUUID()
        val currentTimestamp = Instant.now()

        @BeforeEach
        fun stubDefaultBehaviour() {
            // ID generator returns fixed value
            every { idGenerator.generateId() } returns generatedId
            // Documented repository behaviour is simulated - in this case the update of the timestamp
            every { repository.save(any()) } answers { simulateRepositorySave(firstArg()) }
        }

        // This test asserts that given a specific input, the correct value is returned

        @Test
        fun `returns a book record`() {
            val actualBookRecord = cut.add(book_cleanCode)
            val expectedBookRecord = BookRecord(generatedId, book_cleanCode, currentTimestamp)
            assertThat(actualBookRecord).isEqualTo(expectedBookRecord)
        }

        // This test verifies that the book record was actually saved in the repository

        @Test
        fun `persists a book record it in the repository`() {
            cut.add(book_cleanCode)
            val unsavedBookRecord = BookRecord(generatedId, book_cleanCode)
            verify { repository.save(unsavedBookRecord) }
        }

        // This test verifies that an event was published as a side effect of creating a new book record

        @Test
        fun `publishes a creation event`() {
            cut.add(book_cleanCode)

            val savedBookRecord = BookRecord(generatedId, book_cleanCode, currentTimestamp)
            val expectedEvent = BookRecordCreatedEvent(savedBookRecord)
            verify { eventPublisher.publish(expectedEvent) }
        }

        fun simulateRepositorySave(bookRecord: BookRecord): BookRecord =
            bookRecord.copy(timestamp = currentTimestamp)

    }

    @Nested
    @DisplayName("getting book records by their ID")
    inner class GetById {

        // These tests verify that the delegates result is handed through without modification

        @Test
        fun `returns the record if it was found`() {
            every { repository.findById(id_cleanCode) } returns record_cleanCode
            assertThat(cut.get(id_cleanCode)).isEqualTo(record_cleanCode)
        }

        @Test
        fun `returns null if it was not found`() {
            every { repository.findById(id_cleanCode) } returns null
            assertThat(cut.get(id_cleanCode)).isNull()
        }

    }

    @Nested
    @DisplayName("deleting a book record by its ID")
    inner class DeleteById {

        // These tests makes sure that the side effect of publishing an event occurs only if the book record was
        // actually deleted.

        @Test
        fun `publishes a deletion event, if the record was actually deleted`() {
            every { repository.deleteById(id_cleanCode) } returns true
            cut.delete(id_cleanCode)
            verify { eventPublisher.publish(BookRecordDeletedEvent(id_cleanCode)) }
        }

        @Test
        fun `does not publish any event, if the record did was not actually deleted`() {
            every { repository.deleteById(id_cleanCode) } returns false
            cut.delete(id_cleanCode)
            verify { eventPublisher wasNot called }
        }

        @Test
        @RecordLoggers(BookCollection::class)
        fun `logs whether a book was actually deleted`(log: LogRecord) {
            every { repository.deleteById(id_cleanCode) } returns true
            every { repository.deleteById(id_cleanArchitecture) } returns false

            cut.delete(id_cleanCode)
            cut.delete(id_cleanArchitecture)

            assertThat(log) {
                containsExactly {
                    info("trying to delete book with ID '$id_cleanCode'")
                    debug("book with ID '$id_cleanCode' was deleted")
                    info("trying to delete book with ID '$id_cleanArchitecture'")
                    debug("book with ID '$id_cleanArchitecture' was not deleted")
                }
            }
        }

    }

}
