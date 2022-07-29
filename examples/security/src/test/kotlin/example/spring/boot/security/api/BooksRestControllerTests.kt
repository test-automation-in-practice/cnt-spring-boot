package example.spring.boot.security.api

import com.ninjasquad.springmockk.MockkBean
import example.spring.boot.security.business.BookCollection
import example.spring.boot.security.business.Examples.book_refactoring
import example.spring.boot.security.business.Examples.id_refactoring
import example.spring.boot.security.business.Examples.record_refactoring
import example.spring.boot.security.security.Authorities.SCOPE_BOOKS
import example.spring.boot.security.security.WebSecurityConfiguration
import io.mockk.clearAllMocks
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

/**
 * [WebMvcTest] builds a context that includes web-related as well as security components,
 * but only the defaults. In order to use our own security configuration we have ti import it.
 *
 * Since in our configuration the /api/books namespace requires the [SCOPE_BOOKS] authorization,
 * we'll be using [WithMockUser] to set a pre-authenticated user with that scope to be used by all
 * tests.
 *
 * Since security rules are already tested by other test classes, and we want to focus on functionality,
 * these are the only measures taken to make the test run from a security standpoint.
 * Alternatively we would need to exclude the security autoconfiguration or provide a dummy security configuration
 * that simply allows all interactions.
 */
@MockkBean(BookCollection::class)
@WebMvcTest(BooksRestController::class)
@WithMockUser(authorities = [SCOPE_BOOKS])
@Import(WebSecurityConfiguration::class)
internal class BooksRestControllerTest(
    @Autowired val bookCollection: BookCollection,
    @Autowired val mockMvc: MockMvc
) {

    @BeforeEach
    fun resetMocks() {
        clearAllMocks()
    }

    @Test
    fun `adding a new book responds with a 201 Created`() {
        every { bookCollection.addBook(book_refactoring) } returns record_refactoring

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
        every { bookCollection.getBookById(id_refactoring) } returns null

        mockMvc
            .get("/api/books/$id_refactoring")
            .andExpect {
                status { isNoContent() }
                content { string("") }
            }
    }

    @Test
    fun `getting an existing book by ID responds with a 200 Ok`() {
        every { bookCollection.getBookById(id_refactoring) } returns record_refactoring

        mockMvc
            .get("/api/books/$id_refactoring")
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
        every { bookCollection.deleteBookById(id_refactoring) } returns deleted

        mockMvc
            .delete("/api/books/$id_refactoring")
            .andExpect {
                status { isNoContent() }
                content { string("") }
            }
    }

}
