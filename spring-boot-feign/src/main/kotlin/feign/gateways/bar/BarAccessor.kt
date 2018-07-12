package feign.gateways.bar

import feign.FeignException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class BarAccessor internal constructor(
        private val client: BarClient
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun getMessageOfTheDay(): String? {
        return try {
            client.get().msg
        } catch (e: FeignException) {
            log.error("failed to get message of the day from Bar Service", e)
            null
        }
    }

}