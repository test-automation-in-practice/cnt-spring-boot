package springsecurity.api

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import springsecurity.domain.BookCollection
import springsecurity.domain.BookRecordExamples
import springsecurity.security.Authorities.SCOPE_BOOKS

// @WebMvcTests automatically include Web-related components. This also includes any WebSecurityConfigurerAdapter!
// This means, by default, our own security configuration is found and active during these kinds of tests.

// Since Spring-Security acts before WebMvc it is not possible to use Authentication headers in MockMvc-based tests.
// @WithMockUser is used on the class-level in order to let all tests access the API.

// The general HTTP security configuration is tests in another test. This class is focused solely on the
// RestController's functionality.

@WebMvcTest(BooksRestController::class)
@WithMockUser(authorities = [SCOPE_BOOKS])
@Import(BooksRestControllerTestConfiguration::class)
internal class BooksRestControllerTest(
    @Autowired val bookCollection: BookCollection,
    @Autowired val mockMvc: MockMvc
) {

    val bookRecord = BookRecordExamples.REFACTORING
    val id = bookRecord.id
    val book = bookRecord.book

    @BeforeEach
    fun resetMocks() {
        clearAllMocks()
    }

    @Test
    fun `adding a new book responds with a 201 Created`() {
        every { bookCollection.addBook(book) } returns bookRecord

        mockMvc
            .post("/api/books") {
                contentType = APPLICATION_JSON
                content = """
                    {
                      "isbn": "978-0134757599",
                      "title": "Refactoring: Improving the Design of Existing Code"
                    }
                    """
            }
            .andExpect {
                status { isCreated() }
                content {
                    contentTypeCompatibleWith(APPLICATION_JSON)
                    json(
                        jsonContent = """
                            {
                              "id": "cd690768-74d4-48a8-8443-664975dd46b5",
                              "isbn": "978-0134757599",
                              "title": "Refactoring: Improving the Design of Existing Code"
                            }
                            """,
                        strict = true
                    )
                }
            }
    }

    @Test
    fun `getting a non-existing book by ID responds with a 204 No Content`() {
        every { bookCollection.getBookById(id) } returns null

        mockMvc
            .get("/api/books/$id")
            .andExpect {
                status { isNoContent() }
                content { string("") }
            }
    }

    @Test
    fun `getting an existing book by ID responds with a 200 Ok`() {
        every { bookCollection.getBookById(id) } returns bookRecord

        mockMvc
            .get("/api/books/$id")
            .andExpect {
                status { isOk() }
                content {
                    contentTypeCompatibleWith(APPLICATION_JSON)
                    json(
                        jsonContent = """
                            {
                              "id": "cd690768-74d4-48a8-8443-664975dd46b5",
                              "isbn": "978-0134757599",
                              "title": "Refactoring: Improving the Design of Existing Code"
                            }
                            """,
                        strict = true
                    )
                }
            }
    }

    @ValueSource(booleans = [true, false])
    @ParameterizedTest(name = "was deleted = {0}")
    fun `deleting book by ID responds with a 204 No Content`(deleted: Boolean) {
        every { bookCollection.deleteBookById(id) } returns deleted

        mockMvc
            .delete("/api/books/$id")
            .andExpect {
                status { isNoContent() }
                content { string("") }
            }
    }

}

private class BooksRestControllerTestConfiguration {

    @Bean
    fun bookCollection(): BookCollection = mockk()

}
