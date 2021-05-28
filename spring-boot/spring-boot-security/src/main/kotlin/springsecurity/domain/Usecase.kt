package springsecurity.domain

import org.springframework.stereotype.Component
import kotlin.annotation.AnnotationTarget.CLASS

@Retention
@Target(CLASS)
@Component
annotation class Usecase
