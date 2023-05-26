package example.spring.boot.security

import com.ninjasquad.springmockk.MockkBean
import example.spring.boot.security.persistence.BookRepository
import example.spring.boot.security.utils.InitializeWithContainerizedKeycloak
import example.spring.boot.security.utils.getCuratorToken
import example.spring.boot.security.utils.getUserToken
import io.mockk.every
import io.restassured.RestAssured
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.UNAUTHORIZED
import java.util.UUID.randomUUID

@MockkBean(BookRepository::class)
@InitializeWithContainerizedKeycloak
@SpringBootTest(webEnvironment = RANDOM_PORT)
internal class ApplicationSecurityTests {

    @BeforeEach
    fun setupRestAssured(@LocalServerPort port: Int) {
        RestAssured.port = port
    }

    @BeforeEach
    fun stubRepository(@Autowired repository: BookRepository) {
        every { repository.save(any()) } answers { firstArg() }
        every { repository.findById(any()) } returns null
        every { repository.deleteById(any()) } returns false
    }

    @Nested
    inner class BookApiEndpointSecurity {

        // This would be the end-2-end testing alternative to the WebSecurityConfigurationTests.

        @Test
        fun `endpoints are not accessible without credentials`() {
            assertThatOAuthUserReturnsStatus("/api/books/${randomUUID()}", UNAUTHORIZED)
        }

        @Test
        fun `endpoints are accessible with tokens and role 'user'`() {
            assertThatOAuthUserReturnsStatus("/api/books/${randomUUID()}", NO_CONTENT, getUserToken())
        }

        @Test
        fun `endpoints are accessible with tokens and role 'curator'`() {
            assertThatOAuthUserReturnsStatus("/api/books/${randomUUID()}", NO_CONTENT, getCuratorToken())
        }

        @Test
        fun `endpoints are not accessible with basic-auth`() {
            assertThatBasicAuthUserReturnsStatus("/api/books/${randomUUID()}", UNAUTHORIZED, "user")
        }

    }

    @Nested
    inner class ActuatorSecurity {

        val publicEndpoints = setOf("/actuator/info", "/actuator/health")
        val privateEndpoints = setOf("/actuator/beans", "/actuator/env", "/actuator/metrics")

        @TestFactory
        fun `without credentials only public endpoints are available`() =
            dynamicTests(publicEndpoints to OK, privateEndpoints to UNAUTHORIZED) { endpoint, status ->
                assertThatBasicAuthUserReturnsStatus(endpoint, status)
            }

        @TestFactory
        fun `with credentials of user with ACTUATOR scope all endpoints are available`() =
            dynamicTests(publicEndpoints to OK, privateEndpoints to OK) { endpoint, status ->
                assertThatBasicAuthUserReturnsStatus(endpoint, status, "actuator")
            }

        @TestFactory
        fun `with credentials of user without ACTUATOR scope only public endpoints are available`() =
            dynamicTests(publicEndpoints to OK, privateEndpoints to FORBIDDEN) { endpoint, status ->
                assertThatBasicAuthUserReturnsStatus(endpoint, status, "user")
            }

        private fun dynamicTests(
            vararg expectations: Pair<Set<String>, HttpStatus>,
            block: (String, HttpStatus) -> Unit
        ) = mapOf(*expectations)
            .flatMap { (endpoints, status) -> endpoints.map { it to status } }
            .map { (endpoint, status) -> dynamicTest("$endpoint >> $status") { block(endpoint, status) } }

    }

    private fun assertThatBasicAuthUserReturnsStatus(path: String, status: HttpStatus, username: String? = null) =
        Given {
            if (username != null) auth().preemptive().basic(username, username.reversed()) else this
        } When { get(path) } Then { statusCode(status.value()) }

    private fun assertThatOAuthUserReturnsStatus(path: String, status: HttpStatus, token: String? = null) =
        Given {
            apply { if (token != null) auth().preemptive().oauth2(token) }
            apply { println(token) }
        } When { get(path) } Then { statusCode(status.value()) }

}
