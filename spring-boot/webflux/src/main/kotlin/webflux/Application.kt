package webflux

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.hateoas.config.EnableHypermediaSupport
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType.HAL

@SpringBootApplication
@EnableHypermediaSupport(type = [HAL])
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
