package springsecurity.api

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import springsecurity.ApplicationTest
import springsecurity.IntegrationTest
import springsecurity.domain.AddBookUsecase
import springsecurity.domain.BookRecord
import java.util.UUID

/**
 * Since Spring-Security acts before WebMvc it is not possible
 * to use Authentication headers in MockMvc-based tests.
 *
 * Either use `@WithMockUser` on the class or method level or
 * one of the `SecurityMockMvcRequestPostProcessors` methods.
 *
 * In order to test the actual spring security configuration
 * a test should be written which talks to the system und test
 * via _real HTTP_ over a _real port_. This could be something
 * like a "Security Acceptance Test" with RESTAssured.
 *
 * @see ApplicationTest.SecuritySmokeTests
 */
@WithMockUser
@IntegrationTest
@WebMvcTest(AddBookRestController::class)
@Import(AddBookRestControllerTestConfiguration::class)
internal class AddBookRestControllerTest(
    @Autowired private val mockMvc: MockMvc
) {

    @Test
    fun `posting a new book creates it and returns the new record`() {
        val request = post("/api/books")
            .contentType(APPLICATION_JSON)
            .content(
                """
                {
                  "isbn": "978-0134757599",
                  "title": "Refactoring: Improving the Design of Existing Code"
                }
                """
            )

        mockMvc.perform(request)
            .andExpect(status().isCreated())
            .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
            .andExpect(
                content().json(
                    """
                    {
                      "id": "cd690768-74d4-48a8-8443-664975dd46b5",
                      "book": {
                        "isbn": "978-0134757599",
                        "title": "Refactoring: Improving the Design of Existing Code"
                      }
                    }
                    """, true
                )
            )
    }

}

private class AddBookRestControllerTestConfiguration {

    private val uuid = UUID.fromString("cd690768-74d4-48a8-8443-664975dd46b5")

    @Bean
    fun addBookUsecase(): AddBookUsecase {
        val usecase: AddBookUsecase = mockk()
        every { usecase.invoke(any()) } answers { BookRecord(uuid, firstArg()) }
        return usecase
    }

}
