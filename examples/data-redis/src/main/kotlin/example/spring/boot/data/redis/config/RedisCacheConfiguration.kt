package example.spring.boot.data.redis.config

import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.interceptor.CacheErrorHandler
import org.springframework.cache.interceptor.LoggingCacheErrorHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder
import java.time.Duration.ofMinutes

@Configuration
@EnableCaching
@ImportAutoConfiguration(CacheAutoConfiguration::class, RedisAutoConfiguration::class)
class RedisCacheConfiguration : RedisCacheManagerBuilderCustomizer {

    override fun customize(builder: RedisCacheManagerBuilder) {
        builder
            .withCacheConfiguration(
                /* cacheName = */ "getNumberOfPagesByIsbn",
                /* cacheConfiguration = */ defaultCacheConfig()
                    .entryTtl(ofMinutes(60))
                    .disableCachingNullValues()
            )
            .disableCreateOnMissingCache()
    }

    @Bean // if Redis is not available, this will just log an error and skip the cache
    fun cacheErrorHandler(): CacheErrorHandler = LoggingCacheErrorHandler(true)

}
