package example.spring.boot.basics.persistence

import example.spring.boot.basics.books.core.BookRepositoryContract
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

internal class InMemoryBookRepositoryTest : BookRepositoryContract {
    private val clock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
    override val cut = InMemoryBookRepository(clock)
    override fun now(): Instant = clock.instant()
}
