package example.spring.boot.security

import com.ninjasquad.springmockk.MockkBean
import example.spring.boot.security.persistence.BookRepository
import io.mockk.every
import io.restassured.RestAssured
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
@SpringBootTest(webEnvironment = RANDOM_PORT)
internal class ApplicationSecurtiyTests {

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
        fun `book API endpoints are not accessible without credentials`() {
            assertThatUserOnPathReturnsStatus("/api/books/${randomUUID()}", UNAUTHORIZED)
        }

        @Test
        fun `user with just scope BOOKS can access book API endpoints`() {
            assertThatUserOnPathReturnsStatus("/api/books/${randomUUID()}", NO_CONTENT, "user")
        }

        @Test
        fun `user with just scope ACTUATOR cannot access book API endpoints`() {
            assertThatUserOnPathReturnsStatus("/api/books/${randomUUID()}", FORBIDDEN, "actuator")
        }

    }

    @Nested
    inner class ActuatorSecurity {

        val publicEndpoints = setOf("/actuator/info", "/actuator/health")
        val privateEndpoints = setOf("/actuator/beans", "/actuator/env", "/actuator/metrics")

        @TestFactory
        fun `without credentials only public endpoints are available`() =
            dynamicTests(publicEndpoints to OK, privateEndpoints to UNAUTHORIZED) { endpoint, status ->
                assertThatUserOnPathReturnsStatus(endpoint, status)
            }

        @TestFactory
        fun `with credentials of user with ACTUATOR scope all endpoints are available`() =
            dynamicTests(publicEndpoints to OK, privateEndpoints to OK) { endpoint, status ->
                assertThatUserOnPathReturnsStatus(endpoint, status, "actuator")
            }

        @TestFactory
        fun `with credentials of user without ACTUATOR scope only public endpoints are available`() =
            dynamicTests(publicEndpoints to OK, privateEndpoints to FORBIDDEN) { endpoint, status ->
                assertThatUserOnPathReturnsStatus(endpoint, status, "user")
            }

        private fun dynamicTests(
            vararg expectations: Pair<Set<String>, HttpStatus>,
            block: (String, HttpStatus) -> Unit
        ) = mapOf(*expectations)
            .flatMap { (endpoints, status) -> endpoints.map { it to status } }
            .map { (endpoint, status) -> dynamicTest("$endpoint >> $status") { block(endpoint, status) } }

    }

    private fun assertThatUserOnPathReturnsStatus(path: String, status: HttpStatus, username: String? = null) {
        RestAssured.given()
            .apply { if (username != null) auth().preemptive().basic(username, username.reversed()) }
            .`when`().get(path)
            .then().statusCode(status.value())
    }

}
