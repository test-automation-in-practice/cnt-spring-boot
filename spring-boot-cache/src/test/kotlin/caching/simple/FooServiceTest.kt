package caching.simple

import caching.CacheConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
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

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [FooServiceTest.TestConfiguration::class])
internal class FooServiceTest {

    @EnableCaching
    @Import(CacheConfiguration::class)
    class TestConfiguration {
        @Bean fun fooService(remoteService: RemoteFooService) = FooService(remoteService)
    }

    @MockBean lateinit var remoteService: RemoteFooService
    @Autowired lateinit var cut: FooService
    @Autowired lateinit var caches: List<Cache>

    @BeforeEach fun clearCaches() = caches.forEach { it.clear() }

    @Test fun `service returns whatever remote service returns`() {
        given(remoteService.getFoo("some-bar")).willReturn("abc123")
        val foo = cut.getFoo("some-bar")
        assertThat(foo).isEqualTo("abc123")
    }

    @Test fun `remote service is only called once per bar - results are cached`() {
        given(remoteService.getFoo("some-bar")).willReturn("abc123")
        given(remoteService.getFoo("another-bar")).willReturn("123abc")

        (1..10).forEach {
            assertThat(cut.getFoo("some-bar")).isEqualTo("abc123")
            assertThat(cut.getFoo("another-bar")).isEqualTo("123abc")
        }

        verify(remoteService, times(1)).getFoo("some-bar")
        verify(remoteService, times(1)).getFoo("another-bar")
        verifyNoMoreInteractions(remoteService)
    }

}