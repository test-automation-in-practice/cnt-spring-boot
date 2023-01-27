package example.spring.boot.webmvc.api

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.validation.Validation.buildDefaultValidatorFactory
import jakarta.validation.Validator
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.JsonTest


internal class CreateBookRequestTest {

    @Nested
    @JsonTest
    inner class JsonSerialization(
        @Autowired val objectMapper: ObjectMapper
    ) {

        @Test
        fun `can be deserialized from JSON`() {
            val json = """
                {
                    "title": "Clean Code",
                    "isbn": "9780132350884"
                }
                """
            assertThat(read(json)).isEqualTo(
                CreateBookRequest(title = "Clean Code", isbn = "9780132350884")
            )
        }

        @TestFactory
        fun `certain properties are required when deserializing`() =
            listOf(
                "title" to """{ "isbn": "9780132350884" }""",
                "isbn" to """{ "title": "Clean Code" }"""
            ).map { (name, example) ->
                dynamicTest(name) {
                    assertThat(catchThrowable { read(example) })
                        .hasMessageContaining("value failed for JSON property $name due to missing (therefore NULL)")
                }
            }

        private fun read(@Language("json") json: String) =
            objectMapper.readValue(json, CreateBookRequest::class.java)

    }

    @Nested
    inner class Validation {

        val validator: Validator = buildDefaultValidatorFactory().validator

        @Test
        fun `'isbn' property allows 13 character ISBN`() {
            val request = CreateBookRequest("Clean Code", "9780132350884")
            assertThat(validator.validate(request)).isEmpty()
        }

        @ParameterizedTest
        @ValueSource(strings = ["1234567890", "123456789012", "12345678901234"])
        fun `'isbn' property allows only 13 character ISBN`(isbn: String) {
            val request = CreateBookRequest("My Book", isbn)
            val problems = validator.validate(request).map { it.message }
            assertThat(problems).containsOnly("""must match "[0-9]{13}"""")
        }

    }

}
