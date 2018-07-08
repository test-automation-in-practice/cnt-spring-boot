package caching.simple

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/foo")
class FooController(
        private val service: FooService
) {

    @GetMapping
    fun get(@RequestParam("bar") bar: String) = mapOf(
            "foo" to service.getFoo(bar)
    )

}