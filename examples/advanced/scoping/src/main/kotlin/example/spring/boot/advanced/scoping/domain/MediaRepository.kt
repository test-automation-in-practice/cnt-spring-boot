package example.spring.boot.advanced.scoping.domain

import java.util.UUID

interface MediaRepository {
    fun add(item: MediaItem): UUID
}
