package example.spring.boot.advanced.scoping.admin

import example.spring.boot.advanced.scoping.domain.MediaItem

interface AdminMediaRepository {
    fun getAll(): Sequence<MediaItem>
}
