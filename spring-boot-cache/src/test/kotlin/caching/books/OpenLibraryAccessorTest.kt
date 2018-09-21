package caching.books

import caching.Application
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.cache.Cache
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension

@Import(Application.CacheConfiguration::class)
internal class OpenLibraryTestConfiguration {
    @Bean fun openLibraryAccessor(client: OpenLibraryClient) = OpenLibraryAccessor(client)
}

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [OpenLibraryTestConfiguration::class])
internal class OpenLibraryAccessorTest(
        @Autowired var caches: List<Cache>,
        @Autowired var cut: OpenLibraryAccessor
) {

    @MockBean lateinit var client: OpenLibraryClient

    @BeforeEach fun clearCaches() = caches.forEach { it.clear() }

    @DisplayName("getting number of pages for an ISBN")
    @Nested inner class GetNumberOfPages {

        @Test fun `returns whatever the OpenLibrary service returned`() {
            given(client.getNumberOfPages("9780132350884")).willReturn(464)
            val numberOfPages = cut.getNumberOfPages("9780132350884")
            assertThat(numberOfPages).isEqualTo(464)
        }

        @Test fun `OpenLibrary service is only called once per ISBN - results are cached`() {
            given(client.getNumberOfPages("9780132350884")).willReturn(464)
            given(client.getNumberOfPages("9780134494166")).willReturn(null)

            (1..10).forEach {
                assertThat(cut.getNumberOfPages("9780132350884")).isEqualTo(464)
                assertThat(cut.getNumberOfPages("9780134494166")).isNull()
            }

            verify(client, times(1)).getNumberOfPages("9780132350884")
            verify(client, times(1)).getNumberOfPages("9780134494166")
            verifyNoMoreInteractions(client)
        }

    }

}