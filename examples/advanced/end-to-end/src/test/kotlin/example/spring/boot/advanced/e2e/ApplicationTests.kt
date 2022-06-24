package example.spring.boot.advanced.e2e

import com.github.tomakehurst.wiremock.WireMockServer
import example.spring.boot.advanced.e2e.security.TEST_TOKEN_1
import example.spring.boot.advanced.e2e.security.TestTokenIntrospector
import io.restassured.RestAssured
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
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
        Given {
            header(AUTHORIZATION, "Bearer $TEST_TOKEN_1")
        } When {
            post("/api/books/9781680680584")
        } Then {
            statusCode(201)
        }
    }

}
