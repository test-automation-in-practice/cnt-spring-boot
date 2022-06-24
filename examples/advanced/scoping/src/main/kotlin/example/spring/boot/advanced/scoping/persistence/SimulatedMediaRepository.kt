package example.spring.boot.advanced.scoping.persistence

import example.spring.boot.advanced.scoping.admin.AdminMediaRepository
import example.spring.boot.advanced.scoping.domain.MediaItem
import example.spring.boot.advanced.scoping.domain.MediaRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class SimulatedMediaRepository : MediaRepository, AdminMediaRepository {

    private val database = mutableMapOf<UUID, MediaItem>()

    override fun add(item: MediaItem): UUID {
        Thread.sleep(500) // simulate work
        val id = UUID.randomUUID()
        database[id] = item
        return id
    }

    override fun getAll(): Sequence<MediaItem> = database.values.asSequence()

}
