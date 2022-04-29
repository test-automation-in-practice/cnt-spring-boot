package advanced.e2e.security

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@WebMvcTest(TestRestController::class)
@Import(TestTokenIntrospector::class)
internal class SecurityConfigurationTests(
    @Autowired val mockMvc: MockMvc
) {

    @Test
    fun `api namespace can be accessed with known token`() {
        mockMvc.get("/api/test") {
            headers { setBearerAuth(TEST_TOKEN_1) }
        }.andExpect {
            status { isOk() }
            content { string("api-endpoint") }
        }
    }

    @Test
    fun `api namespace cannot be accessed at all with unknown token`() {
        mockMvc.get("/api/test") {
            headers { setBearerAuth("374cbf90-98c2-4cfc-8fa3-85514e196b15") }
        }.andExpect {
            status { isUnauthorized() }
            content { string("") }
        }
    }

    @Test
    fun `other namespace is forbidden even for known token`() {
        mockMvc.get("/other/test") {
            headers { setBearerAuth(TEST_TOKEN_1) }
        }.andExpect {
            status { isForbidden() }
            content { string("") }
        }
    }

    @Test
    fun `other namespace cannot be accessed at all unknown token`() {
        mockMvc.get("/other/test") {
            headers { setBearerAuth("374cbf90-98c2-4cfc-8fa3-85514e196b15") }
        }.andExpect {
            status { isUnauthorized() }
            content { string("") }
        }
    }

}


@RestController
class TestRestController {

    @GetMapping("/api/test")
    fun apiEndpoint() = "api-endpoint"

    @GetMapping("/other/test")
    fun otherEndpoint() = "other-endpoint"

}
