package example.spring.boot.data.redis.config

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.convert.RedisCustomConversions
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer

@Configuration
class RedisRepositoryConfiguration {

    @Bean
    fun redisCustomConversions(): RedisCustomConversions {
        val serializer = GenericJackson2JsonRedisSerializer(jacksonObjectMapper())
        val converters = listOf(
            BookRecordToByteArrayConverter(serializer),
            ByteArrayToBookRecordConverter(serializer)
        )
        return RedisCustomConversions(converters)
    }
}
