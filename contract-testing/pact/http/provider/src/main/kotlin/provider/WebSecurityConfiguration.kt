package provider

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager

// Simple in-memory basic-auth security configuration for added complexity of the contracts.
// Namely that the API requires authentication, while the contracts should not include any credentials.

@Configuration
@EnableWebSecurity
class WebSecurityConfiguration : WebSecurityConfigurerAdapter() {

    private val passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    override fun configure(http: HttpSecurity) {
        http {
            cors { disable() }
            csrf { disable() }
            httpBasic {}
            authorizeRequests {
                authorize("/books/**", hasRole("USER"))
                authorize("/**", denyAll)
            }
        }
    }

    @Bean
    fun inMemoryUserDetailsManager(): InMemoryUserDetailsManager =
        InMemoryUserDetailsManager()
            .apply {
                createUser(user(name = "user", "USER"))
                createUser(user(name = "admin", "ADMIN"))
            }

    private fun user(name: String, vararg roles: String) =
        User.withUsername(name)
            .password(passwordEncoder.encode(name.reversed()))
            .roles(*roles)
            .build()

    @Bean
    fun passwordEncoder(): PasswordEncoder = passwordEncoder

}
