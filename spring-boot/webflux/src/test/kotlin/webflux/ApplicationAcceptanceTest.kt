package webflux

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient

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

}
