package caching.simple

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.util.*

@Service
class FooService(
        private val remoteService: RemoteFooService
) {

    @Cacheable("getFooByBar")
    fun getFoo(bar: String): String {
        return remoteService.getFoo(bar)
    }

}

interface RemoteFooService {
    fun getFoo(bar: String): String
}

@Component
class HostSimulator9000 : RemoteFooService {

    override fun getFoo(bar: String): String {
        Thread.sleep(5_000)
        return Base64.getEncoder().encodeToString(bar.toByteArray())
    }

}