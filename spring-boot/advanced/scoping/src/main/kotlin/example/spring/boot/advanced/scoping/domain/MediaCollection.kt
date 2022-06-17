package example.spring.boot.advanced.scoping.domain

import org.slf4j.LoggerFactory.getLogger
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class MediaCollection(
    private val repository: MediaRepository
) {

    private val log = getLogger(javaClass)

    @Async
    fun register(item: MediaItem) {
        log.info("registering new ${item::class.simpleName}: ${item.id}")
        val id = repository.add(item)
        log.info("registered ${item::class.simpleName} ${item.id} with media ID $id.")
    }

}
