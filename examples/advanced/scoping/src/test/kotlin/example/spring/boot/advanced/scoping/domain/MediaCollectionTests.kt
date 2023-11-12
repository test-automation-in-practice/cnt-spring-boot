package example.spring.boot.advanced.scoping.domain

import example.spring.boot.advanced.scoping.Examples.book_bobiverse1
import example.spring.boot.advanced.scoping.Examples.book_bobiverse2
import io.github.logrecorder.api.LogRecord
import io.github.logrecorder.assertion.LogRecordAssertion.Companion.assertThat
import io.github.logrecorder.assertion.containsExactly
import io.github.logrecorder.junit5.RecordLoggers
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.util.UUID.fromString

internal class MediaCollectionTests {

    val repository: MediaRepository = mockk {
        every { add(any()) } returns fromString("73b49ca8-aef5-4e59-82ff-98c8d9ec8cd4")
    }
    val cut = MediaCollection(repository)

    @Test
    fun `registration adds items to the repository`() {
        cut.register(book_bobiverse1)
        verify { repository.add(book_bobiverse1) }
    }

    @Test
    @RecordLoggers(MediaCollection::class)
    fun `registration is logged`(log: LogRecord) {
        cut.register(book_bobiverse2)
        assertThat(log) containsExactly {
            info("registering new Book: aadfe61a-4bbd-44c4-85ba-6bddbe7d10a2")
            info("registered Book aadfe61a-4bbd-44c4-85ba-6bddbe7d10a2 with media ID 73b49ca8-aef5-4e59-82ff-98c8d9ec8cd4.")
        }
    }
}
