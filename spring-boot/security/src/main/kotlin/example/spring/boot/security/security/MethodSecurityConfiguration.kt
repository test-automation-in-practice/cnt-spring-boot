package example.spring.boot.security.security

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration

// `@EnableGlobalMethodSecurity` actually already declares `@Configuration` as
// and acts as a composite annotation. But since this is an abnormality which
// is not usually found in `@EnableX` type annotations it might be confusing
// to not have `@Configuration` declared here ..

// Side Note: This inheritance of `@Configuration` when using
// `@EnableGlobalMethodSecurity` might actually trip you up when using it
// in configurations which are intended to be used by, and therefore declared
// in or near, test classes when combined with using `@ComponentScan` in your
// tests.

// See also https://github.com/spring-projects/spring-security/issues/4426

@Configuration
@EnableGlobalMethodSecurity(
    jsr250Enabled = true,
    prePostEnabled = true,
    securedEnabled = true
)
class MethodSecurityConfiguration : GlobalMethodSecurityConfiguration()
