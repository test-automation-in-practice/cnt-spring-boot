package example.spring.boot.data.redis.config

import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.cache.annotation.EnableCaching
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

}
