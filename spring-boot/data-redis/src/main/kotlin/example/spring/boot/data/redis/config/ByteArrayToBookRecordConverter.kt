package example.spring.boot.data.redis.config

import example.spring.boot.data.redis.persistence.BookRecord
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer

@ReadingConverter
class ByteArrayToBookRecordConverter(private val serializer: GenericJackson2JsonRedisSerializer) :
    Converter<ByteArray, BookRecord> {
    override fun convert(source: ByteArray): BookRecord? =
        serializer.deserialize(source, BookRecord::class.java)
}
