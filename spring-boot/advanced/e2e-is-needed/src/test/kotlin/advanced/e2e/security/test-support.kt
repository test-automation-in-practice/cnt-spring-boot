package advanced.e2e.security

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType.BEARER
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal
import java.time.Instant.now

const val TEST_TOKEN_1 = "91325c1c-91e5-4fa4-afe9-55e917b303b5"
const val TEST_TOKEN_2 = "895413fa-7634-4dcc-a1d1-88e7dc71b5c2"
const val TEST_TOKEN_3 = "0dfbefbe-7b0d-470a-bc97-c4aebe148658"
const val TEST_TOKEN_4 = "2b9991c0-56bb-4ea0-a7de-1b6fa504c639"

internal fun testAuthenticationPrincipal(name: String = "user"): OAuth2AuthenticatedPrincipal =
    OAuth2IntrospectionAuthenticatedPrincipal(
        /* name = */ name,
        /* attributes = */ mapOf("foo" to "bar"),
        /* authorities = */ listOf(SimpleGrantedAuthority(SCOPE_API))
    )

internal fun testAuthentication(token: String): BearerTokenAuthentication =
    BearerTokenAuthentication(
        /* principal = */ testAuthenticationPrincipal(),
        /* credentials = */ OAuth2AccessToken(BEARER, token, now(), now().plusSeconds(60)),
        /* authorities = */ emptyList()
    )

internal fun setSecurityContext(token: String) {
    SecurityContextHolder.getContext().authentication = testAuthentication(token)
}

internal fun clearSecurityContext() {
    SecurityContextHolder.clearContext()
}
