package advanced.e2e

import advanced.e2e.security.TEST_TOKEN_1
import advanced.e2e.security.TestTokenIntrospector
import com.github.tomakehurst.wiremock.WireMockServer
import io.restassured.RestAssured
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@TestInstance(PER_CLASS)
@Import(TestTokenIntrospector::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWireMock(port = 0, stubs = ["classpath:/e2e/wiremock"])
internal class ApplicationTests {

    @BeforeEach
    fun setup(@LocalServerPort port: Int) {
        RestAssured.port = port
    }

    @AfterEach
    fun assertWireMockRequests(@Autowired wireMock: WireMockServer) {
        assertThat(wireMock.findAllUnmatchedRequests()).isEmpty()
    }

    @Test
    fun `adding a book by its ISBN works`() {
        RestAssured.given()
            .header(AUTHORIZATION, "Bearer $TEST_TOKEN_1")
            .`when`()
            .post("/api/books/9781680680584")
            .then()
            .statusCode(201)
    }

}
