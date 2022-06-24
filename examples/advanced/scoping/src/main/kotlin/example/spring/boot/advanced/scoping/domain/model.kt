package example.spring.boot.advanced.scoping.domain

import java.util.UUID

enum class TypeOfMedia { BOOK, GAME }

interface MediaItem {
    val id: UUID
    val label: String
}

data class Book(
    override val id: UUID,
    override val label: String
) : MediaItem

data class Game(
    override val id: UUID,
    override val label: String
) : MediaItem
