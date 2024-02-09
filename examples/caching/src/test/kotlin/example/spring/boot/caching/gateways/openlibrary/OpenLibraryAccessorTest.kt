package example.spring.boot.caching.gateways.openlibrary

import com.ninjasquad.springmockk.MockkBean
import example.spring.boot.caching.config.CacheConfiguration
import example.spring.boot.caching.gateways.openlibrary.OpenLibraryAccessorTest.CachingTestsVariant2Configuration.Companion.proxiedMock
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
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.annotation.Bean
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
     * In contrast to [CachingTestsVariant2], this setup mocks the dependencies of the
     * [Cacheable] method of the class under test [OpenLibraryAccessor]. It uses calls
     * made to those dependencies in order to verify whether the cache was used or not.
     *
     * This is a pretty easy to understand setup. However, depending on the complexity of
     * the actual implementation it might get complicated real fast. If there are a lot
     * of dependencies or no dependencies at all (e.g. just a very expensive calculation)
     * the alternative approach of [CachingTestsVariant2] might be a better fit.
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
    inner class CachingTestsVariant1(
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

    /**
     * Since caching is a framework feature, it cannot be unit-tested.
     *
     * In this technology integration test the real [CacheConfiguration] is used
     * to verify that the [OpenLibraryAccessor] actually behaves like it should
     * in regard to caching.
     *
     * In contrast to [CachingTestsVariant1], this setup does not require you to mock
     * whatever the cached method does inside its body. Instead, we are going to store
     * make a global variable available for a mocked class under test (in this case
     * the [OpenLibraryAccessor]) and use that mock in a `@Bean` factory method.
     *
     * Spring will take that mock and wrap it inside a caching proxy in order to implement
     * the caching behaviour described with the [Cacheable] annotation. That proxy is then
     * [Autowired] into the test class as the [cut]. Using the global variable, we can still
     * setup mocked behaviour and verify how many times the cached proxy actually delegated
     * to the underlying implementation (our mock).
     *
     * This setup is a bit more complicated, because it requires a deeper understanding of
     * how Spring works. But depending on what the actual implementation of a cached method
     * looks like, this might be the lesser evil. If mocking and tracking the internals is too
     * complicated, setting up a test like this is a good alternative.
     *
     * In this example:
     * - tests would fail if a non-existing cache was referenced
     * - tests would fail if the desired _unless_ condition was wrong
     * - tests would fail if caching would not be enabled by the [CacheConfiguration]
     */
    @Nested
    @SpringBootTest(classes = [CachingTestsVariant2Configuration::class])
    inner class CachingTestsVariant2(
        @Autowired val cut: OpenLibraryAccessor
    ) {

        @BeforeEach
        fun clearCaches(@Autowired caches: List<Cache>) =
            caches.forEach { it.clear() }

        @Test
        fun `caches results if there are any`() {
            every { proxiedMock.getNumberOfPages(isbn1) } returns 308
            every { proxiedMock.getNumberOfPages(isbn2) } returns 464

            repeat(10) {
                assertThat(cut.getNumberOfPages(isbn1)).isEqualTo(308)
                assertThat(cut.getNumberOfPages(isbn2)).isEqualTo(464)
            }

            verify(exactly = 1) { proxiedMock.getNumberOfPages(isbn1) }
            verify(exactly = 1) { proxiedMock.getNumberOfPages(isbn2) }
            confirmVerified(proxiedMock)
        }

        @Test
        fun `does not cache if there was no result`() {
            every { proxiedMock.getNumberOfPages(any()) } returns null

            repeat(10) {
                assertThat(cut.getNumberOfPages(isbn1)).isNull()
            }

            verify(exactly = 10) { proxiedMock.getNumberOfPages(any()) }
            confirmVerified(proxiedMock)
        }

    }

    @Import(CacheConfiguration::class)
    class CachingTestsVariant2Configuration {
        companion object {
            val proxiedMock: OpenLibraryAccessor = mockk()
        }

        @Bean
        fun openLibraryAccessor() = proxiedMock
    }

}
