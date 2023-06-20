package example.spring.boot.security.security

import example.spring.boot.security.security.Authorities.SCOPE_ACTUATOR
import example.spring.boot.security.security.Authorities.SCOPE_BOOKS
import example.spring.boot.security.security.WebSecurityConfigurationTests.SecurityTestController
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestComponent
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

// Actuator endpoint security cannot be testet / simulated here, because how it is configured in the
// WebSecurityConfiguration. Because everything about actuator endpoints can be changed by configuring
// the management properties, using the EndpointRequest matchers does not simply map to "/actuator/**".

// Testing actual Actuator endpoints in this kind of test is also not possible. Actuator does not actually
// use WebMVC and can therefore not be tested by a @WebMvcTest.

// It might actually be better to use an end-2-end smoke test for testing security rules.
// Doing so would keep all web-security tests in one place.

@TestInstance(PER_CLASS)
@WebMvcTest(SecurityTestController::class)
@Import(SecurityTestController::class, WebSecurityConfiguration::class)
internal class WebSecurityConfigurationTests(
    @Autowired private val mockMvc: MockMvc
) {

    /**
     * Because this [RestController] should only be usable in this test, we annotate it with [TestComponent].
     * Doing so will exclude it from any component scanning!
     * Because of that, we'll have to actively import it at the top of the class.
     *
     * If this is classification is not done, there is a fallback mechanism that is not common knowledge and prone to
     * very specific and strange errors: If the component is specified within a test class with at least one test
     * spring will classify it as a test component. But if all the tests of that class are part of `@Nested` classes,
     * spring will no longer classify it as a test component. If that happens the component (e.g. this rest controller)
     * would be picked up by all component scans executed in other tests! This might lead to all kinds of strange
     * behaviour in other tests, while for this test everything would be fine.
     */
    @TestComponent
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
