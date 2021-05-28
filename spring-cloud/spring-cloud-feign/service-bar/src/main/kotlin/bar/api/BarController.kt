package bar.api

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/bar")
class BarController {

    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping
    fun get(): Map<String, Any> {
        log.info("received GET request on /bar")
        return mapOf("msg" to "Hello Bar!")
    }

}