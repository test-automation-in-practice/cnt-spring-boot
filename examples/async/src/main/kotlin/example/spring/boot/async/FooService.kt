package example.spring.boot.async

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class FooService(
    private val barService: BarService
) {

    @Async
    fun triggerDoingSomething() {
        // do stuff
        barService.doSomething()
        // do more stuff
    }
}
