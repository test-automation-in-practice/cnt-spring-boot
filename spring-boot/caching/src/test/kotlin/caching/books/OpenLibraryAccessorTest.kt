package caching.books

import caching.CacheConfiguration
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.Cache
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import

@Import(CacheConfiguration::class)
private class OpenLibraryTestConfiguration {

    @Bean
    fun openLibraryClient(): OpenLibraryClient = mockk()

    @Bean
    fun openLibraryAccessor(openLibraryClient: OpenLibraryClient) = OpenLibraryAccessor(openLibraryClient)

}

@SpringBootTest(classes = [OpenLibraryTestConfiguration::class])
internal class OpenLibraryAccessorTest(
    @Autowired val caches: List<Cache>,
    @Autowired val client: OpenLibraryClient,
    @Autowired val cut: OpenLibraryAccessor
) {

    @BeforeEach
    fun clearCaches() = caches.forEach { it.clear() }

    @BeforeEach
    fun resetMocks() = clearAllMocks()

    @Nested
    @DisplayName("getting number of pages for an ISBN")
    inner class GetNumberOfPages {

        @Test
        fun `returns whatever the OpenLibrary service returned`() {
            every { client.getNumberOfPages("9780132350884") } returns 464
            val numberOfPages = cut.getNumberOfPages("9780132350884")
            assertThat(numberOfPages).isEqualTo(464)
        }

        @Test
        fun `OpenLibrary service is only called once per ISBN - results are cached`() {
            every { client.getNumberOfPages("9780132350884") } returns 464
            every { client.getNumberOfPages("9780134494166") } returns null

            repeat(10) {
                assertThat(cut.getNumberOfPages("9780132350884")).isEqualTo(464)
                assertThat(cut.getNumberOfPages("9780134494166")).isNull()
            }

            verify(exactly = 1) { client.getNumberOfPages("9780132350884") }
            verify(exactly = 1) { client.getNumberOfPages("9780134494166") }
            confirmVerified(client)
        }

    }

}
