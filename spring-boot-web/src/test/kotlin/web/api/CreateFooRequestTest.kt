package web.api

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.OffsetDateTime.now
import java.time.OffsetDateTime.parse
import javax.validation.Validation.*
import javax.validation.Validator


internal class CreateFooRequestTest {

    @JsonTest
    @ExtendWith(SpringExtension::class)
    @Nested inner class JsonSerialization {

        @Autowired lateinit var objectMapper: ObjectMapper

        @Test fun `can be deserialized from JSON`() {
            val json = """
                {
                    "bar": "Hello World!",
                    "xur": "2018-07-12T12:34:56.789Z"
                }
                """
            assertThat(read(json)).isEqualTo(CreateFooRequest(
                    bar = "Hello World!",
                    xur = parse("2018-07-12T12:34:56.789Z")
            ))
        }

        @Test fun `'bar' property is required when deserializing`() {
            assertThrows<JsonProcessingException> {
                read("""{ "xur": "2018-07-12T12:34:56.789Z" }""")
            }
        }

        @Test fun `'xur' property is required when deserializing`() {
            assertThrows<JsonProcessingException> {
                read("""{ "bar": "Hello World!" }""")
            }
        }

        private fun read(json: String) = objectMapper.readValue(json, CreateFooRequest::class.java)

    }

    @Nested inner class Validation {

        val validator: Validator = buildDefaultValidatorFactory().validator

        @ValueSource(strings = ["Hello World!", "Hello Kotlin!", "Hello Universe!"])
        @ParameterizedTest fun `'bar' property allows 'Hello {}!' messages`(bar: String) {
            val request = CreateFooRequest(bar, now())
            val problems = validator.validate(request)
            assertThat(problems).isEmpty()
        }

        @ValueSource(strings = ["Hello World", "Hello !", "Hello!"])
        @ParameterizedTest fun `'bar' property allows only 'Hello {}!' messages`(bar: String) {
            val request = CreateFooRequest(bar, now())
            val problems = validator.validate(request).map { it.message }
            assertThat(problems).containsOnly("""must match "Hello .+!"""")
        }

    }

}