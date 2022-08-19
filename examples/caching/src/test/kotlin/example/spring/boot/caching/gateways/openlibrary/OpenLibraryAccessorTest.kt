package example.spring.boot.caching.gateways.openlibrary

import com.ninjasquad.springmockk.MockkBean
import example.spring.boot.caching.config.CacheConfiguration
import io.mockk.clearAllMocks
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.Cache
import org.springframework.context.annotation.Import
import java.io.IOException

internal class OpenLibraryAccessorTest {

    val isbn1 = "9781680680584"
    val isbn2 = "9780132350884"

    @BeforeEach
    fun resetMocks() = clearAllMocks()

    /**
     * The correct functionality of our own code is verified using unit-tests.
     */
    @Nested
    inner class FunctionalTests {

        val client: OpenLibraryClient = mockk()
        val cut = OpenLibraryAccessor(client)

        @Test
        fun `invokes invokes client and returns number of pages if found`() {
            every { client.getNumberOfPages(isbn1) } returns 308
            val actual = cut.getNumberOfPages(isbn1)
            assertThat(actual).isEqualTo(308)
        }

        @Test
        fun `invokes invokes client and returns null if null was returned`() {
            every { client.getNumberOfPages(isbn1) } returns null
            val actual = cut.getNumberOfPages(isbn1)
            assertThat(actual).isNull()
        }

        @Test
        fun `invokes invokes client and returns null on IOExeption`() {
            every { client.getNumberOfPages(isbn1) } throws IOException("oops")
            val actual = cut.getNumberOfPages(isbn1)
            assertThat(actual).isNull()
        }

    }

    /**
     * Since caching is a framework feature, it cannot be unit-tested.
     *
     * In this technology integration test the real [CacheConfiguration] is used
     * to verify that the [OpenLibraryAccessor] actually behaves like it should
     * in regard to caching.
     *
     * In this example:
     * - tests would fail if a non-existing cache was referenced
     * - tests would fail if the desired _unless_ condition was wrong
     * - tests would fail if caching would not be enabled by the [CacheConfiguration]
     */
    @Nested
    @Import(OpenLibraryAccessor::class)
    @MockkBean(OpenLibraryClient::class)
    @SpringBootTest(classes = [CacheConfiguration::class])
    inner class CachingTests(
        @Autowired val client: OpenLibraryClient,
        @Autowired val cut: OpenLibraryAccessor
    ) {

        @BeforeEach
        fun clearCaches(@Autowired caches: List<Cache>) =
            caches.forEach { it.clear() }

        @Test
        fun `caches results if there are any`() {
            every { client.getNumberOfPages(isbn1) } returns 308
            every { client.getNumberOfPages(isbn2) } returns 464

            repeat(10) {
                assertThat(cut.getNumberOfPages(isbn1)).isEqualTo(308)
                assertThat(cut.getNumberOfPages(isbn2)).isEqualTo(464)
            }

            verify(exactly = 1) { client.getNumberOfPages(isbn1) }
            verify(exactly = 1) { client.getNumberOfPages(isbn2) }
            confirmVerified(client)
        }

        @Test
        fun `does not cache if there was no result`() {
            every { client.getNumberOfPages(any()) } returns null

            repeat(10) {
                assertThat(cut.getNumberOfPages(isbn1)).isNull()
            }

            verify(exactly = 10) { client.getNumberOfPages(any()) }
            confirmVerified(client)
        }

    }

}
