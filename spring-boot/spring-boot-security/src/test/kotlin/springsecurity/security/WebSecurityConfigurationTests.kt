package springsecurity.security

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.OK
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import springsecurity.security.Authorities.SCOPE_ACTUATOR
import springsecurity.security.Authorities.SCOPE_BOOKS
import springsecurity.security.WebSecurityConfigurationTests.SecurityTestController

// Actuator endpoint security cannot be testet / simulated here, because how it is configured in the
// WebSecurityConfiguration. Because everything about actuator endpoints can be changed by configuring
// the management properties, using the EndpointRequest matchers does not simply map to "/actuator/**".

// Testing actual Actuator endpoints in this kind of test is also not possible. Actuator does not actually
// use WebMVC and can therefore not be tested by a @WebMvcTest.

// It might actually be better to use an end-2-end smoke test for testing security rules.
// Doing so would keep all web-security tests in one place.

@TestInstance(PER_CLASS)
@WebMvcTest(SecurityTestController::class)
@Import(SecurityTestController::class)
internal class WebSecurityConfigurationTests(
    @Autowired private val mockMvc: MockMvc
) {

    // This RestController will not picked up by regular component scans and needs to be imported explicitly
    // to be used as part of these tests.

    @RestController
    class SecurityTestController {

        @GetMapping("/api/books", "/api/books/foo", "/api/books/bar")
        fun getBooks(@AuthenticationPrincipal user: Any) = user

    }

    @TestFactory
    @WithMockUser(authorities = [SCOPE_BOOKS])
    fun `users with just the BOOKS scope can access any books endpoints`() = allBookPathsReturnStatus(OK)

    @TestFactory
    @WithMockUser(authorities = [SCOPE_ACTUATOR])
    fun `users with just the ACTUATOR scope cannot access any books endpoints`() = allBookPathsReturnStatus(FORBIDDEN)

    @TestFactory
    @WithMockUser(authorities = [SCOPE_BOOKS, SCOPE_ACTUATOR])
    fun `users with the BOOKS and ACTUATOR scopes cam access any books endpoints`() = allBookPathsReturnStatus(OK)

    private fun allBookPathsReturnStatus(status: HttpStatus): List<DynamicTest> =
        listOf("/api/books", "/api/books/foo", "/api/books/bar")
            .map { path ->
                dynamicTest(path) {
                    mockMvc.get(path)
                        .andExpect {
                            status { isEqualTo(status.value()) }
                        }
                }
            }

}
