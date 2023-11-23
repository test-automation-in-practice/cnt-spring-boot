package example.spring.boot.http.clients.gateways.libraryservice

import ch.qos.logback.classic.Level.convertAnSLF4JLevel
import ch.qos.logback.classic.Logger
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.containing
import com.github.tomakehurst.wiremock.client.WireMock.equalToJson
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.slf4j.Logger.ROOT_LOGGER_NAME
import org.slf4j.LoggerFactory.getLogger
import org.slf4j.event.Level
import org.slf4j.event.Level.INFO
import org.slf4j.event.Level.TRACE
import org.springframework.http.HttpHeaders.ACCEPT
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.MediaType.TEXT_PLAIN_VALUE

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
internal abstract class LibraryServiceContract(
    private val wireMockInfo: WireMockRuntimeInfo
) {

    private val cut: LibraryService by lazy {
        createClassUnderTest(LibraryServiceProperties(baseUrl = wireMockInfo.httpBaseUrl))
    }

    protected abstract fun createClassUnderTest(properties: LibraryServiceProperties): LibraryService

    @BeforeEach
    fun setLogLevels() {
        setLogLevel(ROOT_LOGGER_NAME, INFO)
        setLogLevel("org.zalando.logbook", TRACE)
    }

    private fun setLogLevel(loggerName: String, level: Level) {
        (getLogger(loggerName) as Logger).level = convertAnSLF4JLevel(level)
    }

    @ParameterizedTest
    @ValueSource(ints = [200, 201])
    fun `sends correct request and parses successful responses`(status: Int) {
        stub {
            post("/api/books")
                .withHeader(ACCEPT, containing(APPLICATION_JSON_VALUE))
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
                              "isbn": "9780132350884",
                              "unknown": true
                            }
                            """
                        ) // includes unknown property to make sure, that our clients are parsing leniently
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
    @ValueSource(ints = [400, 401, 403, 404, 500, 502, 504])
    fun `throws exception for failed responses`(status: Int) {
        stub { post("/api/books").willReturn(aResponse().withStatus(status)) }

        val book = Book(
            title = "Clean Architecture",
            isbn = "9780134494166"
        )
        assertThrows<LibraryServiceException> { cut.addBook(book) }
    }

    @Test
    fun `throws exception for no-content response`() {
        stub {
            post("/api/books").willReturn(
                aResponse().withHeader(CONTENT_TYPE, TEXT_PLAIN_VALUE).withStatus(204)
            )
        }

        val book = Book(
            title = "Clean Architecture",
            isbn = "9780134494166"
        )
        assertThrows<LibraryServiceException> { cut.addBook(book) }
    }

    private fun stub(supplier: () -> MappingBuilder) {
        wireMockInfo.wireMock.register(supplier())
    }

}
