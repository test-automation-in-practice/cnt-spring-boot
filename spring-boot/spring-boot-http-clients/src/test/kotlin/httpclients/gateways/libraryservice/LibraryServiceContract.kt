package httpclients.gateways.libraryservice

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import java.io.IOException

/**
 * When testing HTTP interaction, in this case calling another service's API, the tests should be written as
 * block-box tests. The tests only need to verify that correct requests are sent and responses are processed
 * correctly. It does not matter which client technology is actually used to make those calls, as long as
 * all relevant cases are handled as they should be.
 *
 * Using Wiremock is essential here, because it allows us to simulate the other service in every aspect without
 * having to mock our actually used client technology.
 *
 * Trying to write these tests as unit tests using mocking to simulate the underlying client technology only
 * makes them harder to write, understand and maintain in case we want to change which client we use.
 */
internal interface LibraryServiceContract {

    val wireMock: WireMock
    val cut: LibraryService

    @ParameterizedTest
    @ValueSource(ints = [200, 201])
    fun `sends correct request and parses successful responses`(status: Int) {
        stub {
            post("/api/books")
                .withHeader(CONTENT_TYPE, containing(APPLICATION_JSON_VALUE))
                .withRequestBody(
                    equalToJson(
                        """
                        {
                          "title": "Clean Code",
                          "isbn": "9780132350884"
                        }
                        """
                    )
                )
                .willReturn(
                    aResponse()
                        .withStatus(status)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withBody(
                            """
                            {
                              "id": "be64192f-879c-4346-8aff-76582117a42d",
                              "title": "Clean Code",
                              "isbn": "9780132350884"
                            }
                            """
                        )
                )
        }

        val book = Book(
            title = "Clean Code",
            isbn = "9780132350884"
        )
        val expected = CreatedBook(
            id = "be64192f-879c-4346-8aff-76582117a42d",
            title = "Clean Code",
            isbn = "9780132350884"
        )
        assertThat(cut.addBook(book)).isEqualTo(expected)
    }

    @ParameterizedTest
    @ValueSource(ints = [204, 400, 401, 403, 404, 500, 502, 504])
    fun `throws exception for failed responses`(status: Int) {
        stub { post("/api/books").willReturn(aResponse().withStatus(status).withBody("")) }

        val book = Book(
            title = "Clean Architecture",
            isbn = "9780134494166"
        )
        assertThrows<IOException> { cut.addBook(book) }
    }

    private fun stub(supplier: () -> MappingBuilder) {
        wireMock.register(supplier())
    }

}
