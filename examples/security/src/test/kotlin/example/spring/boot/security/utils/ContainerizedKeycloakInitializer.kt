package example.spring.boot.security.utils

import dasniko.testcontainers.keycloak.KeycloakContainer
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.TestPropertySourceUtils.addInlinedPropertiesToEnvironment
import kotlin.annotation.AnnotationTarget.CLASS

@Retention
@Target(CLASS)
@DirtiesContext(classMode = AFTER_CLASS)
@ContextConfiguration(initializers = [ContainerizedKeycloakInitializer::class])
annotation class InitializeWithContainerizedKeycloak

class ContainerizedKeycloakInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    companion object {
        // start only once for all tests
        val container: KeycloakContainer by lazy {
            KeycloakContainer()
                .withRealmImportFile("keycloak/realm.json")
                .apply { start() }
        }
    }

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val issuerUri = "${container.authServerUrl}realms/cnt-sb"
        val jwkSetUri = "$issuerUri/protocol/openid-connect/certs"

        val issuerUriProperty = "spring.security.oauth2.resourceserver.jwt.issuer-uri=$issuerUri"
        val jwkSetUriProperty = "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=$jwkSetUri"

        addInlinedPropertiesToEnvironment(applicationContext, issuerUriProperty, jwkSetUriProperty)
    }

}
