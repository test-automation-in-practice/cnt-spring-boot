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
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.snippet.Snippet
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import web.business.Foo
import web.business.FooService
import web.business.PersistedFoo
import java.time.OffsetDateTime
import java.util.*

@WebMvcTest(FooController::class)
@ExtendWith(SpringExtension::class)
@AutoConfigureRestDocs("build/generated-snippets/foos")
internal class FooControllerDocTest {

    companion object {
        val id = UUID.randomUUID()
        val foo = Foo(
                bar = "Hello World!",
                xur = OffsetDateTime.parse("2018-07-10T12:34:56.789Z")
        )
        val persistedFoo = PersistedFoo(id, foo)
    }

    @SpyBean lateinit var resourceAssembler: FooResourceAssembler
    @MockBean lateinit var service: FooService
    @Autowired lateinit var mockMvc: MockMvc

    @Test fun `posting a foo persists it and returns resource representation`() {
        given(service.create(foo)).willReturn(persistedFoo)

        val expectedResponse = """
            {
                "bar": "Hello World!",
                "xur": "2018-07-10T12:34:56.789Z",
                "_links": {
                    "self": {"href":"http://localhost:8080/api/foos/$id"}
                }
            }
            """

        mockMvc.perform(post("/api/foos")
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .content("""
                    {
                        "bar": "Hello World!",
                        "xur": "2018-07-10T12:34:56.789Z"
                    }
                    """))
                .andExpect(status().isCreated)
                .andExpect(content().contentType(HAL_JSON_UTF8))
                .andExpect(content().json(expectedResponse, true))
                .andDo(document("postFoo-created"))
    }

    private fun document(identifier: String, vararg snippets: Snippet): RestDocumentationResultHandler {
        return document(identifier, preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()), *snippets)
    }

}