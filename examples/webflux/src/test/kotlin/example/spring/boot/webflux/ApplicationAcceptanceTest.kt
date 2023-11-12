package example.spring.boot.webflux

import io.github.logrecorder.api.LogRecord
import io.github.logrecorder.assertion.LogRecordAssertion.Companion.assertThat
import io.github.logrecorder.assertion.containsExactly
import io.github.logrecorder.junit5.RecordLoggers
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient
import org.zalando.logbook.Logbook

@SpringBootTest(webEnvironment = RANDOM_PORT)
internal class ApplicationAcceptanceTest(
    @Autowired val webTestClient: WebTestClient
) {

    @Test
    fun `creating a book responds with resource representation including self link`() {
        webTestClient.post()
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
        webTestClient.post()
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
