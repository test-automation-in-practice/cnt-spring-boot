package example.spring.boot.data.jpa.model

import kotlin.annotation.AnnotationTarget.CLASS

@Retention
@Target(CLASS)
annotation class NoArgConstructor
