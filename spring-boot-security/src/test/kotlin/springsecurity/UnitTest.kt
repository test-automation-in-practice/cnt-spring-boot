package springsecurity

import org.junit.jupiter.api.Tag
import kotlin.annotation.AnnotationTarget.CLASS

@Retention
@Target(CLASS)
@Tag("unit-test")
annotation class UnitTest
