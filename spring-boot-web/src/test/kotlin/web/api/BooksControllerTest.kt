package web.api

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.hateoas.MediaTypes.HAL_JSON_UTF8
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import web.business.*
import java.util.*

@WebMvcTest(BooksController::class)
@ExtendWith(SpringExtension::class)
internal class BooksControllerTest {

    companion object {
        val id = UUID.randomUUID()
        val book = Book(
                title = Title("Clean Code"),
                isbn = Isbn("9780132350884")
        )
        val bookRecord = BookRecord(id, book)

        val anotherId = UUID.randomUUID()
        val anotherBook = Book(
                title = Title("Clean Architecture"),
                isbn = Isbn("9780134494166")
        )
        val anotherBookRecord = BookRecord(anotherId, anotherBook)
    }

    @SpyBean lateinit var resourceAssembler: BookResourceAssembler
    @MockBean lateinit var library: Library
    @Autowired lateinit var mockMvc: MockMvc

    /** There is a bug in Spring when using JUnit's @Nested feature. */
    @BeforeEach fun resetMocks(): Unit = Mockito.reset(library)

    @DisplayName("POST /api/books")
    @Nested inner class Post {

        @Test fun `posting a book adds it to the library and returns resource representation`() {
            given(library.add(book)).willReturn(bookRecord)

            val expectedResponse = """
                {
                    "title": "Clean Code",
                    "isbn": "9780132350884",
                    "_links": {
                        "self": {"href":"http://localhost/api/books/$id"}
                    }
                }
                """

            mockMvc.perform(post("/api/books")
                    .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .content("""
                        {
                            "title": "Clean Code",
                            "isbn": "9780132350884"
                        }
                        """))
                    .andExpect(status().isCreated)
                    .andExpect(content().contentType(HAL_JSON_UTF8))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `posting a book with a malformed 'isbn' property responds with status 400`() {
            mockMvc.perform(post("/api/books")
                    .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .content("""
                        {
                            "title": "Clean Code",
                            "isbn": "0132350884"
                        }
                        """))
                    .andExpect(status().isBadRequest)
        }

        @Test fun `posting a book with missing 'title' property responds with status 400`() {
            mockMvc.perform(post("/api/books")
                    .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .content("""
                        {
                            "isbn": "Clean Code"
                        }
                        """))
                    .andExpect(status().isBadRequest)
        }

        @Test fun `posting a book with missing 'isbn' property responds with status 400`() {
            mockMvc.perform(post("/api/books")
                    .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .content("""
                        {
                            "title": "9780132350884"
                        }
                        """))
                    .andExpect(status().isBadRequest)
        }

    }

    @DisplayName("GET /api/books")
    @Nested inner class Get {

        @Test fun `getting all books returns their resource representation as a resource list`() {
            given(library.getAll()).willReturn(listOf(bookRecord, anotherBookRecord))

            val expectedResponse = """
                {
                    "_embedded": {
                        "books": [
                            {
                                "title": "Clean Code",
                                "isbn": "9780132350884",
                                "_links": {
                                    "self": {"href":"http://localhost/api/books/$id"}
                                }
                            },
                            {
                                "title": "Clean Architecture",
                                "isbn": "9780134494166",
                                "_links": {
                                    "self": {"href":"http://localhost/api/books/$anotherId"}
                                }
                            }
                        ]
                    },
                    "_links": {
                        "self": {
                            "href": "http://localhost/api/books"
                        }
                    }
                }
                """

            mockMvc.perform(get("/api/books"))
                    .andExpect(status().isOk)
                    .andExpect(content().contentType(HAL_JSON_UTF8))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `getting all books when there are none returns empty resource list`() {
            given(library.getAll()).willReturn(emptyList())

            val expectedResponse = """
                {
                    "_links": {
                        "self": {
                            "href": "http://localhost/api/books"
                        }
                    }
                }
                """

            mockMvc.perform(get("/api/books"))
                    .andExpect(status().isOk)
                    .andExpect(content().contentType(HAL_JSON_UTF8))
                    .andExpect(content().json(expectedResponse, true))
        }

    }

    @DisplayName("GET /api/books/{id}")
    @Nested inner class GetById {

        @Test fun `getting a book by its id returns resource representation`() {
            given(library.get(id)).willReturn(bookRecord)

            val expectedResponse = """
                {
                    "title": "Clean Code",
                    "isbn": "9780132350884",
                    "_links": {
                        "self": {"href":"http://localhost/api/books/$id"}
                    }
                }
                """

            mockMvc.perform(get("/api/books/$id"))
                    .andExpect(status().isOk)
                    .andExpect(content().contentType(HAL_JSON_UTF8))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `getting an unknown book by its id responds with status 404`() {
            given(library.get(id)).willThrow(BookRecordNotFoundException(id))

            mockMvc.perform(get("/api/books/$id"))
                    .andExpect(status().isNotFound)
                    .andExpect(content().string(""))
        }

    }

    @DisplayName("DELETE /api/books/{id}")
    @Nested inner class DeleteById {

        @Test fun `deleting a book by its id returns status 204`() {
            mockMvc.perform(delete("/api/books/$id"))
                    .andExpect(status().isNoContent)
                    .andExpect(content().string(""))
        }

        @Test fun `deleting an unknown book by its id responds with status 404`() {
            given(library.delete(id)).willThrow(BookRecordNotFoundException(id))

            mockMvc.perform(delete("/api/books/$id"))
                    .andExpect(status().isNotFound)
                    .andExpect(content().string(""))
        }

    }

}