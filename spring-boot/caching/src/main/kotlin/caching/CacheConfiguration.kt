package caching

import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
class CacheConfiguration {

    @Bean
    fun cacheManager(caches: List<Cache>): CacheManager = SimpleCacheManager().apply { setCaches(caches) }

    @Bean
    fun getNumberOfPagesByIsbnCache(): Cache = ConcurrentMapCache("getNumberOfPagesByIsbn")

}
