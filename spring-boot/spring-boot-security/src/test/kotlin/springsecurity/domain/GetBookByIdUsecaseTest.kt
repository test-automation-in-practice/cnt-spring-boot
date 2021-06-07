package springsecurity.domain

import io.mockk.called
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.test.context.support.WithMockUser
import springsecurity.IntegrationTest
import springsecurity.UnitTest
import springsecurity.security.MethodSecurityConfiguration
import springsecurity.security.ROLE_ADMIN
import springsecurity.security.ROLE_CURATOR
import springsecurity.security.ROLE_USER
import java.util.UUID

internal class GetBookByIdUsecaseTest {

    val uuid = UUID.fromString("cd690768-74d4-48a8-8443-664975dd46b5")
    val book = Book(
        isbn = "978-0134757599",
        title = "Refactoring: Improving the Design of Existing Code"
    )
    val bookRecord = BookRecord(uuid, book)

    @Nested
    @UnitTest
    inner class FunctionalTests {

        val repository: BookRepository = mockk()
        val cut = GetBookByIdUsecase(repository)

        @Test
        fun `invocation simply delegates to the repository - not found case`() {
            every { repository.findById(uuid) } returns null
            assertThat(cut.invoke(uuid)).isNull()
        }

        @Test
        fun `invocation simply delegates to the repository - found case`() {
            every { repository.findById(uuid) } returns bookRecord
            assertThat(cut.invoke(uuid)).isEqualTo(bookRecord)
        }

    }

    @Nested
    @IntegrationTest
    @SpringBootTest(classes = [GetBookByIdUsecaseTestConfiguration::class])
    inner class SecurityTests(
        @Autowired val repository: BookRepository,
        @Autowired val cut: GetBookByIdUsecase
    ) {

        @AfterEach
        fun resetMocks() = clearAllMocks(answers = false)

        @Test
        fun `usecase cannot be invoked without authenticated user`() {
            assertThrows<AuthenticationCredentialsNotFoundException> { cut.invoke(uuid) }
        }

        @Test
        @WithMockUser(authorities = [ROLE_USER])
        fun `usecase can be invoked by user with role USER`() {
            cut.invoke(uuid)
            verify { repository.findById(any()) }
        }

        @Test
        @WithMockUser(authorities = [ROLE_USER, ROLE_CURATOR])
        fun `usecase can be invoked by user with roles USER and CURATOR`() {
            cut.invoke(uuid)
            verify { repository.findById(any()) }
        }

        @Test
        @WithMockUser(authorities = [ROLE_CURATOR])
        fun `usecase cannot be invoked by user with just role CURATOR`() {
            assertThrows<AccessDeniedException> { cut.invoke(uuid) }
            verify { repository wasNot called }
        }

        @Test
        @WithMockUser(authorities = [ROLE_ADMIN])
        fun `usecase cannot be invoked by user with just role ADMIN`() {
            assertThrows<AccessDeniedException> { cut.invoke(uuid) }
            verify { repository wasNot called }
        }

    }

    @Import(MethodSecurityConfiguration::class)
    class GetBookByIdUsecaseTestConfiguration {

        @Bean
        fun bookRepository(): BookRepository = mockk { every { findById(any()) } returns null }

        @Bean
        fun getBookByIdUsecase(bookRepository: BookRepository) = GetBookByIdUsecase(bookRepository)

    }

}
