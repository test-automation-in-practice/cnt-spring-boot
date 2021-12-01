package web

import io.restassured.RestAssured
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.startsWith
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.web.server.LocalServerPort

@SpringBootTest(webEnvironment = RANDOM_PORT)
internal class ApplicationAcceptanceTest {

    var port: Int = 0

    @LocalServerPort
    fun setupRestAssured(port: Int) {
        this.port = port
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
    }

    @Test
    fun `creating a book responds with resource representation including self link`() {
        RestAssured.given()
            .header("Content-Type", "application/json")
            .body("""{ "title": "Clean Code", "isbn": "9780132350884" }""")
            .`when`()
            .post("/hateoas-api/books")
            .then()
            .statusCode(201)
            .contentType("application/hal+json")
            .body("title", equalTo("Clean Code"))
            .body("isbn", equalTo("9780132350884"))
            .body("_links.self.href", startsWith("http://localhost:$port/hateoas-api/books/"))
    }

}
