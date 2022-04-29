package advanced.e2e.api

import advanced.e2e.domain.BookCollection
import advanced.e2e.domain.BookDataNotFoundException
import advanced.e2e.domain.Examples.isbn_bobiverse1
import advanced.e2e.domain.Examples.isbn_bobiverse2
import advanced.e2e.domain.Examples.isbn_bobiverse3
import advanced.e2e.domain.Examples.record_bobiverse1
import advanced.e2e.security.TEST_TOKEN_1
import advanced.e2e.security.TestTokenIntrospector
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

@WebMvcTest(BookCollectionController::class)
@MockkBean(BookCollection::class)
@Import(TestTokenIntrospector::class)
internal class BookCollectionControllerTests(
    @Autowired val collection: BookCollection,
    @Autowired val mockMvc: MockMvc
) {

    @Test
    fun `posting an ISBN returns created record if book was found`() {
        every { collection.addBookByIsbn(isbn_bobiverse1) } returns success(record_bobiverse1)

        mockMvc.post("/api/books/$isbn_bobiverse1") {
            headers { setBearerAuth(TEST_TOKEN_1) }
        }.andExpect {
            status { isCreated() }
            content {
                contentType(APPLICATION_JSON)
                json(
                    """
                    { 
                      "id": "b3fc0be8-463e-4875-9629-67921a1e00f4",
                      "book": {
                        "isbn": "9781680680584",
                        "title": "We Are Legion (We Are Bob)"
                      }
                    }
                    """
                )
            }
        }
    }

    @Test
    fun `posting an ISBN returns not found if book was not found`() {
        every { collection.addBookByIsbn(isbn_bobiverse2) } returns failure(BookDataNotFoundException(isbn_bobiverse2))

        mockMvc.post("/api/books/$isbn_bobiverse2") {
            headers { setBearerAuth(TEST_TOKEN_1) }
        }.andExpect {
            status { isNotFound() }
            content { string("") }
        }
    }

    @Test
    fun `authentication header is required`() {
        mockMvc.post("/api/books/$isbn_bobiverse3")
            .andExpect { status { isUnauthorized() } }
    }

}
