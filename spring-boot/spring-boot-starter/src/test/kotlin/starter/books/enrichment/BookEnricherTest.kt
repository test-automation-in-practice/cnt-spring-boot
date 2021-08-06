package starter.books.enrichment

import com.ninjasquad.springmockk.MockkBean
import io.mockk.called
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.scheduling.annotation.EnableAsync
import starter.Examples.additional_cleanCode
import starter.Examples.book_cleanCode
import starter.Examples.id_cleanCode
import starter.Examples.record_cleanCode
import starter.Examples.record_cleanCode_enriched
import starter.books.core.BookRecordCreatedEvent
import starter.books.core.BookRecordDeletedEvent
import starter.books.core.BookRepository
import java.lang.Thread.sleep

// This test class actually contains a couple of different kinds of test classes.
// It is mainly a container to run all relevant tests for the BooksEnricher component.

internal class BookEnricherTest {

    // Test data used by both the functional unit and the technology integration tests.

    val createdEvent = BookRecordCreatedEvent(record_cleanCode)
    val deletedEvent = BookRecordDeletedEvent(id_cleanCode)

    @Nested
    inner class FunctionalTest {

        // Unit tests that verify the functional aspects of the BookEnricher

        val bookInformationSource: BookInformationSource = mockk()
        val bookRepository: BookRepository = mockk()
        val cut = BookEnricher(bookInformationSource, bookRepository)

        @BeforeEach
        fun resetMocks() {
            clearAllMocks()
            every { bookRepository.save(any()) } answers { firstArg() }
        }

        @Test
        fun `if additional data was found the book is updated`() {
            every {
                bookInformationSource.getBookInformation(book_cleanCode.isbn)
            } returns additional_cleanCode

            cut.handle(createdEvent)

            verify { bookRepository.save(record_cleanCode_enriched) }
        }

        @Test
        fun `if no additional data was found the book not updated`() {
            every {
                bookInformationSource.getBookInformation(book_cleanCode.isbn)
            } returns null

            cut.handle(createdEvent)

            verify { bookRepository wasNot called }
        }

    }

    @Nested
    @SpringBootTest(classes = [TechnologyIntegrationTestConfigurationVariant1::class])
    inner class TechnologyIntegrationTestVariant1(
        @Autowired private val eventPublisher: ApplicationEventPublisher,
        @Autowired private val cut: BookEnricher
    ) {

        // Technology integration tests that verify that Spring's event listener mechanism is used correctly.

        // But due to the @Async annotation leading to the use of proxies for the class under test, this
        // only works if we do not enable async behaviour for this test.
        // Otherwise the @Autowired cut: BookEnricher is not the actual mock, but a proxy - making the
        // verify { .. } calls impossible.

        @Test
        fun `book record created events will trigger enrichment`() {
            eventPublisher.publishEvent(createdEvent)
            verify { cut.handle(createdEvent) }
        }

        @Test
        fun `book record deleted events will not trigger anything`() {
            eventPublisher.publishEvent(deletedEvent)
            verify { cut wasNot called }
        }

    }

    private class TechnologyIntegrationTestConfigurationVariant1 {

        @Bean
        fun booksEnricher(): BookEnricher = mockk(relaxUnitFun = true)

    }

    @Nested
    @SpringBootTest(classes = [TechnologyIntegrationTestConfigurationVariant2::class])
    inner class TechnologyIntegrationTestVariant2(
        @Autowired private val eventPublisher: ApplicationEventPublisher,
        @Autowired private val bookInformationSource: BookInformationSource
    ) {

        // Technology integration tests that verify that Spring's event listener mechanism is used correctly.

        // But due to the @Async annotation leading to the use of proxies for the class under test, this
        // implementation variant does not mock the class under test, but instead it's dependencies.

        // At this point, if one absolutely has to test the async behaviour, it might be simpler to merge
        // this test with the functional unit test because their setup is pretty similar.

        @Test
        fun `book record created events will trigger enrichment`() {
            eventPublisher.publishEvent(createdEvent)
            verify(timeout = 1_000) { bookInformationSource.getBookInformation(any()) }
        }

        @Test
        fun `book record deleted events will not trigger anything`() {
            eventPublisher.publishEvent(deletedEvent)
            sleep(1_000)
            verify { bookInformationSource wasNot called }
        }

    }

    @EnableAsync
    @Import(BookEnricher::class)
    private class TechnologyIntegrationTestConfigurationVariant2 {

        @Bean
        fun additionalBookDataRepository(): BookInformationSource = mockk {
            every { getBookInformation(any()) } returns null
        }

        @Bean
        fun bookRepository(): BookRepository = mockk()
    }

    @Nested
    @SpringBootTest(classes = [TechnologyIntegrationTestConfigurationVariant3::class])
    inner class TechnologyIntegrationTestVariant3(
        @Autowired private val eventPublisher: ApplicationEventPublisher
    ) {

        // Technology integration tests that verify that Spring's event listener mechanism is used correctly.

        // Uses https://github.com/Ninja-Squad/springmockk in order to NOT run into the previously mentioned problems.

        @MockkBean(relaxUnitFun = true)
        lateinit var cut: BookEnricher

        @Test
        fun `book record created events will trigger enrichment`() {
            eventPublisher.publishEvent(createdEvent)
            verify(timeout = 1_000) { cut.handle(createdEvent) }
        }

        @Test
        fun `book record deleted events will not trigger anything`() {
            eventPublisher.publishEvent(deletedEvent)
            sleep(1_000)
            verify { cut wasNot called }
        }

    }

    @EnableAsync
    private class TechnologyIntegrationTestConfigurationVariant3

}
