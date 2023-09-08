package example.spring.boot.webflux

import io.github.logrecorder.api.LogRecord
import io.github.logrecorder.assertion.LogRecordAssertion.Companion.assertThat
import io.github.logrecorder.assertion.containsExactly
import io.github.logrecorder.logback.junit5.RecordLoggers
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient
import org.zalando.logbook.Logbook

@SpringBootTest(webEnvironment = RANDOM_PORT)
internal class ApplicationAcceptanceTest {

    lateinit var client: WebTestClient

    @LocalServerPort
    fun setupRestAssured(port: Int) {
        client = WebTestClient.bindToServer()
            .baseUrl("http://localhost:$port")
            .build()
    }

    @Test
    fun `creating a book responds with resource representation including self link`() {
        client.post()
            .uri("/default-api/books")
            .contentType(APPLICATION_JSON)
            .bodyValue("""{ "title": "Clean Code", "isbn": "9780132350884" }""")
            .exchange()
            .expectStatus().isCreated
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("id").exists()
            .jsonPath("title").isEqualTo("Clean Code")
            .jsonPath("isbn").isEqualTo("9780132350884")
    }

    @Test
    @RecordLoggers(Logbook::class)
    fun `request and response are logged with obfuscated authorization header`(log: LogRecord) {
        client.post()
            .uri("/default-api/books")
            .header(AUTHORIZATION, "some-token")
            .contentType(APPLICATION_JSON)
            .bodyValue("""{ "title": "Clean Code", "isbn": "9780132350884" }""")
            .exchange()
        assertThat(log) containsExactly {
            trace(startsWith("Incoming Request:"), contains("authorization: XXX"))
            trace(startsWith("Outgoing Response:"))
        }
    }

}
