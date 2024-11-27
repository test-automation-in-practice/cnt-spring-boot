package example.spring.boot.security.security

import example.spring.boot.security.security.Authorities.ROLE_CURATOR
import example.spring.boot.security.security.Authorities.ROLE_USER
import example.spring.boot.security.security.Authorities.SCOPE_ACTUATOR
import example.spring.boot.security.security.Authorities.SCOPE_BOOKS
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.boot.actuate.health.HealthEndpoint
import org.springframework.boot.actuate.info.InfoEndpoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.core.convert.converter.Converter
import org.springframework.security.config.annotation.web.HttpSecurityDsl
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

// `@EnableWebSecurity` actually already declares `@Configuration` as
// and acts as a composite annotation. But since this is an abnormality which
// is not usually found in `@EnableX` type annotations it might be confusing
// to not have `@Configuration` declared here ..

// Side Note: This inheritance of `@Configuration` when using
// `@EnableWebSecurity` might actually trip you up when using it
// in configurations which are intended to be used by, and therefore declared
// in or near, test classes when combined with using `@ComponentScan` in your
// tests.

// See also https://github.com/spring-projects/spring-security/issues/4426

@Configuration
@EnableWebSecurity
class WebSecurityConfiguration {

    private val passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    @Bean
    @Order(1)
    fun actuatorSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            securityMatcher(EndpointRequest.toAnyEndpoint())
            applyDefaults()

            httpBasic {}
            authorizeHttpRequests {
                authorize(EndpointRequest.to(InfoEndpoint::class.java, HealthEndpoint::class.java), permitAll)
                authorize(EndpointRequest.toAnyEndpoint(), hasAuthority(SCOPE_ACTUATOR))
                authorize(anyRequest, denyAll)
            }
        }
        return http.build()
    }

    @Bean
    @Order(2)
    fun generalSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            securityMatcher("/**")
            applyDefaults()

            oauth2ResourceServer { jwt { jwtAuthenticationConverter = CustomJwtAuthenticationConverter } }
            authorizeHttpRequests {
                authorize("/api/books/**", hasAuthority(SCOPE_BOOKS))
                authorize("/error", permitAll)
                authorize(anyRequest, denyAll)
            }
        }
        return http.build()
    }

    private fun HttpSecurityDsl.applyDefaults() {
        cors { disable() }
        csrf { disable() }
        sessionManagement {
            sessionCreationPolicy = STATELESS
        }
    }

    @Bean
    fun userDetailService(): UserDetailsService =
        InMemoryUserDetailsManager(
            user("user"),
            user("actuator", SCOPE_ACTUATOR)
        )

    private fun user(username: String, vararg authorities: String) = User.withUsername(username)
        .password(passwordEncoder.encode(username.reversed()))
        .authorities(*authorities)
        .build()

    object CustomJwtAuthenticationConverter : Converter<Jwt, JwtAuthenticationToken> {

        @Suppress("UNCHECKED_CAST")
        override fun convert(source: Jwt): JwtAuthenticationToken {
            val realmAccess = source.claims["realm_access"] as? Map<String, Any>
            val roles = realmAccess?.get("roles") as? Collection<String> ?: emptyList()

            val authorities = roles
                .flatMap { role ->
                    when (role) {
                        "user" -> listOf(SCOPE_BOOKS, ROLE_USER)
                        "curator" -> listOf(SCOPE_BOOKS, ROLE_CURATOR)
                        else -> emptyList()
                    }
                }
                .map(::SimpleGrantedAuthority)
                .toSet()

            return JwtAuthenticationToken(source, authorities)
        }

    }

}
