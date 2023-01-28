package example.spring.boot.resilience

import org.springframework.stereotype.Component

@Component
class Tripwire {
    fun possiblyThrowException() = Unit
    fun possiblyWait() = Unit
}
