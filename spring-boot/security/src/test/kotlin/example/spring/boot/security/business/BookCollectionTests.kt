package example.spring.boot.security.business

import example.spring.boot.security.business.Examples.record_refactoring
import example.spring.boot.security.persistence.BookRepository
import example.spring.boot.security.security.Authorities.ROLE_CURATOR
import example.spring.boot.security.security.Authorities.ROLE_USER
import example.spring.boot.security.security.MethodSecurityConfiguration
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.util.IdGenerator

internal class BookCollectionTests {

    val bookRecord = record_refactoring
    val uuid = bookRecord.id
    val book = bookRecord.book

    @Nested
    inner class FunctionalTests {

        // Functionality is tested by mocking the BookCollection's dependencies.

        val repository: BookRepository = mockk()
        val idGenerator: IdGenerator = mockk()
        val cut = BookCollection(repository, idGenerator)

        @BeforeEach
        fun resetMocks() {
            clearAllMocks()
        }

        @Nested
        inner class AddBook {

            @Test
            fun `creates a BookRecord and saves it in the repository`() {
                every { idGenerator.generateId() } returns uuid
                every { repository.save(any()) } answers { firstArg() }

                assertThat(cut.addBook(book)).isEqualTo(bookRecord)
                verify { repository.save(bookRecord) }
            }

        }

        @Nested
        inner class GetBookById {

            @Test
            fun `returns null if book was not found in the repository`() {
                every { repository.findById(uuid) } returns null
                assertThat(cut.getBookById(uuid)).isNull()
            }

            @Test
            fun `returns a BookRecord if one was found in the repositroy`() {
                every { repository.findById(uuid) } returns bookRecord
                assertThat(cut.getBookById(uuid)).isEqualTo(bookRecord)
            }

        }

        @Nested
        inner class DeleteBookById {

            @ValueSource(booleans = [true, false])
            @ParameterizedTest(name = "deleted = {0}")
            fun `delegates deletion to the repository`(mockedResult: Boolean) {
                every { repository.deleteById(uuid) } returns mockedResult
                assertThat(cut.deleteBookById(uuid)).isEqualTo(mockedResult)
            }

        }

    }

    @Nested
    @SpringBootTest(classes = [SecurityTestsConfiguration::class])
    inner class SecurityTests(
        @Autowired val cut: BookCollection
    ) {

        // The configured security rules are tested by mocking the BookCollection itself inside of a very
        // small and limited Spring Boot context (only the BookCollection + Spring Security and our own
        // configuration).

        @TestFactory
        @WithMockUser(authorities = [ROLE_USER])
        fun `users with the USER role`() = listOf(
            dynamicTest("cannot add books") { assertAccessDenied { cut.addBook(book) } },
            dynamicTest("can get books by ID") { assertAccessGranted { cut.getBookById(uuid) } },
            dynamicTest("cannot delete books by ID") { assertAccessDenied { cut.deleteBookById(uuid) } }
        )

        @TestFactory
        @WithMockUser(authorities = [ROLE_CURATOR])
        fun `users with the CURATOR role`() = listOf(
            dynamicTest("can add books") { assertAccessGranted { cut.addBook(book) } },
            dynamicTest("can get books by ID") { assertAccessGranted { cut.getBookById(uuid) } },
            dynamicTest("can delete books by ID") { assertAccessGranted { cut.deleteBookById(uuid) } }
        )

        @TestFactory
        fun `without a user context`() = listOf(
            dynamicTest("books cannot be added") { assertNotAuthenticated { cut.addBook(book) } },
            dynamicTest("books cannot be got by ID") { assertNotAuthenticated { cut.getBookById(uuid) } },
            dynamicTest("books cannot be deleted by ID") { assertNotAuthenticated { cut.deleteBookById(uuid) } }
        )

        fun assertAccessGranted(block: () -> Unit) = assertDoesNotThrow(block)
        fun assertAccessDenied(block: () -> Unit) = assertThrows<AccessDeniedException>(block)
        fun assertNotAuthenticated(block: () -> Unit) = assertThrows<AuthenticationCredentialsNotFoundException>(block)
    }

    // Import our own MethodSecurityConfiguration to have the same configuration as the complete
    // application would have.

    @Import(MethodSecurityConfiguration::class)
    class SecurityTestsConfiguration {

        // Even on a mock of the BookCollection, Spring Security annotations will still work.

        @Bean
        fun bookCollection(): BookCollection = mockk(relaxed = true)

    }

}
