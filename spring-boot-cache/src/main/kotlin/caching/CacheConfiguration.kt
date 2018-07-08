package caching

import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.concurrent.ConcurrentMapCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CacheConfiguration {

    @Bean fun getFooByBarCache(): Cache = ConcurrentMapCache("getFooByBar")
    @Bean fun fibonacciByNumberCache(): Cache = ConcurrentMapCache("fibonacciByNumber")

    @Bean fun cacheManager(caches: List<Cache>): CacheManager = SimpleCacheManager()
            .apply {
                setCaches(caches)
            }

}