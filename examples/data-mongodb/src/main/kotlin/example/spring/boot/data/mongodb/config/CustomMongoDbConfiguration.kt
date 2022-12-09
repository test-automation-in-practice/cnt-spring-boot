package example.spring.boot.data.mongodb.config

import com.mongodb.MongoClientSettings.Builder
import example.spring.boot.data.mongodb.model.Isbn
import example.spring.boot.data.mongodb.model.Title
import org.bson.UuidRepresentation.STANDARD
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.mongodb.core.convert.MongoCustomConversions
import java.util.concurrent.TimeUnit.MINUTES

@Configuration
class CustomMongoDbConfiguration : MongoClientSettingsBuilderCustomizer {

    override fun customize(builder: Builder) {
        builder
            .uuidRepresentation(STANDARD) // use UUID4
            .applyToConnectionPoolSettings { pool ->
                pool.maxConnectionIdleTime(1, MINUTES)
                pool.minSize(0)
                pool.maxSize(10)
            }
            .retryReads(false)
            .retryWrites(false)
    }

    @Bean
    fun mongoCustomConversions() =
        MongoCustomConversions(
            listOf(
                TitleToStringConverter,
                StringToTitleConverter,
                IsbnToStringConverter,
                StringToIsbnConverter
            )
        )

    @WritingConverter
    object TitleToStringConverter : Converter<Title, String> {
        override fun convert(source: Title): String = source.value
    }

    @ReadingConverter
    object StringToTitleConverter : Converter<String, Title> {
        override fun convert(source: String) = Title(source)
    }

    @WritingConverter
    object IsbnToStringConverter : Converter<Isbn, String> {
        override fun convert(source: Isbn): String = source.value
    }

    @ReadingConverter
    object StringToIsbnConverter : Converter<String, Isbn> {
        override fun convert(source: String) = Isbn(source)
    }

}
