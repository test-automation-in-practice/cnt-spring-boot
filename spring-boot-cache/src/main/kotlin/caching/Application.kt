package caching

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@SpringBootApplication
class Application {

    @Configuration
    @EnableCaching
    class CacheConfiguration {

        @Bean fun cacheManager(caches: List<Cache>): CacheManager = SimpleCacheManager().apply { setCaches(caches) }
        @Bean fun getNumberOfPagesByIsbnCache(): Cache = ConcurrentMapCache("getNumberOfPagesByIsbn")

    }

}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}