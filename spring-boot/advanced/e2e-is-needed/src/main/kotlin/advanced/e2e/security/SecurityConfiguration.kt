package advanced.e2e.security

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val tokenIntrospector: OpaqueTokenIntrospector
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) = http {
        csrf { disable() }
        headers { cacheControl {} }
        sessionManagement { sessionCreationPolicy = STATELESS }
        oauth2ResourceServer {
            opaqueToken { introspector = tokenIntrospector }
        }
        authorizeRequests {
            authorize("/api/**", hasAuthority(SCOPE_API))
            authorize("/**", denyAll)
        }
    }
    
}
