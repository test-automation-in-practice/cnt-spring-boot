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
import web.business.Foo
import web.business.FooNotFoundException
import web.business.FooService
import web.business.PersistedFoo
import java.time.OffsetDateTime
import java.util.*

@WebMvcTest(FooController::class)
@ExtendWith(SpringExtension::class)
internal class FooControllerTest {

    companion object {
        val id = UUID.randomUUID()
        val foo = Foo(
                bar = "Hello World!",
                xur = OffsetDateTime.parse("2018-07-10T12:34:56.789Z")
        )
        val persistedFoo = PersistedFoo(id, foo)

        val anotherId = UUID.randomUUID()
        val anotherFoo = Foo(
                bar = "Hello Universe!",
                xur = OffsetDateTime.parse("2017-06-09T12:34:56.789Z")
        )
        val anotherPersistedFoo = PersistedFoo(anotherId, anotherFoo)
    }

    @SpyBean lateinit var resourceAssembler: FooResourceAssembler
    @MockBean lateinit var service: FooService
    @Autowired lateinit var mockMvc: MockMvc

    /** There is a bug in Spring when using JUnit's @Nested feature. */
    @BeforeEach fun resetMocks(): Unit = Mockito.reset(service)

    @DisplayName("POST /api/foos")
    @Nested inner class Post {

        @Test fun `posting a foo persists it and returns resource representation`() {
            given(service.create(foo)).willReturn(persistedFoo)

            val expectedResponse = """
                {
                    "bar": "Hello World!",
                    "xur": "2018-07-10T12:34:56.789Z",
                    "_links": {
                        "self": {"href":"http://localhost/api/foos/$id"}
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
        }

        @Test fun `posting a foo with a malformed 'bar' property responds with status 400`() {
            mockMvc.perform(post("/api/foos")
                    .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .content("""
                        {
                            "bar": "Good Bye!",
                            "xur": "2018-07-10T12:34:56.789Z"
                        }
                        """))
                    .andExpect(status().isBadRequest)
        }

        @Test fun `posting a foo with missing 'bar' property responds with status 400`() {
            mockMvc.perform(post("/api/foos")
                    .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .content("""
                        {
                            "xur": "2018-07-10T12:34:56.789Z"
                        }
                        """))
                    .andExpect(status().isBadRequest)
        }

        @Test fun `posting a foo with missing 'xur' property responds with status 400`() {
            mockMvc.perform(post("/api/foos")
                    .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .content("""
                        {
                            "bar": "Hello World!"
                        }
                        """))
                    .andExpect(status().isBadRequest)
        }

    }

    @DisplayName("GET /api/foos")
    @Nested inner class Get {

        @Test fun `getting all foos returns their resource representation as a resource list`() {
            given(service.getAll()).willReturn(listOf(persistedFoo, anotherPersistedFoo))

            val expectedResponse = """
                {
                    "_embedded": {
                        "foos": [
                            {
                                "bar": "Hello World!",
                                "xur": "2018-07-10T12:34:56.789Z",
                                "_links": {
                                    "self": {"href":"http://localhost/api/foos/$id"}
                                }
                            },
                            {
                                "bar": "Hello Universe!",
                                "xur": "2017-06-09T12:34:56.789Z",
                                "_links": {
                                    "self": {"href":"http://localhost/api/foos/$anotherId"}
                                }
                            }
                        ]
                    },
                    "_links": {
                        "self": {
                            "href": "http://localhost/api/foos"
                        }
                    }
                }
                """

            mockMvc.perform(get("/api/foos"))
                    .andExpect(status().isOk)
                    .andExpect(content().contentType(HAL_JSON_UTF8))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `getting all foos when there are none returns empty resource list`() {
            given(service.getAll()).willReturn(emptyList())

            val expectedResponse = """
                {
                    "_links": {
                        "self": {
                            "href": "http://localhost/api/foos"
                        }
                    }
                }
                """

            mockMvc.perform(get("/api/foos"))
                    .andExpect(status().isOk)
                    .andExpect(content().contentType(HAL_JSON_UTF8))
                    .andExpect(content().json(expectedResponse, true))
        }

    }

    @DisplayName("GET /api/foos/{id}")
    @Nested inner class GetById {

        @Test fun `getting a foo by its id returns resource representation`() {
            given(service.get(id)).willReturn(persistedFoo)

            val expectedResponse = """
                {
                    "bar": "Hello World!",
                    "xur": "2018-07-10T12:34:56.789Z",
                    "_links": {
                        "self": {"href":"http://localhost/api/foos/$id"}
                    }
                }
                """

            mockMvc.perform(get("/api/foos/$id"))
                    .andExpect(status().isOk)
                    .andExpect(content().contentType(HAL_JSON_UTF8))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `getting an unknown foo by its id responds with status 404`() {
            given(service.get(id)).willThrow(FooNotFoundException(id))

            mockMvc.perform(get("/api/foos/$id"))
                    .andExpect(status().isNotFound)
                    .andExpect(content().string(""))
        }

    }

    @DisplayName("DELETE /api/foos/{id}")
    @Nested inner class DeleteById {

        @Test fun `deleting a foo by its id returns status 204`() {
            mockMvc.perform(delete("/api/foos/$id"))
                    .andExpect(status().isNoContent)
                    .andExpect(content().string(""))
        }

        @Test fun `deleting an unknown foo by its id responds with status 404`() {
            given(service.delete(id)).willThrow(FooNotFoundException(id))

            mockMvc.perform(delete("/api/foos/$id"))
                    .andExpect(status().isNotFound)
                    .andExpect(content().string(""))
        }

    }

}