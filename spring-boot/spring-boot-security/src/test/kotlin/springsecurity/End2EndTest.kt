package springsecurity

import org.junit.jupiter.api.Tag
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import kotlin.annotation.AnnotationTarget.CLASS

@Retention
@Target(CLASS)
@Tag("end2end-test")
@DirtiesContext
@ActiveProfiles("test", "end2end-test")
annotation class End2EndTest
