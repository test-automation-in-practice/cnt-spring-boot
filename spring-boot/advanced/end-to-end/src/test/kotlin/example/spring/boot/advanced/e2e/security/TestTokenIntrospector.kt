package example.spring.boot.advanced.e2e.security

import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal
import org.springframework.security.oauth2.server.resource.introspection.BadOpaqueTokenException
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector

class TestTokenIntrospector : OpaqueTokenIntrospector {

    private val validTokenMap = mapOf(
        TEST_TOKEN_1 to testAuthenticationPrincipal("user1"),
        TEST_TOKEN_2 to testAuthenticationPrincipal("user2"),
        TEST_TOKEN_3 to testAuthenticationPrincipal("user3"),
        TEST_TOKEN_4 to testAuthenticationPrincipal("user4")
    )

    override fun introspect(token: String): OAuth2AuthenticatedPrincipal =
        validTokenMap[token] ?: throw BadOpaqueTokenException("invalid token: $token")

}
