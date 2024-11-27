package example.spring.boot.advanced.e2e.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector
import org.springframework.security.web.SecurityFilterChain

internal const val SCOPE_API = "SCOPE_API"

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val tokenIntrospector: OpaqueTokenIntrospector
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            headers { cacheControl {} }
            sessionManagement { sessionCreationPolicy = STATELESS }
            oauth2ResourceServer {
                opaqueToken { introspector = tokenIntrospector }
            }
            authorizeHttpRequests {
                authorize("/api/**", hasAuthority(SCOPE_API))
                authorize("/error", permitAll)
                authorize("/**", denyAll)
            }
        }
        return http.build()
    }

}
