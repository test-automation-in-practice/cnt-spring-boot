package example.spring.boot.webmvc

import io.github.logrecorder.api.LogRecord
import io.github.logrecorder.assertion.LogRecordAssertion.Companion.assertThat
import io.github.logrecorder.assertion.containsExactly
import io.github.logrecorder.logback.junit5.RecordLoggers
import io.restassured.RestAssured
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.startsWith
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE
import org.zalando.logbook.Logbook

@SpringBootTest(webEnvironment = RANDOM_PORT)
internal class ApplicationSmokeTest {

    var port: Int = 0

    @LocalServerPort
    fun setupRestAssured(port: Int) {
        this.port = port
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
    }

    @Test
    fun `creating a book responds with resource representation including self link`() {
        Given {
            header("Content-Type", "application/json")
            body("""{ "title": "Clean Code", "isbn": "9780132350884" }""")
        } When {
            post("/hateoas-api/books")
        } Then {
            statusCode(201)
            contentType("application/hal+json")
            body("title", equalTo("Clean Code"))
            body("isbn", equalTo("9780132350884"))
            body("_links.self.href", startsWith("http://localhost:$port/hateoas-api/books/"))
        }
    }

    @Test
    @RecordLoggers(Logbook::class)
    fun `request and response are logged with obfuscated authorization header`(log: LogRecord) {
        Given {
            header("Content-Type", "application/json")
            header("Authorization", "some-token")
            body("""{ "title": "Clean Architecture", "isbn": "9780134494166" }""")
        } When {
            post("/default-api/books")
        } Then {
            statusCode(201)
        }
        assertThat(log) containsExactly {
            trace(startsWith("Incoming Request:"), contains("authorization: XXX"))
            trace(startsWith("Outgoing Response:"))
        }
    }

    @Test
    fun `errors are returned as RFC 7807 problem details with traceId`() {
        Given {
            header("Content-Type", "application/json")
            header("X-Trace-ID", "d18ade213f84")
            body("{}") // empty body -> Bad Request
        } When {
            post("/default-api/books")
        } Then {
            statusCode(400)
            contentType(APPLICATION_PROBLEM_JSON_VALUE)
            body("title", equalTo("Bad Request"))
            body("status", equalTo(400))
            body("detail", equalTo("Failed to read request"))
            body("instance", equalTo("/default-api/books"))
            body("traceId", equalTo("d18ade213f84"))
        }
    }

}
