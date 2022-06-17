package example.spring.boot.webmvc.api.default

import com.ninjasquad.springmockk.MockkBean
import example.spring.boot.webmvc.business.BookCollection
import example.spring.boot.webmvc.business.BookRecordNotFoundException
import example.spring.boot.webmvc.business.Examples.book_cleanCode
import example.spring.boot.webmvc.business.Examples.id_cleanArchitecture
import example.spring.boot.webmvc.business.Examples.id_cleanCode
import example.spring.boot.webmvc.business.Examples.record_cleanArchitecture
import example.spring.boot.webmvc.business.Examples.record_cleanCode
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.test.web.servlet.MockHttpServletRequestDsl
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MockMvcResultHandlersDsl
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.ContentResultMatchersDsl

@MockkBean(BookCollection::class)
@WebMvcTest(DefaultBooksController::class)
@AutoConfigureRestDocs("build/generated-snippets/default/books")
internal class DefaultBookControllerTest(
    @Autowired val bookCollection: BookCollection,
    @Autowired val mockMvc: MockMvc
) {

    @BeforeEach
    fun resetMocks() = clearAllMocks()

    @Nested
    @DisplayName("POST /default-api/books")
    inner class Post {

        @Test
        fun `posting a book adds it to the library and returns resource representation`() {
            every { bookCollection.add(book_cleanCode) } returns record_cleanCode

            mockMvc.post("/default-api/books") {
                jsonContent("""{ "title": "Clean Code", "isbn": "9780132350884" }""")
            }.andExpect {
                status { isCreated() }
                content {
                    contentType(APPLICATION_JSON)
                    strictJson(
                        """
                        {
                            "id": "$id_cleanCode",
                            "title": "Clean Code",
                            "isbn": "9780132350884"
                        }
                        """
                    )
                }
            }.andDo { document("post/created") }
        }

        @Test
        fun `posting a book with a invalid property responds with status 400`() {
            mockMvc.post("/default-api/books") {
                jsonContent("""{ "title": "Clean Code", "isbn": "0132350884" }""") // isbn malformed
            }.andExpect {
                status { isBadRequest() }
            }.andDo { document("post/bad-request_invalid") }
        }

        @Test
        fun `posting a book with missing property responds with status 400`() {
            mockMvc.post("/default-api/books") {
                jsonContent("""{ "isbn": "Clean Code" }""")
            }.andExpect {
                status { isBadRequest() }
            }.andDo { document("post/bad-request_missing") }
        }

    }

    @Nested
    @DisplayName("GET /default-api/books")
    inner class Get {

        @Test
        fun `getting all books returns their resource representation as a resource list`() {
            every { bookCollection.getAll() } returns listOf(record_cleanCode, record_cleanArchitecture)

            mockMvc.get("/default-api/books")
                .andExpect {
                    status { isOk() }
                    content {
                        contentType(APPLICATION_JSON)
                        strictJson(
                            """
                            [
                                {
                                    "id": "$id_cleanCode",
                                    "title": "Clean Code",
                                    "isbn": "9780132350884"
                                },
                                {
                                    "id": "$id_cleanArchitecture",
                                    "title": "Clean Architecture",
                                    "isbn": "9780134494166"
                                }
                            ]
                            """
                        )
                    }
                }
                .andDo { document("get/ok_found") }
        }

        @Test
        fun `getting all books when there are none returns empty resource list`() {
            every { bookCollection.getAll() } returns emptyList()

            mockMvc.get("/default-api/books")
                .andExpect {
                    status { isOk() }
                    content {
                        contentType(APPLICATION_JSON)
                        strictJson("[]")
                    }
                }
                .andDo { document("get/ok_empty") }
        }

    }

    @Nested
    @DisplayName("GET /default-api/books/{id}")
    inner class GetById {

        @Test
        fun `getting a book by its id returns resource representation`() {
            every { bookCollection.get(id_cleanCode) } returns record_cleanCode

            mockMvc.get("/default-api/books/$id_cleanCode")
                .andExpect {
                    status { isOk() }
                    content {
                        contentType(APPLICATION_JSON)
                        strictJson(
                            """
                            {
                                "id": "$id_cleanCode",
                                "title": "Clean Code",
                                "isbn": "9780132350884"
                            }
                            """
                        )
                    }
                }
                .andDo { document("by-id/get/ok") }
        }

        @Test
        fun `getting an unknown book by its id responds with status 404`() {
            every { bookCollection.get(id_cleanCode) } throws BookRecordNotFoundException(id_cleanCode)

            mockMvc.get("/default-api/books/$id_cleanCode")
                .andExpect {
                    status { isNotFound() }
                    content { string("") }
                }
                .andDo { document("by-id/get/not-found") }
        }

    }

    @Nested
    @DisplayName("DELETE /default-api/books/{id}")
    inner class DeleteById {

        @Test
        fun `deleting a book by its id returns status 204`() {
            every { bookCollection.delete(id_cleanCode) } just runs

            mockMvc.delete("/default-api/books/$id_cleanCode")
                .andExpect {
                    status { isNoContent() }
                    content { string("") }
                }
                .andDo { document("by-id/delete/no-content") }
        }

    }

    private fun MockHttpServletRequestDsl.jsonContent(@Language("json") json: String) {
        contentType = APPLICATION_JSON
        content = json
    }

    fun ContentResultMatchersDsl.strictJson(@Language("json") json: String) =
        json(jsonContent = json, strict = true)

    fun MockMvcResultHandlersDsl.document(identifier: String) =
        handle(document(identifier, preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))

}
