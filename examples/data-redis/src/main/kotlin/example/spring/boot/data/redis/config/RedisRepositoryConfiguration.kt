package example.spring.boot.data.redis.config

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import example.spring.boot.data.redis.persistence.BookRecord
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.redis.core.convert.RedisCustomConversions
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer

@Configuration
class RedisRepositoryConfiguration {

    @Bean
    fun redisCustomConversions(): RedisCustomConversions {
        val converters = listOf(
            BookRecordToByteArrayConverter,
            ByteArrayToBookRecordConverter
        )
        return RedisCustomConversions(converters)
    }

    @WritingConverter
    object BookRecordToByteArrayConverter : Converter<BookRecord, ByteArray> {
        override fun convert(source: BookRecord) = bookRecordJsonSerializer.serialize(source)
    }

    @ReadingConverter
    object ByteArrayToBookRecordConverter : Converter<ByteArray, BookRecord> {
        override fun convert(source: ByteArray) = bookRecordJsonSerializer.deserialize(source)
    }

    companion object {
        val bookRecordJsonSerializer = Jackson2JsonRedisSerializer(jacksonObjectMapper(), BookRecord::class.java)
    }
}
