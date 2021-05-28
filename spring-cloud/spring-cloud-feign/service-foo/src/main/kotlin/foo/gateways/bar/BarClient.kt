package foo.gateways.bar

import feign.hystrix.FallbackFactory
import org.slf4j.LoggerFactory
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping


@FeignClient("service-bar", fallbackFactory = BarClientFallbackFactory::class)
interface BarClient {

    @GetMapping("/bar")
    fun get(): Map<String, Any>

}

object BarClientFallback : BarClient {

    override fun get(): Map<String, Any> {
        return mapOf("msg" to "Hello Fallback!")
    }

}

@Component
class BarClientFallbackFactory : FallbackFactory<BarClient> {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun create(cause: Throwable): BarClient {
        log.error("Fallback triggered because original request failed with an exception: ", cause)
        return BarClientFallback
    }

}