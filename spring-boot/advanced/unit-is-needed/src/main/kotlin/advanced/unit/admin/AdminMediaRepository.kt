package advanced.unit.admin

import advanced.unit.domain.MediaItem

interface AdminMediaRepository {
    fun getAll(): Sequence<MediaItem>
}
