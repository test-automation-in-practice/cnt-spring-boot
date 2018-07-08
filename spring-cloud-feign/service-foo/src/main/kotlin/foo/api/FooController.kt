package foo.api

import foo.gateways.bar.BarClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/foo")
class FooController(
        private val barClient: BarClient
) {

    @GetMapping
    fun get(): Map<String, Any?> {
        val barData = barClient.get()
        return mapOf(
                "msg" to barData["msg"],
                "answer" to 42
        )
    }

}