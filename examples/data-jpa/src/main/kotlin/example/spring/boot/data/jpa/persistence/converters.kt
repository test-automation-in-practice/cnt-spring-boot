package example.spring.boot.data.jpa.persistence

import example.spring.boot.data.jpa.model.Isbn
import example.spring.boot.data.jpa.model.Title
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
internal object TitleConverter : AttributeConverter<Title, String> {
    override fun convertToDatabaseColumn(attribute: Title?) = attribute?.value
    override fun convertToEntityAttribute(dbData: String?) = dbData?.let(::Title)
}

@Converter(autoApply = true)
internal object IsbnConverter : AttributeConverter<Isbn, String> {
    override fun convertToDatabaseColumn(attribute: Isbn?) = attribute?.value
    override fun convertToEntityAttribute(dbData: String?) = dbData?.let(::Isbn)
}
