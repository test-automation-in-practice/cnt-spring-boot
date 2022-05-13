package advanced.unit.domain

import java.util.UUID

interface MediaRepository {
    fun add(item: MediaItem): UUID
}
