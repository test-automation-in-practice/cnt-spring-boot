package springsecurity

import org.junit.jupiter.api.Tag
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import kotlin.annotation.AnnotationTarget.CLASS

@Retention
@Target(CLASS)
@Tag("integration-test")
@DirtiesContext
@ActiveProfiles("test", "integration-test")
annotation class IntegrationTest
