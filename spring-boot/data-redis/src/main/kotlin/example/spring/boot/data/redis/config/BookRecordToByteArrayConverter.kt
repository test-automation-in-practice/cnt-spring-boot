package example.spring.boot.data.redis.config

import example.spring.boot.data.redis.persistence.BookRecord
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer

@WritingConverter
class BookRecordToByteArrayConverter(private val serializer: GenericJackson2JsonRedisSerializer) :
    Converter<BookRecord, ByteArray> {
    override fun convert(source: BookRecord): ByteArray =
        serializer.serialize(source)
}
