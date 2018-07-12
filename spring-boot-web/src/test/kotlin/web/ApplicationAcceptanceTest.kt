package web

import io.restassured.RestAssured
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.startsWith
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
internal class ApplicationAcceptanceTest {

    var port: Int = 0

    @LocalServerPort
    fun setupRestAssured(port: Int) {
        this.port = port
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
    }

    @Test fun `creating a foo responds with resource representation including self link`() {
        RestAssured.given()
                .header("Content-Type", "application/json")
                .body("""
                    {
                      "bar": "Hello RESTAssured!",
                      "xur": "2018-07-12T12:34:56.789Z"
                    }
                    """)

                .`when`().post("/api/foos")

                .then()
                .statusCode(201)
                .contentType("application/hal+json;charset=UTF-8")
                .content("bar", equalTo("Hello RESTAssured!"))
                .content("xur", equalTo("2018-07-12T12:34:56.789Z"))
                .content("_links.self.href", startsWith("http://localhost:$port/api/foos/"))
    }

}