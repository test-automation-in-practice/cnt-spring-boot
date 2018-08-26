package web.api

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.hateoas.MediaTypes.HAL_JSON_UTF8
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.snippet.Snippet
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import web.business.*
import java.util.*

@WebMvcTest(BooksController::class)
@ExtendWith(SpringExtension::class)
@AutoConfigureRestDocs("build/generated-snippets/books")
internal class BooksControllerDocTest {

    companion object {
        val id = UUID.randomUUID()
        val book = Book(
                title = Title("Clean Code"),
                isbn = Isbn("9780132350884")
        )
        val bookRecord = BookRecord(id, book)
    }

    @SpyBean lateinit var resourceAssembler: BookResourceAssembler
    @MockBean lateinit var library: Library
    @Autowired lateinit var mockMvc: MockMvc

    @Test fun `posting a book adds it to the library and returns resource representation`() {
        given(library.add(book)).willReturn(bookRecord)

        val expectedResponse = """
            {
                "title": "Clean Code",
                "isbn": "9780132350884",
                "_links": {
                    "self": {"href":"http://localhost:8080/api/books/$id"}
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
                .andDo(document("postBook-added"))
    }

    private fun document(identifier: String, vararg snippets: Snippet): RestDocumentationResultHandler {
        return document(identifier, preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()), *snippets)
    }

}