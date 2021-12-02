package webmvc.api.hateoas

import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.hateoas.MediaTypes.HAL_JSON
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.result.ContentResultMatchersDsl
import webmvc.business.BookCollection
import webmvc.business.BookRecordNotFoundException
import webmvc.business.Examples.book_cleanCode
import webmvc.business.Examples.id_cleanArchitecture
import webmvc.business.Examples.id_cleanCode
import webmvc.business.Examples.record_cleanArchitecture
import webmvc.business.Examples.record_cleanCode

private class HateoasBookControllerTestConfiguration {
    @Bean
    fun rookRepresentationAssembler() = BookRepresentationAssembler()

    @Bean
    fun bookCollection(): BookCollection = mockk()
}

@WebMvcTest(HateoasBookController::class)
@Import(HateoasBookControllerTestConfiguration::class)
@AutoConfigureRestDocs("build/generated-snippets/hateoas/books")
internal class HateoasBookControllerTest(
    @Autowired val bookCollection: BookCollection,
    @Autowired val mockMvc: MockMvc
) {

    @BeforeEach
    fun resetMocks() = clearAllMocks()

    @Nested
    @DisplayName("POST /hateoas-api/books")
    inner class Post {

        @Test
        fun `posting a book adds it to the library and returns resource representation`() {
            every { bookCollection.add(book_cleanCode) } returns record_cleanCode

            mockMvc.post("/hateoas-api/books") {
                contentType = APPLICATION_JSON
                content = """{ "title": "Clean Code", "isbn": "9780132350884" }"""
            }.andExpect {
                status { isCreated() }
                content {
                    contentType(HAL_JSON)
                    strictJson {
                        """
                        {
                            "title": "Clean Code",
                            "isbn": "9780132350884",
                            "_links": {
                                "self": {"href":"http://localhost:8080/hateoas-api/books/$id_cleanCode"}
                            }
                        }
                        """
                    }
                }
            }.andDo { document("post/created") }
        }

        @Test
        fun `posting a book with a invalid property responds with status 400`() {
            mockMvc.post("/hateoas-api/books") {
                contentType = APPLICATION_JSON
                content = """{ "title": "Clean Code", "isbn": "0132350884" }""" // isbn malformed
            }.andExpect {
                status { isBadRequest() }
            }.andDo { document("post/bad-request_invalid") }
        }

        @Test
        fun `posting a book with missing property responds with status 400`() {
            mockMvc.post("/hateoas-api/books") {
                contentType = APPLICATION_JSON
                content = """{ "isbn": "Clean Code" }"""
            }.andExpect {
                status { isBadRequest() }
            }.andDo { document("post/bad-request_missing") }
        }

    }

    @Nested
    @DisplayName("GET /hateoas-api/books")
    inner class Get {

        @Test
        fun `getting all books returns their resource representation as a resource list`() {
            every { bookCollection.getAll() } returns listOf(record_cleanCode, record_cleanArchitecture)

            mockMvc.get("/hateoas-api/books")
                .andExpect {
                    status { isOk() }
                    content {
                        contentType(HAL_JSON)
                        strictJson {
                            """
                            {
                                "_embedded": {
                                    "books": [
                                        {
                                            "title": "Clean Code",
                                            "isbn": "9780132350884",
                                            "_links": {
                                                "self": {"href":"http://localhost:8080/hateoas-api/books/$id_cleanCode"}
                                            }
                                        },
                                        {
                                            "title": "Clean Architecture",
                                            "isbn": "9780134494166",
                                            "_links": {
                                                "self": {"href":"http://localhost:8080/hateoas-api/books/$id_cleanArchitecture"}
                                            }
                                        }
                                    ]
                                },
                                "_links": {
                                    "self": {
                                        "href": "http://localhost:8080/hateoas-api/books"
                                    }
                                }
                            }
                            """
                        }
                    }
                }
                .andDo { document("get/ok_found") }
        }

        @Test
        fun `getting all books when there are none returns empty resource list`() {
            every { bookCollection.getAll() } returns emptyList()

            mockMvc.get("/hateoas-api/books")
                .andExpect {
                    status { isOk() }
                    content {
                        contentType(HAL_JSON)
                        strictJson {
                            """
                            {
                                "_links": {
                                    "self": {
                                        "href": "http://localhost:8080/hateoas-api/books"
                                    }
                                }
                            }
                            """
                        }
                    }
                }
                .andDo { document("get/ok_empty") }
        }

    }

    @Nested
    @DisplayName("GET /hateoas-api/books/{id}")
    inner class GetById {

        @Test
        fun `getting a book by its id returns resource representation`() {
            every { bookCollection.get(id_cleanCode) } returns record_cleanCode

            mockMvc.get("/hateoas-api/books/$id_cleanCode")
                .andExpect {
                    status { isOk() }
                    content {
                        contentType(HAL_JSON)
                        strictJson {
                            """
                            {
                                "title": "Clean Code",
                                "isbn": "9780132350884",
                                "_links": {
                                    "self": {"href":"http://localhost:8080/hateoas-api/books/$id_cleanCode"}
                                }
                            }
                            """
                        }
                    }
                }
                .andDo { document("by-id/get/ok") }
        }

        @Test
        fun `getting an unknown book by its id responds with status 404`() {
            every { bookCollection.get(id_cleanCode) } throws BookRecordNotFoundException(id_cleanCode)

            mockMvc.get("/hateoas-api/books/$id_cleanCode")
                .andExpect {
                    status { isNotFound() }
                    content { string("") }
                }
                .andDo { document("by-id/get/not-found") }
        }

    }

    @Nested
    @DisplayName("DELETE /hateoas-api/books/{id}")
    inner class DeleteById {

        @Test
        fun `deleting a book by its id returns status 204`() {
            every { bookCollection.delete(id_cleanCode) } just runs

            mockMvc.delete("/hateoas-api/books/$id_cleanCode")
                .andExpect {
                    status { isNoContent() }
                    content { string("") }
                }
                .andDo { document("by-id/delete/no-content") }
        }

    }

    fun ContentResultMatchersDsl.strictJson(supplier: () -> String) =
        json(jsonContent = supplier(), strict = true)

    fun MockMvcResultHandlersDsl.document(identifier: String) =
        handle(document(identifier, preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))

}
