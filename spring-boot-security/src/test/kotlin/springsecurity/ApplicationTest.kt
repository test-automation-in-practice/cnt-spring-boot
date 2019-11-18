package springsecurity

import io.restassured.RestAssured
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@End2EndTest
@SpringBootTest(webEnvironment = RANDOM_PORT)
class ApplicationTest {

    @LocalServerPort
    fun setPort(port: Int) {
        RestAssured.port = port
    }

    @Nested
    inner class SecuritySmokeTests {

        @Nested
        inner class AddBook {

            @Test
            fun `authentication must be provided`() {
                addBookAs(null, 401)
            }

            @Test
            fun `authentication's user must exist`() {
                addBookAs("unknown", 401)
            }

            @ParameterizedTest
            @ValueSource(strings = ["user", "curator", "admin"])
            fun `existing users's requests will pass through request security filters`(user: String) {
                addBookAs(user, 400)
            }

            fun addBookAs(user: String?, expectedStatus: Int) {
                RestAssured.given()
                    .also { if (user != null) it.auth().basic(user, user.reversed()) }
                    .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .body("{}")
                    .`when`()
                    .post("/api/books")
                    .then()
                    .statusCode(expectedStatus)
            }

        }

    }

}
