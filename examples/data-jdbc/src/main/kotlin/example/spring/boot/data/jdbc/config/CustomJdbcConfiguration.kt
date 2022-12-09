package example.spring.boot.data.jdbc.config

import example.spring.boot.data.jdbc.model.Isbn
import example.spring.boot.data.jdbc.model.Title
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration

@Configuration
class CustomJdbcConfiguration : AbstractJdbcConfiguration() {

    override fun jdbcCustomConversions(): JdbcCustomConversions =
        JdbcCustomConversions(
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
