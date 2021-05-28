package web.api

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.JsonTest
import javax.validation.Validation.buildDefaultValidatorFactory
import javax.validation.Validator


internal class CreateBookRequestTest {

    @JsonTest
    @Nested inner class JsonSerialization(
        @Autowired val objectMapper: ObjectMapper
    ) {

        @Test fun `can be deserialized from JSON`() {
            val json = """
                {
                    "title": "Clean Code",
                    "isbn": "9780132350884"
                }
                """
            assertThat(read(json)).isEqualTo(
                CreateBookRequest(
                    title = "Clean Code",
                    isbn = "9780132350884"
                )
            )
        }

        @Test fun `'title' property is required when deserializing`() {
            assertThrows<JsonProcessingException> {
                read("""{ "isbn": "9780132350884" }""")
            }
        }

        @Test fun `'isbn' property is required when deserializing`() {
            assertThrows<JsonProcessingException> {
                read("""{ "title": "Clean Code" }""")
            }
        }

        private fun read(json: String) = objectMapper.readValue(json, CreateBookRequest::class.java)

    }

    @Nested inner class Validation {

        val validator: Validator = buildDefaultValidatorFactory().validator

        @Test fun `'isbn' property allows 13 character ISBN`() {
            val request = CreateBookRequest("Clean Code", "9780132350884")
            assertThat(validator.validate(request)).isEmpty()
        }

        @ValueSource(strings = ["1234567890", "123456789012", "12345678901234"])
        @ParameterizedTest fun `'isbn' property allows only 13 character ISBN`(isbn: String) {
            val request = CreateBookRequest("My Book", isbn)
            val problems = validator.validate(request).map { it.message }
            assertThat(problems).containsOnly("""must match "[0-9]{13}"""")
        }

    }

}